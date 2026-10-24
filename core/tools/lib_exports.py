"""Which library damage rows are in packages the library itself declares internal.

  python3 core/tools/lib_exports.py <damage-lib/V__W.tsv> <artifacts.tsv> <V> <librariesDir> [--rows]

A library jar that declares its public packages -- an OSGi bundle's Export-Package manifest header -- says
every other package is internal. For each artifact of the table this prints the declaration
found in V's jar, and for each row whether its class's package is exported. --rows prints one line per row
not exported (artifact, package, symbol): candidates for an "internal to the library" judgment, with the
declaration as evidence. An artifact that declares nothing is never judged here.
"""
import os
import re
import sys
import zipfile


def manifest_exports(z):
    try:
        mf = z.read("META-INF/MANIFEST.MF").decode("utf-8", "replace")
    except KeyError:
        return None
    # continuation lines start with one space
    joined = re.sub(r"\r?\n ", "", mf)
    for line in joined.splitlines():
        if line.startswith("Export-Package:"):
            body = line[len("Export-Package:"):]
            pkgs, depth, cur = [], 0, ""
            for ch in body:  # split on commas outside quotes
                if ch == '"':
                    depth ^= 1
                if ch == "," and not depth:
                    pkgs.append(cur)
                    cur = ""
                else:
                    cur += ch
            pkgs.append(cur)
            return {p.split(";")[0].strip().replace(".", "/") for p in pkgs if p.strip()}
    return None


def main():
    damage, artifacts, v, libdir = sys.argv[1:5]
    show_rows = "--rows" in sys.argv
    versions = {}
    for line in open(artifacts, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) == 4 and f[0] == v:
            versions[f[1]] = f[2]
    rows = [l.rstrip("\n").split("\t") for l in open(damage, encoding="utf-8")
            if not l.startswith("#") and not l.startswith("artifact\t")]
    decl = {}
    for art in sorted({r[0] for r in rows}):
        ver = versions.get(art)
        jar = os.path.join(libdir, *art.split("/"), ver, f"{art.split('/')[-1]}-{ver}.jar") if ver else None
        if not jar or not os.path.exists(jar):
            print(f"{art}: no jar", file=sys.stderr)
            continue
        with zipfile.ZipFile(jar) as z:
            exp = manifest_exports(z)
            how = "OSGi Export-Package of its manifest"
        decl[art] = (exp, how, ver)
        print(f"{art} {ver}: " + (f"{len(exp)} packages exported ({how})" if exp is not None else "declares no exports"))
    internal = 0
    for r in rows:
        art, sym = r[0], r[3]
        cls = sym.split(".", 1)[0]
        pkg = cls[:cls.rfind("/")]
        exp = decl.get(art, (None,))[0]
        if exp is not None and pkg not in exp:
            internal += 1
            if show_rows:
                print(f"INTERNAL\t{art}\t{pkg}\t{r[2]}\t{sym}")
    print(f"{internal} of {len(rows)} rows in packages their library does not export")


if __name__ == "__main__":
    main()
