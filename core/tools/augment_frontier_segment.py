"""Add DESC_CHANGED knowledge to a year-era segment.

gen_frontier_segment.py records only what is GONE across a mojang-named
boundary; a member whose descriptor changed (OptionInstance.onValueUpdate:
Consumer -> ValueUpdateListener) looked alive to the chain, so neither mixin
triage nor call-site verification saw the damage. The owner-qualified
stub-diff has it; this back-translates its DESC_CHANGED rows to intermediary
tokens and merges them into the segment's *DescChanged lists.

  python3 augment_frontier_segment.py <inter-to-mojang.tsv> <diff.tsv> <segment.json> [--alt <table.tsv> ...]

  --alt : extra tables giving the same tokens other mojang placements (the
          1.21.11-named compose table next to the 26.2-targeted one): a diff
          measured against 26.1 spells owners the way 26.1 does, which for a
          class that moved only at 26.2 is the OLD name.
"""
import argparse
import io
import json

ap = argparse.ArgumentParser()
ap.add_argument("table")
ap.add_argument("diff")
ap.add_argument("segment")
ap.add_argument("--alt", nargs="*", default=[])
a = ap.parse_args()

rev = {}                                   # "mojOwner.mojName" -> intermediary token
for table in [a.table] + a.alt:
    for line in io.open(table, encoding="utf-8"):
        k, i, m = line.rstrip("\n").split("\t")
        if k in ("M", "F"):
            rev.setdefault(m, i.rsplit(".", 1)[1])

seg = json.load(io.open(a.segment, encoding="utf-8"))
mc, fc = set(seg.get("methodsDescChanged", [])), set(seg.get("fieldsDescChanged", []))
added_m = added_f = 0
for line in io.open(a.diff, encoding="utf-8"):
    p = line.rstrip("\n").split("\t")
    if len(p) < 4 or p[1] != "DESC_CHANGED":
        continue
    tok = rev.get(p[2])
    if tok is None:
        continue                           # born after 1.21.11: no old mod can name it
    if p[0] == "method":
        if tok not in mc:
            mc.add(tok); added_m += 1
    else:
        if tok not in fc:
            fc.add(tok); added_f += 1
seg["methodsDescChanged"] = sorted(mc)
seg["fieldsDescChanged"] = sorted(fc)
io.open(a.segment, "w", encoding="utf-8", newline="\n").write(json.dumps(seg, indent=1))
print(f"{a.segment}: +{added_m} methodsDescChanged, +{added_f} fieldsDescChanged")
