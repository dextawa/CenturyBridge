"""Every intermediary class name with its Mojang name: the naming table the step tools filter areas by.

inter-to-mojang-1.21.11.tsv only knows classes that still exist in 1.21.11; a class that died on the
way (1.20.2 removed every loot serializer class) keeps its class_NNNN name there and falls outside
every area. This joins Mojang's client mappings with Fabric's intermediary for every release of the
intermediary era, newest first, so each class gets the Mojang name of the last release that had it.

  python class_names.py <mappingsDir> <out.tsv>
reads <mappingsDir>/intermediary-<v>.tiny and mojang-<v>.txt (downloads the Mojang ones it lacks,
without keeping them) and writes "C<TAB>intermediary<TAB>mojang<TAB>last release" lines.
"""
import json
import os
import re
import sys
import urllib.request

RELEASES = ["1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6", "1.21", "1.21.1", "1.21.2", "1.21.3",
            "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11"]
MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"


def mojang_text(mdir, v, manifest):
    p = os.path.join(mdir, f"mojang-{v}.txt")
    if os.path.exists(p):
        return open(p, encoding="utf-8").read()
    vj = json.load(urllib.request.urlopen(next(x["url"] for x in manifest["versions"] if x["id"] == v)))
    return urllib.request.urlopen(vj["downloads"]["client_mappings"]["url"]).read().decode("utf-8")


def main():
    mdir, out = sys.argv[1], sys.argv[2]
    manifest = None
    names = {}
    for v in reversed(RELEASES):
        tiny = os.path.join(mdir, f"intermediary-{v}.tiny")
        if not os.path.exists(tiny):
            print(f"{v}: no {tiny}, skipped")
            continue
        inter_of_obf = {}
        for line in open(tiny, encoding="utf-8"):
            f = line.rstrip("\n").split("\t")
            if f[0] in ("c", "CLASS") and len(f) >= 3:
                inter_of_obf[f[1]] = f[2]
        if manifest is None and not os.path.exists(os.path.join(mdir, f"mojang-{v}.txt")):
            manifest = json.load(urllib.request.urlopen(MANIFEST))
        added = 0
        for line in mojang_text(mdir, v, manifest).splitlines():
            if line.startswith((" ", "#")):
                continue
            m = re.match(r"(\S+) -> (\S+):", line)
            if not m:
                continue
            inter = inter_of_obf.get(m.group(2).replace(".", "/"))
            if inter and inter not in names:
                names[inter] = (m.group(1).replace(".", "/"), v)
                added += 1
        print(f"{v}: {added} classes named")
    with open(out, "w", encoding="utf-8", newline="\n") as o:
        for inter in sorted(names):
            o.write(f"C\t{inter}\t{names[inter][0]}\t{names[inter][1]}\n")
    print(f"{len(names)} classes -> {out}")


if __name__ == "__main__":
    main()
