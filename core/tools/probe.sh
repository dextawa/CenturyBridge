#!/usr/bin/env bash
# probe.sh: the API probe (Main api-probe: every public/protected member of 1.20.1 touched once
# in every way a mod can) through every target. First the control -- the raw probe on its own
# native 1.20.1 runtime must be clean, or the probe itself is wrong -- then convert + link-check
# per target. Findings are entries of the full diff the bridges do not cover.
set -uo pipefail
cd ~/cbwork
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
CP="core/build/classes:$LIBS"
OUT=probe
PROBE=$OUT/cb-apiprobe-1.20.1.jar
: > "$OUT/progress.txt"
rm -rf "$OUT/lc-native-1.20.1"
"$JAVA" -Xmx48g -Dcb.linkcheck.perSite=true -cp "$CP" top.dext.centurybridge.Main link-check "$OUT/lc-native-1.20.1" - "$OUT/raw" -- \
    data/jars/client-1.20.1-intermediary.jar data/runtime/client-1.20.1/mods/fabric-api-0.92.12+1.20.1.jar data/runtime/client-1.20.1/libraries/ \
    > "$OUT/linkcheck-native-1.20.1.log" 2>&1
echo "control done $(date +%H:%M)" >> "$OUT/progress.txt"
for T in ${TARGETS:-26.2 1.21.11 1.21.8 1.21.4 1.21.1 1.20.4 1.20.2}; do
  rm -rf "$OUT/conv-$T" "$OUT/lc-$T"
  "$JAVA" -Xmx24g -cp "$CP" top.dext.centurybridge.Main convert core/segments 1.20.1 "$T" "$OUT/conv-$T" "$PROBE" > "$OUT/convert-$T.log" 2>&1
  if [ "$T" = "26.2" ]; then GAME=data/jars/client-26.2.jar; else GAME="data/jars/client-$T-intermediary.jar"; fi
  FAPI=$(ls data/runtime/client-$T/mods/fabric-api-*.jar | head -1)
  "$JAVA" -Xmx48g -Dcb.linkcheck.perSite=true -cp "$CP" top.dext.centurybridge.Main link-check "$OUT/lc-$T" "$OUT/conv-$T/full-audit.tsv" "$OUT/conv-$T" -- \
      "build/centurybridge-0.3.0+$T.jar" "$GAME" "$FAPI" "data/runtime/client-$T/libraries" > "$OUT/linkcheck-$T.log" 2>&1
  echo "done $T $(date +%H:%M)" >> "$OUT/progress.txt"
done
echo ALL_DONE >> "$OUT/progress.txt"
