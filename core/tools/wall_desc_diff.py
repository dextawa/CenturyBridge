"""The DESC_CHANGED half of the wall segment: members of a 1.21.11 class that the target still has
under the wall table's name, with another descriptor.

gen_frontier_segment.py records what is gone across the wall; a member whose descriptor changed looks
alive to it. augment_frontier_segment.py takes those from a stub-diff of the 1.21.11 jar renamed into
mojang names against the target -- and the rename it needs is the one the wall table makes. This is
that diff, straight from the table and the two jars, in augment_frontier_segment.py's input format
(kind TAB fate TAB owner.name TAB old desc TAB new desc), the old side named the way the table names it:

  python3 wall_desc_diff.py <wall table.tsv> <V intermediary jar> <W jar> <out diff.tsv>
"""
import os
import re
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class"):
                k = classfile.read(z.read(n))
                if k:
                    out[k.name] = k
    return out


def main():
    table, vjar, wjar, out = sys.argv[1:5]
    C, MF = {}, {}
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3:
            if f[0] == "C":
                C[f[1]] = f[2]
            else:
                MF[f[1]] = f[2][f[2].rindex(".") + 1:]

    def tc(c):
        if c in C:
            return C[c]
        d = c.rfind("$")
        return tc(c[:d]) + c[d:] if d > 0 else c

    def td(d):
        return re.sub(r"L([^;]+);", lambda m: "L" + tc(m.group(1)) + ";", d)

    V, W = load(vjar), load(wjar)
    rows = []
    for c, k in V.items():
        w = W.get(tc(c))
        if w is None:
            continue
        for kind, vs, ws in (("method", k.methods, w.methods), ("field", k.fields, w.fields)):
            wby = {}
            for m in ws:
                nm, ds = (m.name, m.desc) if kind == "method" else (m[0], m[1])
                wby.setdefault(nm, set()).add(ds)
            for m in vs:
                nm, ds = (m.name, m.desc) if kind == "method" else (m[0], m[1])
                if nm.startswith("<"):
                    continue
                n = MF.get(c + "." + nm, nm)
                nd = td(ds)
                have = wby.get(n)
                if have and nd not in have:
                    rows.append((kind, "DESC_CHANGED", w.name + "." + n, nd, sorted(have)[0]))
    with open(out, "w", encoding="utf-8", newline="\n") as o:
        o.write("kind\tfate\tsymbol\tdesc\tdetail\n")
        for r in sorted(rows):
            o.write("\t".join(r) + "\n")
    print(f"{len(rows)} DESC_CHANGED members -> {out}")


if __name__ == "__main__":
    main()
