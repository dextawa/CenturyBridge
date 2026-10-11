#!/usr/bin/env bash
# probe_segments.sh: the full diff, one release boundary at a time. For every consecutive pair
# V -> W of the chain: the API probe of V's whole surface (Main api-probe: every public and
# protected member, every way a mod can touch it), link-checked RAW on W's real classpath --
# no CenturyBridge, no conversion, nothing bridged. What breaks is that segment's complete
# usage-level damage, independent of any corpus. The namespace wall (1.21.11 -> 26.1) only
# renames: the 1.21.11 probe goes through remap-mojang first, nothing else.
# Run from ~/cbwork on beta2 (needs data/jars stubs, data/runtime/client-<ver>/libraries).
set -uo pipefail
cd ~/cbwork
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
CP="core/build/classes:$LIBS"
OUT=probe/seg
mkdir -p "$OUT"
RELEASES=(1.20.1 1.20.2 1.20.3 1.20.4 1.20.5 1.20.6 1.21 1.21.1 1.21.2 1.21.3 1.21.4 1.21.5 1.21.6 1.21.7 1.21.8 1.21.9 1.21.10 1.21.11 26.1 26.1.1 26.1.2 26.2)
jar_of() { case "$1" in 26.*) echo "data/jars/client-$1.jar" ;; *) echo "data/jars/client-$1-intermediary.jar" ;; esac; }
: > "$OUT/progress.txt"
for ((i = 0; i + 1 < ${#RELEASES[@]}; i++)); do
  V=${RELEASES[$i]}; W=${RELEASES[$((i + 1))]}
  SEG="${V}__${W}"
  mkdir -p "$OUT/$SEG/probe"
  "$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main api-probe "$(jar_of "$V")" "$OUT/$SEG/probe-$V.jar" "data/runtime/client-$V/libraries" \
      > "$OUT/$SEG/probe.log" 2>&1
  # control: the raw probe on its own release must be clean, or the probe (not the segment) is wrong
  mkdir -p "$OUT/$SEG/self" && cp "$OUT/$SEG/probe-$V.jar" "$OUT/$SEG/self/"
  rm -rf "$OUT/$SEG/control"
  "$JAVA" -Xmx32g -cp "$CP" top.dext.centurybridge.Main link-check "$OUT/$SEG/control" - "$OUT/$SEG/self" -- \
      "$(jar_of "$V")" "data/runtime/client-$V/libraries" > "$OUT/$SEG/control.log" 2>&1
  if [ "$V" = "1.21.11" ]; then
    # the wall: the same probe under mojang names, nothing more
    "$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main remap-mojang data/mappings/inter-to-mojang-1.21.11.tsv \
        "$OUT/$SEG/probe-$V.jar" "$OUT/$SEG/probe/probe-$V-mojang.jar" >> "$OUT/$SEG/probe.log" 2>&1
  else
    cp "$OUT/$SEG/probe-$V.jar" "$OUT/$SEG/probe/"
  fi
  rm -rf "$OUT/$SEG/lc"
  "$JAVA" -Xmx32g -cp "$CP" top.dext.centurybridge.Main link-check "$OUT/$SEG/lc" - "$OUT/$SEG/probe" -- \
      "$(jar_of "$W")" "data/runtime/client-$W/libraries" > "$OUT/$SEG/linkcheck.log" 2>&1
  echo "done $SEG $(date +%H:%M) control=$(($(grep -c . "$OUT/$SEG/control/link-findings.tsv" 2>/dev/null) - 1)) findings=$(($(grep -c . "$OUT/$SEG/lc/link-findings.tsv" 2>/dev/null) - 1))" >> "$OUT/progress.txt"
done
echo ALL_DONE >> "$OUT/progress.txt"
