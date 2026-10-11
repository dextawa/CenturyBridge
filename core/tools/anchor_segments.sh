#!/usr/bin/env bash
# anchor_segments.sh: the mixin side of the full diff, one release boundary at a time
# (Main anchor-diff: every injection point of every method with code in V, checked in W).
# Run from the repo root; needs data/jars/client-<ver>-intermediary-code.jar (CB_REMAP_KEEP_CODE=1 remap-jar)
# and the named 26.x jars.
set -uo pipefail
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ';')
OUT=core/out-probe/anchors
mkdir -p "$OUT"
RELEASES=(1.20.1 1.20.2 1.20.3 1.20.4 1.20.5 1.20.6 1.21 1.21.1 1.21.2 1.21.3 1.21.4 1.21.5 1.21.6 1.21.7 1.21.8 1.21.9 1.21.10 1.21.11 26.1 26.1.1 26.1.2 26.2)
code_of() { case "$1" in 26.*) echo "data/jars/client-$1.jar" ;; *) echo "data/jars/client-$1-intermediary-code.jar" ;; esac; }
for ((i = 0; i + 1 < ${#RELEASES[@]}; i++)); do
  V=${RELEASES[$i]}; W=${RELEASES[$((i + 1))]}
  WALL=(); [ "$V" = "1.21.11" ] && WALL=(--wall data/mappings/inter-to-mojang-1.21.11.tsv)
  java -Xmx2800m -cp "core/build/classes;$LIBS" top.dext.centurybridge.Main anchor-diff "$(code_of "$V")" "$(code_of "$W")" \
      "$OUT/${V}__${W}.tsv" "${WALL[@]}" 2>&1 | tail -1 | sed "s|^|${V}__${W}: |"
done
