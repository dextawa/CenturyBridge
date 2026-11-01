"""Evidence of use for an access-widener damage table (core/segments/damage-aw): a column only, the table itself
stays corpus-independent (api-probe -Dcb.apiprobe.nonpublic lists every non-public member of every game class).

For every jar of a corpus (nested jars included) it reads the access widener its fabric.mod.json names and the
mixin refmaps (the targets @Accessor / @Invoker / @Shadow and injectors name, "Lowner;name(desc)" or
"Lowner;name:desc"), and appends to each damage row:
  used   aw:<mods whose AW names the member or, for a class line, the owner> mixin:<mods whose refmaps name it>
A member is matched by owner, name and descriptor (a refmap target names its descriptor; one naming another overload
of the same name is no evidence for this row, nor is a descriptor-less one). Owners and names are matched as the table spells them (V's intermediary names; the corpus is 1.20.1-era, so a member
V added after 1.20.1 has no evidence by construction -- read 0 there as "no 1.20.1 mod", not "no mod").

  python aw_evidence.py <corpus dir> <damage.tsv> [<damage.tsv>...]     (rewrites each table with the column)
"""
import io
import json
import os
import re
import sys
import zipfile

REF = re.compile(r"L([\w/$]+);([\w$<>]+)(\([^)\s\"]*\)[^\s\"]*|:[^\s\"]+)?")


def scan(data, mod, aw_members, aw_classes, mx_members):
    try:
        z = zipfile.ZipFile(io.BytesIO(data))
    except zipfile.BadZipFile:
        return
    names = z.namelist()
    aw_path = None
    if "fabric.mod.json" in names:
        try:
            o = json.loads(z.read("fabric.mod.json").decode("utf-8", "replace"), strict=False)
            if isinstance(o, dict) and isinstance(o.get("accessWidener"), str):
                aw_path = o["accessWidener"].lstrip("/")
        except ValueError:
            pass
    if aw_path and aw_path in names:
        for line in z.read(aw_path).decode("utf-8", "replace").splitlines():
            t = line.split("#", 1)[0].split()
            if len(t) >= 3 and t[1] == "class":
                aw_classes.setdefault(t[2], set()).add(mod)
            elif len(t) >= 5 and t[1] in ("method", "field"):
                aw_members.setdefault((t[2], t[3], t[4]), set()).add(mod)
    for n in names:
        if n.endswith(".jar"):
            scan(z.read(n), mod, aw_members, aw_classes, mx_members)
        elif n.endswith(".json") and "refmap" in n:
            for m in REF.finditer(z.read(n).decode("utf-8", "replace")):
                d = (m.group(3) or "").lstrip(":")
                if d:
                    mx_members.setdefault((m.group(1), m.group(2), d), set()).add(mod)


def main():
    corpus, tables = sys.argv[1], sys.argv[2:]
    aw_members, aw_classes, mx_members = {}, {}, {}
    for f in sorted(os.listdir(corpus)):
        if f.endswith(".jar"):
            with open(os.path.join(corpus, f), "rb") as h:
                scan(h.read(), f[:-4], aw_members, aw_classes, mx_members)
    for t in tables:
        rows = open(t, encoding="utf-8").read().splitlines()
        hdr = rows[0].split("\t")
        if hdr[-1] == "used":
            rows = ["\t".join(r.split("\t")[:-1]) for r in rows]
        out = [rows[0] + "\tused"]
        hit = 0
        for r in rows[1:]:
            c = r.split("\t")
            owner, name, desc = c[1], c[2], c[3]
            aw = aw_members.get((owner, name, desc), set()) | aw_classes.get(owner, set())
            mx = mx_members.get((owner, name, desc), set())
            hit += bool(aw or mx)
            out.append(r + f"\taw:{len(aw)} mixin:{len(mx)}")
        with open(t, "w", encoding="utf-8", newline="\n") as o:
            o.write("\n".join(out) + "\n")
        print(f"{os.path.basename(t)}: {len(rows) - 1} rows, {hit} with evidence of use")


if __name__ == "__main__":
    main()
