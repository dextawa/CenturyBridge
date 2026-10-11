#!/usr/bin/env bash
# climb_check.sh <target> <cbRuntimeJar> : the 1.20.1 full API probe climbed to <target> one release at a
# time (Main convert-climb), link-checked on <target>'s real runtime with the given CenturyBridge runtime
# (the step layers, each climbed to <target>), attributed to the segment where each open entry first broke.
# Run on beta2 from ~/cbwork.
set -uo pipefail
cd ~/cbwork
T="$1"; CB="$2"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
OUT=climb/probe-$T
rm -rf "$OUT" && mkdir -p "$OUT"
"$JAVA" -Xmx24g -cp "core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb core/segments 1.20.1 "$T" "$OUT/conv" \
    probe/seg/1.20.1__1.20.2/probe-1.20.1.jar > "$OUT/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$OUT/climb.log"; exit 1; }
grep -E "^== climb|unaccounted" "$OUT/climb.log"
case "$T" in 26.*) GAME="data/jars/client-$T.jar" ;; *) GAME="data/jars/client-$T-intermediary.jar" ;; esac
FAPI=$(ls data/runtime/client-$T/mods/fabric-api-*.jar 2>/dev/null | head -1)
mkdir -p "$OUT/final" && cp "$OUT"/conv/*+"$T".jar "$OUT/final/"
"$JAVA" -Xmx32g -Dcb.linkcheck.perSite=true -cp "core/build/classes:$LIBS" top.dext.centurybridge.Main link-check "$OUT/lc" - "$OUT/final" -- \
    "$CB" "$GAME" $FAPI "data/runtime/client-$T/libraries" > "$OUT/linkcheck.log" 2>&1
python3 probe/probe_report.py probe/seg/1.20.1__1.20.2/probe-1.20.1.jar.index.tsv "$(ls "$OUT"/final/*.jar | head -1)" \
    "$OUT/lc/link-findings.tsv" "$OUT/report.tsv" > "$OUT/report.txt" 2>&1
head -2 "$OUT/report.txt"
python3 probe/segment_closure.py "$OUT/report.tsv" probe/damage "$T" data/mappings/inter-to-mojang-1.21.11.tsv
