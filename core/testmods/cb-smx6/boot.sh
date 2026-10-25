#!/usr/bin/env bash
# boot.sh <tag> <mod jar...>: step 1.20.6 -> 1.21 real-server check for smx6 -- 1.20.6 mods converted 1.20.6 -> 1.21 with
# this agent's engine, wiring and built step layer (-Dcb.layerJar: mixins on members the layer restores stay there),
# booted on a Fabric 1.21 server (own copy, agents/smx6/srv-1.21) with Fabric API and the step layer. Run on beta2 from
# ~/cbwork after the work dir's layer was built (remote_step.sh).
set -uo pipefail
cd ~/cbwork
WD=agents/smx6; TAG="$1"; shift 1
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
LAYER=$WD/build/centurybridge-0.3.0+1.21.jar
[ -f "$LAYER" ] || { echo "no $LAYER (build the layer first)"; exit 1; }
SRV=$WD/srv-1.21
[ -d "$SRV" ] || { cp -r ~/cbtest/server-1.21 "$SRV" && rm -rf "$SRV/logs" "$SRV"/world*; }
CONV=$WD/boot/$TAG
rm -rf "$CONV" && mkdir -p "$CONV"
"$JAVA" -Xmx4g -Dcb.wiring=$WD/core/segments/steps/1.20.6__1.21.json -Dcb.layerJar="$LAYER" -cp "$WD/core/build/classes:$LIBS" \
    top.dext.centurybridge.Main convert "$WD/core/segments" 1.20.6 1.21 "$CONV/conv" "$@" > "$CONV/convert.log" 2>&1 \
    || { echo "convert failed"; tail -5 "$CONV/convert.log"; exit 1; }
grep -aE 'restored member|isChanneling|method_31551' "$CONV"/conv/*report* 2>/dev/null | head -10
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
cp data/runtime/client-1.21/mods/fabric-api-*.jar "$SRV/mods/"
cp "$LAYER" "$SRV/mods/"
cp "$CONV"/conv/*.jar "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 400 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 190); do
    sleep 2
    grep -qaE 'Done \(|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-SMX6|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply|at top.dext|at cb\.' "boot-$TAG.log" | head -60
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
