#!/usr/bin/env bash
# probe_segments.sh: the full diff, one release boundary at a time. For every consecutive pair
# V -> W of the chain: the API probe of V's whole surface (Main api-probe: every public and
# protected member, every way a mod can touch it), link-checked RAW on W's real classpath --
# no CenturyBridge, no conversion, nothing bridged. What breaks is that segment's complete
# usage-level damage, independent of any corpus. The namespace wall (1.21.11 -> 26.1) only
# renames: the 1.21.11 probe goes through remap-mojang first, nothing else -- with the table that
# spells names the way 26.1 declares them (core/segments/wall-tables.tsv; probe/wall_tables.py is a
# copy of core/tools/wall_tables.py, reading ~/cbwork/core/segments/wall-tables.tsv).
# Findings are per site (-Dcb.linkcheck.perSite): the probe holds every usage of every member in one
# jar, and per-jar deduplication by reference would keep only the first usage of a broken member.
# Each segment's damage table lands in probe/damage/<V>__<W>.damage.tsv (copy it to
# core/segments/damage/ in the repo). Run from ~/cbwork on beta2 (needs data/jars stubs,
# data/runtime/client-<ver>/libraries, probe/probe_report.py and probe/segment_damage.py).
#   probe_segments.sh [V...]   (CB_ENGINE=<classes dir>, CB_REPROBE=1: see below)
set -uo pipefail
cd ~/cbwork
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
# CB_ENGINE: the compiled engine to measure with (default the working set's core/build/classes)
CP="${CB_ENGINE:-core/build/classes}:$LIBS"
# arguments: only the segments starting at these releases (all when none); CB_REPROBE=1 rebuilds their
# probes too (a probe depends only on V's stub: rebuild it when the stub changed)
ONLY=" $* "
OUT=probe/seg
mkdir -p "$OUT"
RELEASES=(1.20.1 1.20.2 1.20.3 1.20.4 1.20.5 1.20.6 1.21 1.21.1 1.21.2 1.21.3 1.21.4 1.21.5 1.21.6 1.21.7 1.21.8 1.21.9 1.21.10 1.21.11 26.1 26.1.1 26.1.2 26.2)
jar_of() { case "$1" in 26.*) echo "data/jars/client-$1.jar" ;; *) echo "data/jars/client-$1-intermediary.jar" ;; esac; }
: > "$OUT/progress.txt"
for ((i = 0; i + 1 < ${#RELEASES[@]}; i++)); do
  V=${RELEASES[$i]}; W=${RELEASES[$((i + 1))]}
  SEG="${V}__${W}"
  [ "$ONLY" = "  " ] || [[ "$ONLY" == *" $V "* ]] || continue
  [ "${CB_REPROBE:-}" = 1 ] && rm -f "$OUT/$SEG/probe-$V.jar" "$OUT/$SEG/probe-$V.jar.index.tsv"
  mkdir -p "$OUT/$SEG/probe"
  # the probe of V depends only on V's stub: kept when it is already there
  [ -f "$OUT/$SEG/probe-$V.jar.index.tsv" ] || "$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main api-probe "$(jar_of "$V")" \
      "$OUT/$SEG/probe-$V.jar" "data/runtime/client-$V/libraries" > "$OUT/$SEG/probe.log" 2>&1
  # control: the raw probe on its own release must be clean, or the probe (not the segment) is wrong
  mkdir -p "$OUT/$SEG/self" && cp "$OUT/$SEG/probe-$V.jar" "$OUT/$SEG/self/"
  rm -rf "$OUT/$SEG/control"
  "$JAVA" -Xmx32g -Dcb.linkcheck.perSite=true -cp "$CP" top.dext.centurybridge.Main link-check "$OUT/$SEG/control" - "$OUT/$SEG/self" -- \
      "$(jar_of "$V")" "data/runtime/client-$V/libraries" > "$OUT/$SEG/control.log" 2>&1
  # the control's own findings per site: a site that does not link on V either is no damage of the step
  python3 probe/probe_report.py "$OUT/$SEG/probe-$V.jar.index.tsv" "$OUT/$SEG/self/probe-$V.jar" \
      "$OUT/$SEG/control/link-findings.tsv" "$OUT/$SEG/control-report.tsv" > "$OUT/$SEG/control-report.txt" 2>&1
  if [ "$V" = "1.21.11" ]; then
    # the wall: the same probe under mojang names, nothing more
    "$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main remap-mojang "$(python3 probe/wall_tables.py "$W")" \
        "$OUT/$SEG/probe-$V.jar" "$OUT/$SEG/probe/probe-$V-mojang.jar" >> "$OUT/$SEG/probe.log" 2>&1
  else
    cp "$OUT/$SEG/probe-$V.jar" "$OUT/$SEG/probe/"
  fi
  rm -rf "$OUT/$SEG/lc"
  "$JAVA" -Xmx32g -Dcb.linkcheck.perSite=true -cp "$CP" top.dext.centurybridge.Main link-check "$OUT/$SEG/lc" - "$OUT/$SEG/probe" -- \
      "$(jar_of "$W")" "data/runtime/client-$W/libraries" > "$OUT/$SEG/linkcheck.log" 2>&1
  # the segment's damage table: findings mapped back to probe sites, then to V members and usages
  python3 probe/probe_report.py "$OUT/$SEG/probe-$V.jar.index.tsv" "$(ls "$OUT/$SEG/probe"/*.jar | head -1)" \
      "$OUT/$SEG/lc/link-findings.tsv" "$OUT/$SEG/report.tsv" > "$OUT/$SEG/report.txt" 2>&1
  WALL=""; [ "$V" = "1.21.11" ] && WALL="$(python3 probe/wall_tables.py "$W")"
  mkdir -p probe/damage
  python3 probe/segment_damage.py "$OUT/$SEG/report.tsv" "$(jar_of "$W")" "probe/damage/$SEG.damage.tsv" $WALL \
      --control "$OUT/$SEG/control-report.tsv" >> "$OUT/$SEG/report.txt" 2>&1
  echo "done $SEG $(date +%H:%M) control=$(($(grep -c . "$OUT/$SEG/control/link-findings.tsv" 2>/dev/null) - 1)) findings=$(($(grep -c . "$OUT/$SEG/lc/link-findings.tsv" 2>/dev/null) - 1)) damage=$(($(grep -c . "probe/damage/$SEG.damage.tsv" 2>/dev/null) - 1))" >> "$OUT/progress.txt"
done
echo ALL_DONE >> "$OUT/progress.txt"
