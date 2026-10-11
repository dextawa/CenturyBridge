#!/usr/bin/env python3
"""ROI view of a link-check run: which refs, if bridged, would clear the most jars.

    python3 lc_roi.py <lc-dir> [topN]

Reads link-findings.tsv; a jar is "dirty" if it has any CLAIMED/BLIND finding. For every
dirty jar, its distinct (kind, ref) blockers. Prints: jars blocked by exactly one / two refs,
and the refs ranked by how many jars they appear in, with how many of those jars they
would clear outright (sole blocker).
"""
import collections
import sys

d = sys.argv[1]
top = int(sys.argv[2]) if len(sys.argv) > 2 else 40
by_jar = collections.defaultdict(set)
for i, line in enumerate(open(f'{d}/link-findings.tsv', encoding='utf-8', errors='replace')):
    if i == 0:
        continue
    f = line.rstrip('\n').split('\t')
    if len(f) < 5 or f[0] not in ('CLAIMED', 'BLIND'):
        continue
    by_jar[f[2]].add((f[1], f[4]))
refs = collections.Counter()
sole = collections.Counter()
for jar, bl in by_jar.items():
    for r in bl:
        refs[r] += 1
    if len(bl) == 1:
        sole[next(iter(bl))] += 1
dist = collections.Counter(min(len(b), 10) for b in by_jar.values())
print(f'dirty jars: {len(by_jar)}; blockers per jar: ' + ', '.join(f'{k}{"+" if k == 10 else ""}:{v}' for k, v in sorted(dist.items())))
print(f'{"jars":>5} {"sole":>5}  kind ref')
for (kind, ref), n in refs.most_common(top):
    print(f'{n:5d} {sole[(kind, ref)]:5d}  {kind:8s} {ref[:150]}')
