"""The library probe's input: V's library classes a step's library damage table names, as one jar.

  python3 core/tools/lib_probe_jar.py <damage-lib/V__W.tsv> <artifacts.tsv> <V> <librariesDir> <out.jar> [artifactRegex]

Every class named by a row (the class of a CLASS/KIND/FINAL row, the owner a member row goes
through) plus its enclosing classes, read from the jar of V's own version of the row's artifact
(artifacts.tsv). Main api-probe then touches every public/protected member of these classes in
every way a mod can; converted one step and link-checked on W, its sites are the step's library
damage as a mod meets it.
"""
import os
import re
import sys
import zipfile


def main():
    damage, artifacts, v, libdir, out = sys.argv[1:6]
    regex = re.compile(sys.argv[6]) if len(sys.argv) > 6 and sys.argv[6] else None
    versions = {}
    for line in open(artifacts, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) == 4 and f[0] == v:
            versions[f[1]] = f[2]
    want = {}
    for line in open(damage, encoding="utf-8"):
        if line.startswith("#") or line.startswith("artifact\t"):
            continue
        f = line.rstrip("\n").split("\t")
        if regex and not regex.search(f[0]):
            continue
        want.setdefault(f[0], set()).add(f[3].split(".", 1)[0])  # internal names have no dots
    n = 0
    with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as zo:
        for art, classes in sorted(want.items()):
            ver = versions[art]
            d = os.path.join(libdir, *art.split("/"), ver)
            jar = os.path.join(d, f"{art.split('/')[-1]}-{ver}.jar")
            if not os.path.exists(jar):
                print(f"missing {jar}", file=sys.stderr)
                continue
            names = set()
            for c in classes:
                names.add(c)
                while "$" in c:
                    c = c[:c.rfind("$")]
                    names.add(c)
            with zipfile.ZipFile(jar) as zi:
                have = set(zi.namelist())
                for c in sorted(names):
                    e = c + ".class"
                    if e in have:
                        zo.writestr(e, zi.read(e))
                        n += 1
    print(f"lib-probe classes: {n} -> {out}")


if __name__ == "__main__":
    main()
