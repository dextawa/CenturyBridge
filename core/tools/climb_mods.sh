#!/usr/bin/env bash
# climb_mods.sh <workDir> <T> <convDir> <modsDir> <mod jar...>: the mods directory of a climb boot.
# The given 1.20.1 mods are converted one release at a time to T (Main convert-climb with <workDir>'s
# engine, segments, step wirings and layers; log in <convDir>/climb.log), and <modsDir> is emptied and
# filled with Fabric API for T, the climbed CenturyBridge layers of <workDir>/runtime-T
# (climb_runtime.sh) and the converted mods. Shared by boot_climb.sh (server) and
# boot_client_remote.sh (client). Run on beta2; paths are relative to ~/cbwork.
# CB_NATIVE_MODS="<jar> ...": mods native to T, copied in as they are (never converted), e.g. a test mod whose
# T-era mixins must still act beside the layers.
set -uo pipefail
cd ~/cbwork
WD="$1"; T="$2"; CONV="$3"; MODS="$4"; shift 4
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
[ -d "$WD/runtime-$T" ] || { echo "no $WD/runtime-$T (climb_runtime.sh $WD $T)"; exit 1; }
rm -rf "$CONV" && mkdir -p "$CONV"
"$JAVA" -Xmx8g -Dcb.layersDir="$WD/layers" -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.20.1 "$T" "$CONV/conv" "$@" \
    > "$CONV/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$CONV/climb.log"; exit 1; }
rm -rf "$MODS" && mkdir -p "$MODS"
cp data/runtime/client-$T/mods/fabric-api-*.jar "$MODS/"
cp "$WD/runtime-$T"/*.jar "$MODS/"
cp "$CONV"/conv/*+"$T".jar "$MODS/"
for n in ${CB_NATIVE_MODS:-}; do
    [ -f "$n" ] || { echo "no native mod $n"; exit 1; }
    cp "$n" "$MODS/"
done
echo "mods ($(ls "$MODS" | wc -l)): $(ls "$MODS" | tr '\n' ' ')"
