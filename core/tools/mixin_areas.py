"""Split a step's open mixin rows by area, the way step_check areas are keyed: a regex on the owner class's
Mojang name (the last release that had the class, data/mappings/inter-to-mojang-classes.tsv; 26.x owners
are already Mojang names).

  python mixin_areas.py <open-mixin.tsv | cause-mixin.tsv> [--out <dir>] [--area NAME=REGEX ...]

With no --area, the default table below is used, first match wins and every row lands in exactly one area
("other" catches the rest). With --area (repeatable) only those areas are reported, each filtered by its
regex exactly as step_check filters (an agent's own area regex gives that agent's rows). Prints rows,
distinct targets and the top causes per area (cause-mixin.tsv carries a cause column; open-mixin.tsv
its "now" fate); --out writes <dir>/<area>.tsv with the input's header and the area's rows.
"""
import collections
import os
import re
import sys

DEFAULT = [
    ("client-gui", r"^(net/minecraft/client/gui/|com/mojang/realmsclient/)"),
    ("client-render", r"^(net/minecraft/client/(renderer|model|resources/model|particle)/|com/mojang/blaze3d/)"),
    ("client-other", r"^net/minecraft/client/"),
    ("entity", r"^net/minecraft/world/entity/"),
    ("item", r"^net/minecraft/world/(item|food|effect|inventory)/"),
    ("level-block", r"^net/minecraft/world/level/(block|material)/"),
    ("level", r"^net/minecraft/world/(level|phys)/"),
    ("world-other", r"^net/minecraft/world/"),
    ("network", r"^net/minecraft/network/"),
    ("server-commands", r"^net/minecraft/(server|commands|gametest)/"),
    ("data-advancements", r"^net/minecraft/(data|advancements|recipebook|stats|tags|resources)/"),
    ("core-util", r"^net/minecraft/(core|util|nbt|sounds|SharedConstants|CrashReport|Util)"),
    ("other", r"."),
]


def names():
    cm = {}
    for p in ("data/mappings/inter-to-mojang-classes.tsv", "data/mappings/inter-to-mojang-1.21.11.tsv"):
        if os.path.exists(p):
            for line in open(p, encoding="utf-8"):
                f = line.rstrip("\n").split("\t")
                if len(f) >= 3 and f[0] == "C":
                    cm.setdefault(f[1], f[2])
    return cm


def main():
    args = sys.argv[1:]
    src = args[0]
    out = None
    areas = []
    i = 1
    while i < len(args):
        if args[i] == "--out":
            out = args[i + 1]
            i += 2
        elif args[i] == "--area":
            n, r = args[i + 1].split("=", 1)
            areas.append((n, r))
            i += 2
        else:
            sys.exit("unknown argument " + args[i])
    exclusive = not areas
    areas = areas or DEFAULT
    cm = names()

    def moj(c):
        if c in cm:
            return cm[c]
        d = c.rfind("$")
        return moj(c[:d]) + c[d:] if d > 0 else c

    with open(src, encoding="utf-8") as f:
        head = next(f).rstrip("\n")
        col = {n: k for k, n in enumerate(head.split("\t"))}
        rows = [l.rstrip("\n").split("\t") for l in f]
    why = "cause" if "cause" in col else "now"
    by = collections.OrderedDict((n, []) for n, _ in areas)
    for r in rows:
        m = r[col["mojang"]] if "mojang" in col else moj(r[col["owner"]])
        m = moj(r[col["owner"]]) if m.startswith("net/minecraft/class_") else m
        for n, rx in areas:
            if re.search(rx, m):
                by[n].append(r)
                if exclusive:
                    break
    print(f"{src}: {len(rows)} open mixin rows" + (" (areas exclusive, first match)" if exclusive else ""))
    for n, rs in by.items():
        if not rs:
            continue
        tg = len({(r[col["owner"]], r[col["target"]]) for r in rs})
        top = collections.Counter(re.sub(r"\d+", "N", r[col[why]].split(":")[0])[:40] for r in rs).most_common(3)
        print(f"  {n:18s} {len(rs):6d} rows {tg:5d} targets   " + ", ".join(f"{c} {k}" for k, c in top))
        if out:
            os.makedirs(out, exist_ok=True)
            with open(os.path.join(out, n + ".tsv"), "w", encoding="utf-8") as o:
                o.write(head + "\n")
                for r in rs:
                    o.write("\t".join(r) + "\n")


if __name__ == "__main__":
    main()
