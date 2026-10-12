"""Our intermediary stub against the loader's own remapped game jar: every class's member names.

CB builds its compile stubs with Main remap-jar (obfuscated -> intermediary, tiny mappings). The
loader remaps the same jar itself at launch (.fabric/remappedJars/.../client-intermediary.jar) --
what a mod actually runs against. A member our remapper names differently is a wrong row in every
table measured against the stub (ChestBoat.clearItemStacks named after Boat's private controlBoat).

  python stub_vs_loader.py <our stub jar> <loader's remapped jar> [out.tsv]
"""
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402


def members(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith(("net/minecraft/", "com/mojang/")):
                c = classfile.read(z.read(n))
                if c:
                    out[c.name] = ({m.name + m.desc for m in c.methods}, {f[0] + ":" + f[1] for f in c.fields})
    return out


def main():
    ours, loader = members(sys.argv[1]), members(sys.argv[2])
    rows = []
    for cls in sorted(set(ours) | set(loader)):
        if cls not in ours or cls not in loader:
            rows.append((cls, "class", "only in " + ("ours" if cls in ours else "loader's"), ""))
            continue
        (om, of), (lm, lf) = ours[cls], loader[cls]
        for x in sorted(om - lm):
            rows.append((cls, "method", "ours", x))
        for x in sorted(lm - om):
            rows.append((cls, "method", "loader's", x))
        for x in sorted(of - lf):
            rows.append((cls, "field", "ours", x))
        for x in sorted(lf - of):
            rows.append((cls, "field", "loader's", x))
    print(f"{len(ours)} / {len(loader)} classes; {len(rows)} differences")
    for r in rows[:40]:
        print("\t".join(r))
    if len(sys.argv) > 3:
        with open(sys.argv[3], "w", encoding="utf-8", newline="\n") as f:
            f.write("class\tkind\tside\tmember\n")
            for r in rows:
                f.write("\t".join(r) + "\n")


if __name__ == "__main__":
    main()
