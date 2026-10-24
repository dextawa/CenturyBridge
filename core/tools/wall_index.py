"""The mixin probe's index in the names past the namespace wall: owner, target and anchor of each row
translated through the wall table (inter-to-mojang-1.21.11-26.1.tsv), so mixin-judge can find the row's V
site in 1.21.11's code renamed through the same table (--source-index; the original index keeps the keys).

  python wall_index.py <wall table.tsv> <index.tsv> <out.tsv>

Members are translated by their owner's table entry, else by name alone (intermediary method_/field_/comp_
names are unique across classes); descriptors class by class. A name the table lacks stays as it is (the
converter leaves it so too).
"""
import re
import sys


def main():
    table, index, out = sys.argv[1:4]
    cls, mem, by_name = {}, {}, {}
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) < 3:
            continue
        if f[0] == "C":
            cls[f[1]] = f[2]
        elif f[0] in ("M", "F"):
            mem[f[1]] = f[2].rsplit(".", 1)[1]
            by_name.setdefault(f[1].rsplit(".", 1)[1], set()).add(f[2].rsplit(".", 1)[1])

    def c(name):
        if name in cls:
            return cls[name]
        d = name.rfind("$")
        return c(name[:d]) + name[d:] if d > 0 else name

    def desc(d):
        return re.sub(r"L([^;]+);", lambda m: "L" + c(m.group(1)) + ";", d)

    def m(owner, name):
        if owner + "." + name in mem:
            return mem[owner + "." + name]
        alts = by_name.get(name)
        return next(iter(alts)) if alts and len(alts) == 1 else name

    with open(index, encoding="utf-8") as f, open(out, "w", encoding="utf-8", newline="\n") as o:
        o.write(next(f))
        for line in f:
            r = line.rstrip("\n").split("\t")
            owner = r[3]
            t = r[4]
            if ":" in t and "(" not in t:
                n, d = t.split(":", 1)
                r[4] = m(owner, n) + ":" + desc(d)
            elif "(" in t:
                p = t.index("(")
                n = t[:p]
                r[4] = (n if n.startswith("<") else m(owner, n)) + desc(t[p:])
            a = r[5]
            k = a.split(" ", 1)
            if k[0] in ("INVOKE", "FIELD") and len(k) > 1:
                rest = k[1]
                ao = rest[1:rest.index(";")]
                tail = rest[rest.index(";") + 1:]
                if k[0] == "INVOKE":
                    p = tail.index("(")
                    n = tail[:p]
                    r[5] = "INVOKE L" + c(ao) + ";" + (n if n.startswith("<") else m(ao, n)) + desc(tail[p:])
                else:
                    n, d = tail.split(":", 1)
                    r[5] = "FIELD L" + c(ao) + ";" + m(ao, n) + ":" + desc(d)
            elif k[0] == "NEW" and len(k) > 1:
                r[5] = "NEW " + c(k[1])
            r[3] = c(owner)
            o.write("\t".join(r) + "\n")


if __name__ == "__main__":
    main()
