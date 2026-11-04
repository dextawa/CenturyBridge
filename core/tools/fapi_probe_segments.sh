#!/usr/bin/env bash
# fapi_probe_segments.sh: the Fabric API side of the full diff, one release boundary at a time. A 1.20.1
# mod uses Fabric API as much as Minecraft, and Fabric API changes between releases too (modules
# removed, events and networking reshaped, 26.x mojang-style class names). For every consecutive pair
# V -> W: the API probe (Main api-probe) of Fabric API's public surface on V -- every class under
# net/fabricmc/fabric/api of every module of V's latest Fabric API -- link-checked per site, raw, on
# W's real classpath (game W + W's Fabric API modules + W's libraries); control: the same probe on V's
# own classpath. The wall (1.21.11 -> 26.1) remaps the probe to mojang names first, nothing more.
# Each boundary's table lands in probe/fapi-damage/<V>__<W>.damage.tsv (copy to core/segments/damage-fapi/).
# Run from ~/cbwork on beta2 (provision_step.sh gives every release its Fabric API modules).
set -uo pipefail
cd ~/cbwork
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
# CB_ENGINE: the compiled engine to measure with; arguments: only the segments starting at these
# releases (all when none); CB_REPROBE=1 rebuilds their probes (when api-probe itself changed)
# CB_FAPI_TIER=impl: the impl tier instead -- every class under net/fabricmc/fabric/impl of V's modules (not API, but mods
# reach it: a mixin naming an impl interface Fabric API injects into a vanilla class, Kilt's TagFileHooks; an impl static
# helper), same probe and link-check; probes in probe/fapi-impl, tables in probe/fapi-impl-damage (copy to
# core/segments/damage-fapi-impl/, kept apart from damage-fapi: a separate count, never the API kill list).
CP="${CB_ENGINE:-core/build/classes}:$LIBS"
ONLY=" $* "
OUT=probe/fapi; DMG=probe/fapi-damage; PKG=api
[ "${CB_FAPI_TIER:-}" = impl ] && { OUT=probe/fapi-impl; DMG=probe/fapi-impl-damage; PKG=impl; }
mkdir -p "$OUT" "$DMG"
RELEASES=(1.20.1 1.20.2 1.20.3 1.20.4 1.20.5 1.20.6 1.21 1.21.1 1.21.2 1.21.3 1.21.4 1.21.5 1.21.6 1.21.7 1.21.8 1.21.9 1.21.10 1.21.11 26.1 26.1.1 26.1.2 26.2)
jar_of() { case "$1" in 26.*) echo "data/jars/client-$1.jar" ;; *) echo "data/jars/client-$1-intermediary.jar" ;; esac; }
mods_of() { echo "data/runtime/client-$1/.fabric/processedMods"; }
: > "$OUT/progress.txt"
for ((i = 0; i + 1 < ${#RELEASES[@]}; i++)); do
  V=${RELEASES[$i]}; W=${RELEASES[$((i + 1))]}
  SEG="${V}__${W}"
  [ "$ONLY" = "  " ] || [[ "$ONLY" == *" $V "* ]] || continue
  [ "${CB_REPROBE:-}" = 1 ] && rm -f "$OUT/$SEG/probe-$V.jar" "$OUT/$SEG/probe-$V.jar.index.tsv"
  mkdir -p "$OUT/$SEG/probe"
  # V's Fabric API surface: the api (or impl) packages of every module, one jar
  if [ ! -f "$OUT/$SEG/fapi-$PKG-$V.jar" ]; then
    rm -rf "$OUT/$SEG/$PKG" && mkdir -p "$OUT/$SEG/$PKG"
    for m in $(mods_of "$V")/*.jar; do
      (cd "$OUT/$SEG/$PKG" && unzip -q -o "$OLDPWD/$m" "net/fabricmc/fabric/$PKG/*" 2>/dev/null)
    done
    (cd "$OUT/$SEG/$PKG" && ~/cbtest/jdk-25.0.4.1+1/bin/jar cf "../fapi-$PKG-$V.jar" .)
    rm -rf "$OUT/$SEG/$PKG"
  fi
  [ -f "$OUT/$SEG/probe-$V.jar.index.tsv" ] || "$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main api-probe "$OUT/$SEG/fapi-$PKG-$V.jar" \
      "$OUT/$SEG/probe-$V.jar" "$(jar_of "$V")" "data/runtime/client-$V/libraries" "$(mods_of "$V")" > "$OUT/$SEG/probe.log" 2>&1
  mkdir -p "$OUT/$SEG/self" && cp "$OUT/$SEG/probe-$V.jar" "$OUT/$SEG/self/"
  rm -rf "$OUT/$SEG/control"
  "$JAVA" -Xmx32g -Dcb.linkcheck.perSite=true -cp "$CP" top.dext.centurybridge.Main link-check "$OUT/$SEG/control" - "$OUT/$SEG/self" -- \
      "$(jar_of "$V")" "$(mods_of "$V")" "data/runtime/client-$V/libraries" > "$OUT/$SEG/control.log" 2>&1
  # the control's own findings per site: a site that does not link on V either is no damage of the step
  python3 probe/probe_report.py "$OUT/$SEG/probe-$V.jar.index.tsv" "$OUT/$SEG/self/probe-$V.jar" \
      "$OUT/$SEG/control/link-findings.tsv" "$OUT/$SEG/control-report.tsv" > "$OUT/$SEG/control-report.txt" 2>&1
  rm -f "$OUT/$SEG/probe"/*.jar
  if [ "$V" = "1.21.11" ]; then
    "$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main remap-mojang "$(python3 probe/wall_tables.py "$W")" \
        "$OUT/$SEG/probe-$V.jar" "$OUT/$SEG/probe/probe-$V-mojang.jar" >> "$OUT/$SEG/probe.log" 2>&1
  else
    cp "$OUT/$SEG/probe-$V.jar" "$OUT/$SEG/probe/"
  fi
  rm -rf "$OUT/$SEG/lc"
  "$JAVA" -Xmx32g -Dcb.linkcheck.perSite=true -cp "$CP" top.dext.centurybridge.Main link-check "$OUT/$SEG/lc" - "$OUT/$SEG/probe" -- \
      "$(jar_of "$W")" "$(mods_of "$W")" "data/runtime/client-$W/libraries" > "$OUT/$SEG/linkcheck.log" 2>&1
  python3 probe/probe_report.py "$OUT/$SEG/probe-$V.jar.index.tsv" "$(ls "$OUT/$SEG/probe"/*.jar | head -1)" \
      "$OUT/$SEG/lc/link-findings.tsv" "$OUT/$SEG/report.tsv" > "$OUT/$SEG/report.txt" 2>&1
  WALL=""; [ "$V" = "1.21.11" ] && WALL="$(python3 probe/wall_tables.py "$W")"
  python3 probe/segment_damage.py "$OUT/$SEG/report.tsv" "$(jar_of "$W")" "$DMG/$SEG.damage.tsv" $WALL \
      --control "$OUT/$SEG/control-report.tsv" \
      --w-extra "$(mods_of "$W")" >> "$OUT/$SEG/report.txt" 2>&1
  echo "done $SEG $(date +%H:%M) control=$(($(grep -c . "$OUT/$SEG/control/link-findings.tsv" 2>/dev/null) - 1)) findings=$(($(grep -c . "$OUT/$SEG/lc/link-findings.tsv" 2>/dev/null) - 1)) damage=$(($(grep -c . "$DMG/$SEG.damage.tsv" 2>/dev/null) - 1))" >> "$OUT/progress.txt"
done
echo ALL_DONE >> "$OUT/progress.txt"
