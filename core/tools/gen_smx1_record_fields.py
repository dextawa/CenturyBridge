"""fieldRenames for classes 1.20.2 made records (step 1.20.1 -> 1.20.2): a 1.20.1 private field -> the record component
its constructor parameter is stored into, read off both constructors' bytecode (javap -c -p).

A pair is emitted only when the 1.20.1 class has one constructor and 1.20.2's record its canonical one with the same
parameter types in the same order, 1.20.1's constructor stores parameter i (unchanged: load, putfield) into the field
and 1.20.2's into the component, both of the same descriptor, and 1.20.2 no longer declares the 1.20.1 field. A record
component holds the value the constructor was given, as the 1.20.1 field did; a mixin's shadow / accessor / FIELD
anchor on the old field then names the component. Owners: every class the step's mixin damage table names (targets
and anchor owners). Keys other fragments already wire are left to them.

  python3 core/tools/gen_smx1_record_fields.py <damage-mixin tsv(.gz)> core/segments/shims-1.20.2.json.d/smx1-recordfields-gen.json
      (from the repository root; javap on PATH; V / W code jars from $CB_DATA/jars, CB_DATA defaulting to data)
"""
import glob
import gzip
import json
import os
import re
import subprocess
import sys

V, W = "1.20.1", "1.20.2"
JAR = os.environ.get("CB_DATA", "data") + "/jars/client-%s-intermediary-code.jar"
WIRING = "core/segments/shims-1.20.2.json"


def owners(table):
    op = gzip.open if table.endswith(".gz") else open
    out = set()
    with op(table, "rt", encoding="utf-8") as f:
        head = next(f).rstrip("\n").split("\t")
        ix = {n: i for i, n in enumerate(head)}
        for line in f:
            r = line.rstrip("\n").split("\t")
            if len(r) < len(head):
                continue
            out.add(r[ix["owner"]])
            m = re.search(r"L(net/minecraft/[\w$/]+);", r[ix["anchor"]]) if "anchor" in ix else None
            if m:
                out.add(m.group(1))
    return sorted(o for o in out if o.startswith("net/minecraft/"))


def javap(ver, classes):
    """class -> javap -c -p text, in batches (one JVM per batch)"""
    out = {}
    for i in range(0, len(classes), 200):
        batch = classes[i:i + 200]
        txt = subprocess.run(["javap", "-c", "-p", "-cp", JAR % ver] + [c.replace("/", ".") for c in batch],
                             capture_output=True, text=True, encoding="utf-8", errors="replace").stdout
        for part in re.split(r"\n(?=Compiled from |(?:public |final |abstract |)*(?:class|interface|enum) )", txt):
            m = re.search(r"(?:class|interface|enum) (net\.minecraft\.[\w$.]+)", part)
            if m:
                out.setdefault(m.group(1).replace(".", "/"), "")
                out[m.group(1).replace(".", "/")] += part
    return out


def ctors(txt, cls):
    """[(param list text, [(param index, field, desc)])] for each constructor"""
    res = []
    name = re.escape(cls.replace("/", "."))
    for m in re.finditer(r"\n  (?:public |private |protected )?" + name + r"\((.*?)\);\n    Code:\n(.*?)(?:\n\n|\n}|$)", txt, re.S):
        params = [p.strip() for p in m.group(1).split(",")] if m.group(1).strip() else []
        slots, s = {}, 1
        for i, p in enumerate(params):
            slots[s] = i
            s += 2 if p in ("long", "double") else 1
        stores, last = [], None
        for line in m.group(2).split("\n"):
            lm = re.search(r": ([aifld])load(?:_| +)(\d+)\s*$", line)
            if lm:
                last = int(lm.group(2))
                continue
            pm = re.search(r": putfield .*// Field (\w+):(\S+)", line)
            if pm and last in slots:
                stores.append((slots[last], pm.group(1), pm.group(2)))
            last = None if not lm else last
        res.append((params, stores))
    return res


def wired():
    keys = set()
    for p in [WIRING] + sorted(glob.glob(WIRING + ".d/*.json")):
        if p.endswith("smx1-recordfields-gen.json"):
            continue
        keys |= set(json.load(open(p, encoding="utf-8")).get("fieldRenames", {}))
    return keys


def main():
    table, out = sys.argv[1], sys.argv[2]
    cls = owners(table)
    wtxt = javap(W, cls)
    recs = [c for c in cls if "extends java.lang.Record" in wtxt.get(c, "")]
    vtxt = javap(V, recs)
    have = wired()
    res = {}
    for c in recs:
        v, w = vtxt.get(c, ""), wtxt.get(c, "")
        vc, wc = ctors(v, c), ctors(w, c)
        if len(vc) != 1 or not vc[0][1]:
            continue
        canon = [x for x in wc if x[0] == vc[0][0]]
        if len(canon) != 1:
            continue
        wfields = set(re.findall(r" (\w+);\n", w))
        wstore = {i: (f, d) for i, f, d in canon[0][1]}
        for i, f, d in vc[0][1]:
            if f in wfields or i not in wstore or wstore[i][1] != d:
                continue
            key = f"{c}.{f}:{d}"
            if key not in have:
                res[key] = wstore[i][0]
    root = {"_smx1_recordfields_basis": "gen_smx1_record_fields.py (both constructors' bytecode); regenerate, never hand-edit",
            "fieldRenames": dict(sorted(res.items()))}
    with open(out, "w", encoding="utf-8", newline="\n") as o:
        json.dump(root, o, indent=1)
        o.write("\n")
    print(f"{len(res)} record fields over {len(recs)} classes made records (of {len(cls)} owners) -> {out}")


if __name__ == "__main__":
    main()
