#!/usr/bin/env bash
# step_check.sh <workDir> <V> <W> [areaRegex]: how far one step of the climb (V -> W) is closed.
# Converts V's full API probe (probe/seg/<V>__<W>/probe-<V>.jar, from probe_segments.sh) one step
# with the engine, segments and bridge jar found in <workDir> (core/build/classes, core/segments,
# build/centurybridge-0.3.0+<W>.jar), link-checks it on W's real runtime (game + libraries +
# fabric-api + that bridge jar), and lists the step's damage entries (core/segments/damage) that
# are still open -- optionally only those whose owner's mojang name matches areaRegex.
# CB_PROBE=fapi measures the Fabric API side instead: V's Fabric API probe (probe/fapi, from
# fapi_probe_segments.sh) against the step's Fabric API damage table (probe/fapi-damage).
# Run on beta2 from ~/cbwork; <workDir> is relative to it (an agent's own directory, or "." for main).
set -uo pipefail
cd ~/cbwork
WD="$1"; V="$2"; W="$3"; AREA="${4:-}"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
SEG="${V}__${W}"
PROBES=probe/seg; DAMAGE=probe/damage; OUT="$WD/step/$SEG"
[ "${CB_PROBE:-}" = fapi ] && { PROBES=probe/fapi; DAMAGE=probe/fapi-damage; OUT="$WD/step-fapi/$SEG"; }
rm -rf "$OUT" && mkdir -p "$OUT"
case "$W" in 26.*) GAME="data/jars/client-$W.jar" ;; *) GAME="data/jars/client-$W-intermediary.jar" ;; esac
FAPI=$(ls data/runtime/client-$W/mods/fabric-api-*.jar 2>/dev/null | head -1)
# the step's own bridge wiring (step 1 is the 1.20.2 tree's shims-1.20.2.json)
WIRING="$WD/core/segments/steps/$SEG.json"
[ "$SEG" = "1.20.1__1.20.2" ] && WIRING="$WD/core/segments/shims-1.20.2.json"
"$JAVA" -Xmx16g -Dcb.wiring="$WIRING" -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert "$WD/core/segments" "$V" "$W" "$OUT/conv" \
    "$PROBES/$SEG/probe-$V.jar" > "$OUT/convert.log" 2>&1 || { echo "convert failed"; tail -5 "$OUT/convert.log"; exit 1; }
"$JAVA" -Xmx32g -Dcb.linkcheck.perSite=true -cp "core/build/classes:$LIBS" top.dext.centurybridge.Main link-check "$OUT/lc" "$OUT/conv/full-audit.tsv" "$OUT/conv" -- \
    "$WD/build/centurybridge-0.3.0+$W.jar" "$GAME" $FAPI "data/runtime/client-$W/libraries" > "$OUT/linkcheck.log" 2>&1
python3 probe/probe_report.py "$PROBES/$SEG/probe-$V.jar.index.tsv" "$(ls "$OUT"/conv/*.jar | head -1)" "$OUT/lc/link-findings.tsv" \
    "$OUT/report.tsv" > "$OUT/report.txt" 2>&1
python3 - "$OUT/report.tsv" "$DAMAGE/$SEG.damage.tsv" "$AREA" <<'EOF'
import collections, os, re, sys
report, damage, area = sys.argv[1], sys.argv[2], sys.argv[3]
cm = {}
# every intermediary class's Mojang name from the last release that had it (class_names.py): a class
# that died before 1.21.11 has no name in the 1.21.11 table and would fall outside every area
names = "data/mappings/inter-to-mojang-classes.tsv"
for line in open(names if os.path.exists(names) else "data/mappings/inter-to-mojang-1.21.11.tsv", encoding="utf-8"):
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
# Generic drift (generic_drift.py): members whose erased descriptor V and W share while their type
# arguments changed -- linked fine, so the probe above cannot see them; the first element a mod takes
# out is a ClassCastException. Counted against the same wiring (and area) as the probe's rows.
if [ "${CB_PROBE:-}" != fapi ]; then
    case "$V" in 26.*) VJAR="data/jars/client-$V.jar" ;; *) VJAR="data/jars/client-$V-intermediary.jar" ;; esac
    WALL=""
    [ "$SEG" = "1.21.11__26.1" ] && WALL="--wall data/mappings/inter-to-mojang-1.21.11.tsv"
    python3 "$WD/core/tools/generic_drift.py" "$VJAR" "$GAME" "$OUT/generic.tsv" $WALL --wiring "$WIRING" > /dev/null \
        || { echo "generic drift failed"; exit 1; }
    python3 - "$OUT/generic.tsv" "$AREA" <<'EOF'
import os, re, sys
table, area = sys.argv[1], sys.argv[2]
cm = {}
names = "data/mappings/inter-to-mojang-classes.tsv"
for line in open(names if os.path.exists(names) else "data/mappings/inter-to-mojang-1.21.11.tsv", encoding="utf-8"):
    f = line.rstrip("\n").split("\t")
    if len(f) >= 3 and f[0] == "C":
        cm[f[1]] = f[2]
def moj(c):
    if c in cm:
        return cm[c]
    d = c.rfind("$")
    return moj(c[:d]) + c[d:] if d > 0 else c
rows = [l.rstrip("\n").split("\t") for l in open(table, encoding="utf-8")][1:]
rows = [r for r in rows if not area or re.search(area, moj(r[1]))]
open_rows = [r for r in rows if not r[8]]
print(f"generic drift: {len(open_rows)} of {len(rows)} member usages still open" + (f" (area /{area}/)" if area else ""))
with open(table.replace("generic.tsv", "generic-open.tsv"), "w", encoding="utf-8") as o:
    o.write("usage\towner\tmojang\tname\tdesc\tchanged\tV signature\tW signature\tV classes W lacks\n")
    for r in open_rows:
        o.write("\t".join([r[0], r[1], moj(r[1])] + r[2:8]) + "\n")
print("open generic drift ->", table.replace("generic.tsv", "generic-open.tsv"))
EOF
fi
