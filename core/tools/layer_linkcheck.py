"""layer_linkcheck.py: the climbed CenturyBridge layers' link-check, counted, ratcheted and attributed.

A climbed layer is judged on its target T by link-check (climb_runtime.sh, layer_linkcheck_steps.sh). A finding
whose bucket is CLAIMED / BLIND / KNOWN is a reference the JVM cannot link on T ("dangling"); KNOWN TOMBSTONE rows
are calls the converter replaced with a lazy failure ("tombstoned"): both crash where reached.

  python3 layer_linkcheck.py ratchet <link-findings.tsv> <T> <baseline.tsv> [--write]
      per layer: dangling + tombstoned at T against the baseline's row for (T, layer). A layer above its baseline
      (or with no row: baseline 0) fails (exit 1). --write replaces T's rows with what was measured (lowering the
      baseline after a burn-down is a regenerate; raising it is a decision the commit has to state).
  python3 layer_linkcheck.py steps <dir> <T> <baseline.tsv> <attributed.tsv>
      <dir>/<R>/link-findings.tsv for every release R a layer was link-checked at (layer_linkcheck_steps.sh):
      writes every R's rows into the baseline, and lists T's dangling / tombstoned rows with the step where each
      started dangling: the first R of the unbroken run of releases ending at T in which the same finding stands.
      Findings are compared in one spelling: intermediary names translated through the wall table of the climb's
      wall step (26.1) and 26.2's class moves (core/segments/steps/26.1.2__26.2.json.d classRenames); a finding no
      earlier release has in that spelling is attributed to T's own step.

The layer of a jar is its own release W: centurybridge-0.3.0+<W>[+<T>].jar.
"""
import glob
import json
import os
import re
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
sys.path.insert(0, HERE)

RELEASES = ["1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6", "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4",
            "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11", "26.1", "26.1.1", "26.1.2", "26.2"]
JAR = re.compile(r"^centurybridge-0\.3\.0\+([0-9.]+?)(?:\+[0-9.]+)?\.jar$")
HEADER = ("# The climbed CenturyBridge layers' link-check on each target T: per layer (its own release W), the references\n"
          "# the JVM cannot link (CLAIMED/BLIND/KNOWN) and the calls tombstoned by the converter. climb_runtime.sh fails\n"
          "# when a layer's dangling + tombstoned at T rises above its row. Generated (core/tools/layer_linkcheck.py,\n"
          "# layer_linkcheck_steps.sh / CB_LAYER_BASELINE=write): lowering it is a regenerate, never a hand edit.\n"
          "# T\tlayer\tdangling\ttombstoned\n")


def rows(path):
    out = []
    with open(path, encoding="utf-8") as f:
        next(f, None)
        for line in f:
            r = line.rstrip("\n").split("\t")
            if len(r) >= 5 and r[0] in ("CLAIMED", "BLIND", "KNOWN"):
                m = JAR.match(r[2])
                if m:
                    out.append((m.group(1), r))
    return out


def counts(path):
    c = {}
    for layer, r in rows(path):
        d = c.setdefault(layer, [0, 0])
        d[1 if r[1] == "TOMBSTONE" else 0] += 1
    return c


def read_baseline(path):
    b = {}
    if os.path.exists(path):
        for line in open(path, encoding="utf-8"):
            if line.startswith("#") or not line.strip():
                continue
            t, layer, d, tb = line.rstrip("\n").split("\t")[:4]
            b[(t, layer)] = (int(d), int(tb))
    return b


def write_baseline(path, b):
    key = lambda k: (RELEASES.index(k[0]) if k[0] in RELEASES else 99, RELEASES.index(k[1]) if k[1] in RELEASES else 99)
    with open(path, "w", encoding="utf-8", newline="\n") as o:
        o.write(HEADER)
        for k in sorted(b, key=key):
            o.write(f"{k[0]}\t{k[1]}\t{b[k][0]}\t{b[k][1]}\n")


def ratchet(findings, t, baseline, write):
    b = read_baseline(baseline)
    c = counts(findings)
    measured = set(c)  # a layer with no finding has no row: its baseline is 0
    bad = []
    for layer in sorted(measured | {k[1] for k in b if k[0] == t}, key=lambda x: RELEASES.index(x) if x in RELEASES else 99):
        d, tb = c.get(layer, (0, 0))
        bd, btb = b.get((t, layer), (0, 0))
        if d + tb > bd + btb:
            bad.append(f"layer {layer} on {t}: {d} dangling + {tb} tombstoned = {d + tb}, baseline {bd} + {btb} = {bd + btb}")
    if write:
        for k in [k for k in b if k[0] == t]:
            del b[k]
        for layer in measured:
            b[(t, layer)] = tuple(c.get(layer, (0, 0)))
        write_baseline(baseline, b)
        print(f"baseline {baseline}: rows for {t} rewritten ({len(measured)} layers)")
        return 0
    for line in bad:
        print("RATCHET " + line)
    if bad:
        print(f"climbed layers -> {t}: {len(bad)} layer(s) above the baseline ({baseline}); fix the new references, "
              "or regenerate the baseline (CB_LAYER_BASELINE=write) and say why in the commit")
        return 1
    print(f"climbed layers -> {t}: within the baseline ({baseline})")
    return 0


class Spelling:
    """one spelling for findings on both sides of the wall"""
    TOKEN = re.compile(r"net/minecraft/class_\d+(?:\$class_\d+)*|\b(?:method|field|comp)_\d+\b")

    def __init__(self, data):
        sys.path.insert(0, HERE)
        import wall_tables
        self.cls, self.mem = {}, {}
        for line in open(wall_tables.table_for("26.1", data), encoding="utf-8"):
            f = line.rstrip("\n").split("\t")
            if len(f) < 3:
                continue
            if f[0] == "C":
                self.cls[f[1]] = f[2]
            elif f[0] in ("M", "F"):
                self.mem[f[1].rsplit(".", 1)[1]] = f[2].rsplit(".", 1)[1]
        self.moves = {}
        for p in glob.glob(os.path.join(ROOT, "core/segments/steps/26.1.2__26.2.json.d/*.json")):
            for k, v in json.load(open(p, encoding="utf-8")).get("classRenames", {}).items():
                if isinstance(v, str):
                    self.moves[k] = v
        self.movere = re.compile("|".join(re.escape(k) for k in sorted(self.moves, key=len, reverse=True)) + r"(?=[;.$\s(]|$)") \
            if self.moves else None

    def __call__(self, s, release):
        s = self.TOKEN.sub(lambda m: self.cls.get(m.group(0), self.mem.get(m.group(0), m.group(0))), s)
        if self.movere is not None and RELEASES.index(release) < RELEASES.index("26.2"):
            s = self.movere.sub(lambda m: self.moves[m.group(0)], s)
        return s


def steps(d, t, baseline, out):
    b = read_baseline(baseline)
    measured = [r for r in RELEASES if os.path.exists(os.path.join(d, r, "link-findings.tsv"))]
    for r in measured:
        f = os.path.join(d, r, "link-findings.tsv")
        c = counts(f)
        for k in [k for k in b if k[0] == r]:
            del b[k]
        for layer in c:
            b[(r, layer)] = tuple(c.get(layer, (0, 0)))
    write_baseline(baseline, b)
    print(f"baseline {baseline}: rows for {len(measured)} releases ({measured[0]} .. {measured[-1]})")
    if t not in measured:
        raise SystemExit(f"no {d}/{t}/link-findings.tsv")
    data = os.environ.get("CB_DATA", "data")
    sp = Spelling(data)
    present = {}
    for r in measured:
        keys = set()
        for layer, row in rows(os.path.join(d, r, "link-findings.tsv")):
            keys.add((layer, row[1], sp(row[3], r), sp(row[4], r)))
        present[r] = keys
    ti = measured.index(t)
    per_step = {}
    with open(out, "w", encoding="utf-8", newline="\n") as o:
        o.write(f"# climbed layers' dangling / tombstoned references on {t}, each with the step where it started dangling\n"
                "# (layer_linkcheck.py steps: the first release of the unbroken run ending at {t} in which the same finding\n"
                "# stands, names compared through the wall table). Owner: that step's owner.\n".replace("{t}", t))
        o.write("step\tbucket\tkind\tlayer jar\twhere\tref\tnote\n")
        for layer, row in sorted(rows(os.path.join(d, t, "link-findings.tsv")), key=lambda x: (RELEASES.index(x[0]), x[1][1], x[1][3])):
            key = (layer, row[1], sp(row[3], t), sp(row[4], t))
            i = ti
            while i > 0 and key in present[measured[i - 1]] and RELEASES.index(measured[i - 1]) == RELEASES.index(measured[i]) - 1:
                i -= 1
            r = measured[i]
            step = (RELEASES[RELEASES.index(r) - 1] + "__" + r) if r != layer else r + " (layer built there)"
            per_step[step] = per_step.get(step, 0) + 1
            o.write(step + "\t" + "\t".join(row[:6] + [""] * (6 - len(row[:6]))) + "\n")
    for s in sorted(per_step, key=lambda x: RELEASES.index(x.split("__")[-1].split(" ")[0])):
        print(f"  {s}: {per_step[s]}")
    print(f"{sum(per_step.values())} rows on {t} attributed -> {out}")


def main(a):
    if len(a) >= 4 and a[0] == "ratchet":
        return ratchet(a[1], a[2], a[3], "--write" in a[4:])
    if len(a) == 5 and a[0] == "steps":
        steps(a[1], a[2], a[3], a[4])
        return 0
    print(__doc__)
    return 2


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
