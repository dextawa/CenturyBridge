#!/usr/bin/env bash
# boot6f.sh <tag> <mod jar...>: step-6 real-server check for s6fin -- 1.20.6 mods converted 1.20.6 -> 1.21 with this
# agent's engine and wiring, booted on a Fabric 1.21 server (own copy, agents/s6fin/srv-1.21) with Fabric API and
# this agent's step-6 layer. Run on beta2 from ~/cbwork after remote_step.sh built the layer.
set -uo pipefail
cd ~/cbwork
WD=agents/s6fin; TAG="$1"; shift 1
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
SRV=$WD/srv-1.21
[ -d "$SRV" ] || { cp -r ~/cbtest/server-1.21 "$SRV" && rm -rf "$SRV/logs" "$SRV"/world*; }
CONV=$WD/boot/$TAG
rm -rf "$CONV" && mkdir -p "$CONV"
"$JAVA" -Xmx4g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.20.6 1.21 "$CONV/conv" "$@" \
    > "$CONV/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$CONV/climb.log"; exit 1; }
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
cp data/runtime/client-1.21/mods/fabric-api-*.jar "$SRV/mods/"
cp $WD/build/centurybridge-0.3.0+1.21.jar "$SRV/mods/"
cp "$CONV"/conv/*+1.21.jar "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 400 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 190); do
    sleep 2
    grep -qaE 'DONE [0-9]+/|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-[A-Z0-9]+TEST|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply|at top.dext|at cb\.' "boot-$TAG.log" | head -90
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
