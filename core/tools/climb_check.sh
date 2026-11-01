#!/usr/bin/env bash
# climb_check.sh <workDir> <target>: the 1.20.1 full API probe climbed to <target> one release at a
# time (Main convert-climb, with the engine, segments and step wirings in <workDir>), link-checked per
# site on <target>'s real runtime with the climbed CenturyBridge layers of <workDir>/runtime-<target>
# (climb_runtime.sh), and every open entry attributed to the segment where it first broke.
# Run on beta2 from ~/cbwork; <workDir> is relative to it.
set -uo pipefail
cd ~/cbwork
WD="$1"; T="$2"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
OUT="$WD/climb/probe-$T"
rm -rf "$OUT" && mkdir -p "$OUT"
[ -d "$WD/runtime-$T" ] || { echo "no $WD/runtime-$T (run climb_runtime.sh $WD $T first)"; exit 1; }
# the climb and the link-check both run <workDir>'s compiled engine, never ~/cbwork/core/build/classes
[ -f "$WD/core/build/classes/top/dext/centurybridge/data/LinkCheck.class" ] \
    || { echo "engine not compiled in $WD/core/build/classes (climb_check judges with the tree under test's engine)"; exit 1; }
"$JAVA" -Xmx24g -Dcb.layersDir="$WD/layers" -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.20.1 "$T" "$OUT/conv" \
    probe/seg/1.20.1__1.20.2/probe-1.20.1.jar > "$OUT/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$OUT/climb.log"; exit 1; }
grep -E "^== climb" "$OUT/climb.log"
case "$T" in 26.*) GAME="data/jars/client-$T.jar" ;; *) GAME="data/jars/client-$T-intermediary.jar" ;; esac
FAPI=$(ls data/runtime/client-$T/mods/fabric-api-*.jar 2>/dev/null | head -1)
mkdir -p "$OUT/final" && cp "$OUT"/conv/*+"$T".jar "$OUT/final/"
"$JAVA" -Xmx32g -Dcb.linkcheck.perSite=true -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main link-check "$OUT/lc" - "$OUT/final" -- \
    "$WD/runtime-$T" "$GAME" $FAPI "data/runtime/client-$T/libraries" > "$OUT/linkcheck.log" 2>&1
python3 probe/probe_report.py probe/seg/1.20.1__1.20.2/probe-1.20.1.jar.index.tsv "$(ls "$OUT"/final/*.jar | head -1)" \
    "$OUT/lc/link-findings.tsv" "$OUT/report.tsv" > "$OUT/report.txt" 2>&1
head -2 "$OUT/report.txt"
# the climb crosses the wall at its 1.21.11 -> 26.1 step whatever T is: the year-era damage tables spell
# their V side the way that step's table names things (core/segments/wall-tables.tsv, target 26.1)
python3 probe/segment_closure.py "$OUT/report.tsv" probe/damage "$T" "$(python3 "$WD/core/tools/wall_tables.py" 26.1)"
