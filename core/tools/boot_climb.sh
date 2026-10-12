#!/usr/bin/env bash
# boot_climb.sh <workDir> <T> <tag> <mod jar...>: real-server check of the climb. The given 1.20.1 mods
# are converted one release at a time to T (Main convert-climb with <workDir>'s engine, segments and
# step wirings), and a Fabric T server (~/cbtest/server-T, fabric-server.jar) boots with Fabric API for
# T, the climbed CenturyBridge layers of <workDir>/runtime-T (climb_runtime.sh) and the converted mods.
# Prints whether it reached "Done" and the lines that matter. Run on beta2 from ~/cbwork.
set -uo pipefail
cd ~/cbwork
WD="$1"; T="$2"; TAG="$3"; shift 3
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
# CB_SERVER_DIR: an agent's own copy (cp -r ~/cbtest/server-T ~/cbwork/agents/<name>/), so two boots
# never share a mods directory
SRV="${CB_SERVER_DIR:-$HOME/cbtest/server-$T}"
[ -f "$SRV/fabric-server.jar" ] || { echo "no Fabric server at $SRV"; exit 1; }
[ -d "$WD/runtime-$T" ] || { echo "no $WD/runtime-$T (climb_runtime.sh $WD $T)"; exit 1; }
CONV="$WD/boot/$TAG"
rm -rf "$CONV" && mkdir -p "$CONV"
"$JAVA" -Xmx8g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.20.1 "$T" "$CONV/conv" "$@" \
    > "$CONV/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$CONV/climb.log"; exit 1; }
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
cp data/runtime/client-$T/mods/fabric-api-*.jar "$SRV/mods/"
cp "$WD/runtime-$T"/*.jar "$SRV/mods/"
cp "$CONV"/conv/*+"$T".jar "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods ($(ls "$SRV/mods" | wc -l)): $(ls "$SRV/mods" | tr '\n' ' ')"
LOG="$SRV/boot-$TAG.log"
cd "$SRV"
timeout 300 "$JAVA" -Xmx6g -jar fabric-server.jar nogui > "$LOG" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 140); do
    sleep 2
    grep -qaE 'Done \(|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "$LOG" && break
done
sleep 1
grep -qa 'Done (' "$LOG" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -anE 'Done \(|/ERROR|Caused by|Exception|crashed|Incompatible|Loading [0-9]+ mods|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply' "$LOG" \
    | grep -viE 'Error loading class' | head -40
kill $PID 2>/dev/null   # timeout passes the signal on to the server
wait $PID 2>/dev/null
echo "===END==="
