"""Attribute an anchor target's open probe sites to the segment where each first broke.

The end-to-end probe (probe.sh: the 1.20.1 surface converted to target T, link-checked with
CenturyBridge on T's real runtime) says which 1.20.1 member/usage pairs a mod still cannot
use on T. The per-segment damage tables (core/segments/damage) say at which boundary each pair
breaks. Joining them gives, per segment, the entries still open at T -- the closure of every
segment as T sees it -- and separates out what no segment broke: open at T although every
boundary on the way kept it, i.e. damage the conversion or a bridge introduced.

  python segment_closure.py <end-to-end report.tsv> <damage dir> <target> [<inter-to-mojang.tsv>]
"""
import collections
import os
import sys

RELEASES = ["1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6", "1.21", "1.21.1", "1.21.2", "1.21.3",
            "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11", "26.1", "26.1.1",
            "26.1.2", "26.2"]


def main():
    report, damage_dir, target = sys.argv[1:4]
    cmap, mmap = {}, {}
    if len(sys.argv) > 4:
        for line in open(sys.argv[4], encoding="utf-8"):
            f = line.rstrip("\n").split("\t")
            if len(f) >= 3:
                if f[0] == "C":
                    cmap[f[1]] = f[2]
                else:
                    mmap[f[1]] = f[2].rsplit(".", 1)[1]

    def tcls(c):
        if c in cmap:
            return cmap[c]
        d = c.rfind("$")
        return tcls(c[:d]) + c[d:] if d > 0 else c

    def tdesc(d):
        out, i = [], 0
        while i < len(d):
            ch = d[i]
            out.append(ch)
            if ch == "L":
                j = d.index(";", i)
                out.append(tcls(d[i + 1:j]) + ";")
                i = j
            i += 1
        return "".join(out)

    segs = []
    for a, b in zip(RELEASES, RELEASES[1:]):
        segs.append(f"{a}__{b}")
        if b == target:
            break
    tables = {}
    for s in segs:
        keys = set()
        p = os.path.join(damage_dir, s + ".damage.tsv")
        if os.path.exists(p):
            with open(p, encoding="utf-8") as f:
                next(f)
                for line in f:
                    r = line.rstrip("\n").split("\t")
                    keys.add((r[0], r[1], r[2], r[3]))
        tables[s] = keys
    open_by_seg = collections.Counter()
    kinds_by_seg = collections.defaultdict(collections.Counter)
    introduced = collections.Counter()
    examples = collections.defaultdict(list)
    with open(report, encoding="utf-8") as f:
        next(f)
        for line in f:
            r = line.rstrip("\n").split("\t")
            usage, owner, name, desc, kinds = r[1], r[2], r[3], r[4], r[6]
            hit = None
            for s in segs:
                post_wall = s.split("__")[0].startswith("26.")
                key = (usage, tcls(owner), mmap.get(owner + "." + name, name), tdesc(desc)) if post_wall and cmap \
                    else (usage, owner, name, desc)
                if key in tables[s]:
                    hit = s
                    break
            if hit:
                open_by_seg[hit] += 1
                for k in kinds.split(","):
                    kinds_by_seg[hit][k] += 1
            else:
                introduced[kinds] += 1
                if len(examples[kinds]) < 3:
                    examples[kinds].append(f"{usage} {owner}.{name}{desc}")
    total = sum(open_by_seg.values()) + sum(introduced.values())
    print(f"open probe sites at {target}: {total}")
    print("by the segment where they first break (segment's own damage rows in parentheses):")
    for s in segs:
        if open_by_seg[s] or tables[s]:
            print(f"  {s:18} open {open_by_seg[s]:6} of ({len(tables[s]):5})  {dict(kinds_by_seg[s].most_common(5))}")
    print(f"open although no segment broke them (conversion/bridge-introduced): {sum(introduced.values())}")
    for k, c in introduced.most_common(8):
        print(f"  {c:6} {k:18} e.g. {examples[k][0][:140]}")


if __name__ == "__main__":
    main()
