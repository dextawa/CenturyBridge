#!/usr/bin/env bash
# step_check.sh <workDir> <V> <W> [areaRegex]: how far one step of the climb (V -> W) is closed.
# Converts V's full API probe (probe/seg/<V>__<W>/probe-<V>.jar, from probe_segments.sh) one step
# with the engine, segments and bridge jar found in <workDir> (core/build/classes, core/segments,
# build/centurybridge-0.3.0+<W>.jar), link-checks it on W's real runtime (game + libraries +
# fabric-api + that bridge jar), and lists the step's damage entries (core/segments/damage) that
# are still open -- optionally only those whose owner's mojang name matches areaRegex.
# Run on beta2 from ~/cbwork; <workDir> is relative to it (an agent's own directory, or "." for main).
set -uo pipefail
cd ~/cbwork
WD="$1"; V="$2"; W="$3"; AREA="${4:-}"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
SEG="${V}__${W}"
OUT="$WD/step/$SEG"
rm -rf "$OUT" && mkdir -p "$OUT"
case "$W" in 26.*) GAME="data/jars/client-$W.jar" ;; *) GAME="data/jars/client-$W-intermediary.jar" ;; esac
FAPI=$(ls data/runtime/client-$W/mods/fabric-api-*.jar 2>/dev/null | head -1)
"$JAVA" -Xmx16g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert "$WD/core/segments" "$V" "$W" "$OUT/conv" \
    "probe/seg/$SEG/probe-$V.jar" > "$OUT/convert.log" 2>&1 || { echo "convert failed"; tail -5 "$OUT/convert.log"; exit 1; }
"$JAVA" -Xmx32g -cp "core/build/classes:$LIBS" top.dext.centurybridge.Main link-check "$OUT/lc" "$OUT/conv/full-audit.tsv" "$OUT/conv" -- \
    "$WD/build/centurybridge-0.3.0+$W.jar" "$GAME" $FAPI "data/runtime/client-$W/libraries" > "$OUT/linkcheck.log" 2>&1
python3 probe/probe_report.py "probe/seg/$SEG/probe-$V.jar.index.tsv" "$(ls "$OUT"/conv/*.jar | head -1)" "$OUT/lc/link-findings.tsv" \
    "$OUT/report.tsv" > "$OUT/report.txt" 2>&1
python3 - "$OUT/report.tsv" "probe/damage/$SEG.damage.tsv" "$AREA" <<'EOF'
import collections, re, sys
report, damage, area = sys.argv[1], sys.argv[2], sys.argv[3]
cm = {}
for line in open("data/mappings/inter-to-mojang-1.21.11.tsv", encoding="utf-8"):
    f = line.rstrip("\n").split("\t")
    if len(f) >= 3 and f[0] == "C":
        cm[f[1]] = f[2]
def moj(c):
    if c in cm:
        return cm[c]
    d = c.rfind("$")
    return moj(c[:d]) + c[d:] if d > 0 else c
dmg = {}
for i, line in enumerate(open(damage, encoding="utf-8")):
    if i:
        r = line.rstrip("\n").split("\t")
        dmg[(r[0], r[1], r[2], r[3])] = r
open_rows = []
for i, line in enumerate(open(report, encoding="utf-8")):
    if i == 0:
        continue
    r = line.rstrip("\n").split("\t")
    k = (r[1], r[2], r[3], r[4])
    if k in dmg and (not area or re.search(area, moj(r[2]))):
        open_rows.append((r[1], r[2], moj(r[2]), r[3], r[4], r[5], r[6], dmg[k][5]))
total = sum(1 for k in dmg if not area or re.search(area, moj(k[1])))
print(f"step {damage.split('/')[-1][:-11]}: {len(open_rows)} of {total} damage entries still open" + (f" (area /{area}/)" if area else ""))
by = collections.Counter((r[0], r[6]) for r in open_rows)
for k, c in by.most_common(12):
    print(f"  {c:5} {k[0]:22} {k[1]}")
with open(report.replace("report.tsv", "open.tsv"), "w", encoding="utf-8") as o:
    o.write("usage\towner\tmojang\tname\tdesc\tbuckets\tkinds\tw\n")
    for r in open_rows:
        o.write("\t".join(r) + "\n")
print("open entries ->", report.replace("report.tsv", "open.tsv"))
EOF
