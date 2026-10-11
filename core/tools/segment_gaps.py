"""How much of a segment's real damage the engine's segment data records.

The per-segment probe (probe_segments.sh) gives each boundary V -> W its usage-level damage,
measured on W's real classpath; core/segments/<V>__<W>.json is what the engine works from
(names that died or changed descriptor). Every broken probe site is checked against it:

  listed      the member is in the segment's death / descriptor-change lists
  unlisted    it is not -- the engine cannot see this damage at all. Some kinds have no place
              in the segment format: constructors (<init> is no unique name), access narrowed,
              final / sealed / static flips, abstract methods W added to a class or interface,
              a member that still exists but no longer resolves through its old owner.

  python segment_gaps.py <probe report.tsv> <segment.json> [<inter-to-mojang.tsv>]
(the tsv translates a mojang-named V back to the segment's intermediary names, for 26.x V)
"""
import collections
import json
import sys


def main():
    report, seg_path = sys.argv[1], sys.argv[2]
    seg = json.load(open(seg_path, encoding="utf-8"))
    back = {}
    if len(sys.argv) > 3:
        for line in open(sys.argv[3], encoding="utf-8"):
            f = line.rstrip("\n").split("\t")
            if len(f) >= 3:
                back[f[2]] = f[1]  # mojang owner.name / class -> intermediary
    classes = set(seg.get("classesGone", []))
    methods = set(seg.get("methodsGone", [])) | set(seg.get("methodsDescChanged", []))
    fields = set(seg.get("fieldsGone", [])) | set(seg.get("fieldsDescChanged", []))

    def inter(owner, name):
        o = back.get(owner, owner)
        n = back.get(owner + "." + name, name)
        if "." in n:
            n = n.rsplit(".", 1)[1]
        return o, n

    counts = collections.Counter()
    by_kind = collections.defaultdict(collections.Counter)
    examples = collections.defaultdict(list)
    with open(report, encoding="utf-8") as f:
        next(f)
        for line in f:
            r = line.rstrip("\n").split("\t")
            usage, owner, name, kinds = r[1], r[2], r[3], r[6]
            o, n = inter(owner, name)
            if usage in ("extend", "implement-interface"):
                listed = o in classes
            elif name == "<init>":
                listed = o in classes
            elif usage.startswith(("get", "put")):
                listed = n in fields or o in classes
            else:
                listed = n in methods or o in classes
            verdict = "listed" if listed else "unlisted"
            counts[verdict] += 1
            for k in kinds.split(","):
                by_kind[k][verdict] += 1
            if not listed and len(examples[(usage, kinds)]) < 2:
                examples[(usage, kinds)].append(f"{owner}.{name}{r[4]}")
    total = sum(counts.values())
    print(f"broken probe sites: {total}; listed in the segment data: {counts['listed']}; "
          f"unlisted: {counts['unlisted']} ({counts['unlisted'] * 100 / max(total, 1):.0f}%)")
    for k, c in sorted(by_kind.items(), key=lambda x: -sum(x[1].values())):
        print(f"  {k:10} listed {c['listed']:6}  unlisted {c['unlisted']:6}")
    print("unlisted, by usage and kind (examples):")
    for (u, k), ex in sorted(examples.items(), key=lambda x: x[0]):
        print(f"  {u:22} {k:18} {ex[0][:120]}")


if __name__ == "__main__":
    main()
