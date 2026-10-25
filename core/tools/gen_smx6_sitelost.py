"""mixinLedger rows of step 1.20.6 -> 1.21 (smx6) whose V site 1.21 deleted: count-sites-lost / ordinal-site-lost rows where
every lost V occurrence of the anchor stands in a block of V's body that W deleted outright.

  [CB_DATA=<main checkout>/data] python3 core/tools/gen_smx6_sitelost.py <cause-mixin.tsv> core/segments/steps/1.20.6__1.21.json.d/smx6-sitelost-gen.json

For each (member, anchor) of those rows it reads both bodies (javap -c -p of data/jars/client-<ver>-intermediary-code.jar),
aligns their instruction sequences (difflib over opcode + member / constant operand) and keeps the anchor only when
  - V has n occurrences, W has m < n, and exactly n - m of V's lie in "delete" regions (V instructions with nothing in their
    place in W, the code on both sides aligned), the others in aligned regions;
  - every open row of the anchor is the unordinaled one or names one of those deleted V occurrences;
  - the anchor does not occur in any method of W's class that the W method calls (depth 2) nor in any lambda body of the
    class, and W's other methods of the class hold no more occurrences of it than V's did (code moved there -- a static
    field's initializer, a sibling method -- is moved, not deleted).
The reason names the deleted V offsets. Regenerate, never hand-edit; keys another fragment already ledgers are skipped.
"""
import csv
import difflib
import glob
import json
import os
import re
import subprocess
import sys

STEP = "1.20.6__1.21"
DATA = os.environ.get("CB_DATA", "data")  # a worktree reads the main checkout's data/ (read only)
JARS = {"V": f"{DATA}/jars/client-1.20.6-intermediary-code.jar", "W": f"{DATA}/jars/client-1.21-intermediary-code.jar"}
_cache = {}


def javap(side, cls):
    k = (side, cls)
    if k not in _cache:
        r = subprocess.run(["javap", "-c", "-p", "-cp", JARS[side], cls.replace("/", ".")], capture_output=True)
        _cache[k] = r.stdout.decode("utf-8", "replace").split("\n") if r.returncode == 0 else []
    return _cache[k]


def methods(lines, cls):
    out = {}
    cur = None
    simple = cls.split("/")[-1]
    for line in lines:
        if line.startswith("  ") and not line.startswith("   ") and ("(" in line or line.strip() == "static {};"):
            if line.strip() == "static {};":
                name = "<clinit>"
            else:
                m = re.search(r"([\w$<>]+)\(", line)
                name = m.group(1) if m else "?"
                if name == simple or name.endswith("." + simple):
                    name = "<init>"
            cur = [line]
            out.setdefault(name, []).append(cur)
        elif cur is not None:
            if line.strip() == "":
                cur = None
            else:
                cur.append(line)
    return out


def arity(desc):
    return len(re.findall(r"\[*(?:L[^;]+;|[ZBCSIJFD])", desc[1:desc.index(")")]))


def pick(side, cls, target):
    bodies = methods(javap(side, cls), cls).get(target.split("(")[0], [])
    if len(bodies) == 1:
        return bodies[0]
    n = arity(target[target.index("("):])
    c = []
    for b in bodies:
        ps = b[0][b[0].index("(") + 1:b[0].index(")")].strip()
        if (0 if not ps else ps.count(",") + 1) == n:
            c.append(b)
    return c[0] if len(c) == 1 else None


def code(body):
    out = []
    for line in body[1:]:
        m = re.match(r"\s+(\d+): (\w+)\s*(.*)", line)
        if not m:
            continue
        op, rest = m.group(2), m.group(3)
        c = rest.split("//", 1)[1].strip() if "//" in rest else ""
        if op.startswith(("invoke", "get", "put", "new", "instanceof", "checkcast", "anewarray")):
            sig = op + " " + c
        elif op.startswith("ldc"):
            sig = "ldc " + c
        elif op in ("bipush", "sipush"):
            sig = op + " " + rest.strip()
        else:
            sig = op
        out.append((int(m.group(1)), sig))
    return out


def matches(sig, anchor):
    kind, rest = anchor.split(" ", 1)
    if kind == "INVOKE":
        o, n, d = re.match(r"L([^;]+);([^(]+)(\(.*)", rest).groups()
        return sig.startswith("invoke") and (f"{o}.{n}:{d}" in sig or f"Method {n}:{d}" in sig)
    if kind == "FIELD":
        o, n, d = re.match(r"L([^;]+);([^:]+):(.*)", rest).groups()
        return sig.startswith(("get", "put")) and (f"{o}.{n}:{d}" in sig or f"Field {n}:{d}" in sig)
    if kind == "NEW":
        return sig.startswith("new ") and sig.endswith("class " + rest)
    if kind == "CONSTANT":
        t, v = rest.split("=", 1)
        if t == "intValue":
            iv = int(v)
            short = {-1: "iconst_m1", 0: "iconst_0", 1: "iconst_1", 2: "iconst_2", 3: "iconst_3", 4: "iconst_4", 5: "iconst_5"}
            return sig == short.get(iv) or sig in (f"bipush {iv}", f"sipush {iv}", f"ldc int {iv}")
        if t == "floatValue":
            fv = float(v)
            return (fv in (0.0, 1.0, 2.0) and sig == f"fconst_{int(fv)}") or (
                sig.startswith("ldc float ") and float(sig.split()[-1].rstrip("f")) == fv)
        if t == "doubleValue":
            dv = float(v)
            return (dv in (0.0, 1.0) and sig == f"dconst_{int(dv)}") or (
                sig.startswith("ldc double ") and float(sig.split()[-1].rstrip("d")) == dv)
        if t == "stringValue":
            return sig == "ldc String " + v
    return False


def moved(cls, wbody, anchor, vbody):
    ms = methods(javap("W", cls), cls)
    names, frontier = set(), [wbody]
    for _ in range(2):
        nxt = []
        for b in frontier:
            for line in b:
                m = re.search(r"(?:Method|InterfaceMethod) (?:" + re.escape(cls) + r"\.)?([\w$<>]+):", line)
                if m and m.group(1) not in names and m.group(1) in ms:
                    names.add(m.group(1))
                    nxt.extend(ms[m.group(1)])
        frontier = nxt
    names.update(n for n in ms if n.startswith("lambda$"))
    if any(matches(s, anchor) for n in names for b in ms[n] for _, s in code(b)):
        return True
    # moved elsewhere in the class (a static field's initializer, a sibling method): some other method of W's class holds
    # more occurrences of the anchor than V's method of that name did
    vms = methods(javap("V", cls), cls)

    def count(bodies):
        return sum(1 for b in bodies for _, s in code(b) if matches(s, anchor))
    for n, bs in ms.items():
        rest = [b for b in bs if b is not wbody]
        if count(rest) > count([b for b in vms.get(n, []) if b is not vbody[0]]):
            return True
    return False


def ledgered_elsewhere(root):
    keys = set()
    for f in glob.glob(os.path.join(root, f"core/segments/steps/{STEP}.json.d/*.json")):
        if f.endswith("smx6-sitelost-gen.json"):
            continue
        keys.update(json.load(open(f, encoding="utf-8")).get("mixinLedger", {}).keys())
    return keys


def main():
    src, out = sys.argv[1], sys.argv[2]
    have = ledgered_elsewhere(".")
    groups = {}
    for r in csv.DictReader(open(src, encoding="utf-8"), delimiter="\t"):
        if r["cause"].split(":")[0] in ("count-sites-lost", "ordinal-site-lost") and r["anchor"]:
            groups.setdefault((r["owner"], r["target"], r["anchor"]), []).append(int(r["ordinal"]))
    led = {}
    for (o, t, a), ords in sorted(groups.items()):
        key = f"{o}.{t} @ {a}"
        if key in have or o in have or f"{o}.{t}" in have:
            continue
        bv, bw = pick("V", o, t), pick("W", o, t)
        if not bv or not bw:
            continue
        cv, cw = code(bv), code(bw)
        sm = difflib.SequenceMatcher(None, [s for _, s in cv], [s for _, s in cw], autojunk=False)
        region = {}
        for tag, i1, i2, j1, j2 in sm.get_opcodes():
            for i in range(i1, i2):
                region[i] = (tag, i1, i2)
        vs = [i for i, (_, s) in enumerate(cv) if matches(s, a)]
        nw = sum(1 for _, s in cw if matches(s, a))
        gone = [(k, i) for k, i in enumerate(vs) if region[i][0] == "delete"]
        if not gone or len(vs) - nw != len(gone) or nw >= len(vs):
            continue
        if not all(x == -1 or x in {k for k, _ in gone} for x in ords):
            continue
        if moved(o, bw, a, [bv]):
            continue
        sites = "; ".join(f"V site #{k} at offset {cv[i][0]} in the block of offsets {cv[region[i][1]][0]}-{cv[region[i][2] - 1][0]} "
                          f"({region[i][2] - region[i][1]} instructions)" for k, i in gone)
        led[key] = (f"V site deleted: {sites} has nothing in its place in 1.21's body, the code on both sides aligned (javap of both "
                    f"bodies, instruction sequences aligned); V has {len(vs)} occurrences, W {nw}, the others aligned one for one; the anchor "
                    f"is in no method the W method calls (depth 2) nor any lambda of the class, and no other method of the class gained it")
    root = {"_smx6_sitelost_basis": "gen_smx6_sitelost.py over cause-mixin.tsv and javap of both code jars; regenerate, never hand-edit",
            "mixinLedger": led}
    json.dump(root, open(out, "w", encoding="utf-8", newline="\n"), indent=1, ensure_ascii=False)
    print(len(led), "anchors ->", out)


if __name__ == "__main__":
    main()
