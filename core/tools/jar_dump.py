"""A stub dump of a mojang-named jar in the text form gen_frontier_segment.py reads:

  C <class>
  M <owner>.<name><desc>
  F <owner>.<name>:<desc>

(Main stub-dump writes only the M lines; a segment measured against those knows no class and no field
as alive.)

  python3 jar_dump.py <jar> <out.txt>
"""
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402


def main():
    jar, out = sys.argv[1:3]
    n = 0
    with zipfile.ZipFile(jar) as z, open(out, "w", encoding="utf-8", newline="\n") as o:
        for e in sorted(z.namelist()):
            if not e.endswith(".class"):
                continue
            k = classfile.read(z.read(e))
            if not k:
                continue
            n += 1
            o.write(f"C {k.name}\n")
            for m in k.methods:
                o.write(f"M {k.name}.{m.name}{m.desc}\n")
            for f in k.fields:
                o.write(f"F {k.name}.{f[0]}:{f[1]}\n")
    print(f"{n} classes -> {out}")


if __name__ == "__main__":
    main()
