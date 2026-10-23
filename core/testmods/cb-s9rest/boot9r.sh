#!/usr/bin/env bash
# boot9r.sh <tag> <mod jar...>: step-9 real-server check for s9rest -- 1.21.3 mods converted
# 1.21.3 -> 1.21.4 with this agent's engine and wiring, booted on a Fabric 1.21.4 server (own copy) with Fabric API
# and this agent's step-9 layer.
set -uo pipefail
cd ~/cbwork
WD=agents/s9rest; TAG="$1"; PROPS="-"; shift 1
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
SRV=$WD/srv-1.21.4
CONV=$WD/boot/$TAG
rm -rf "$CONV" && mkdir -p "$CONV"
"$JAVA" -Xmx4g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.21.3 1.21.4 "$CONV/conv" "$@" \
    > "$CONV/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$CONV/climb.log"; exit 1; }
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
cp data/runtime/client-1.21.4/mods/fabric-api-*.jar "$SRV/mods/"
cp $WD/build/centurybridge-0.3.0+1.21.4.jar "$SRV/mods/"
cp "$CONV"/conv/*+1.21.4.jar "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
if [ "$PROPS" != "-" ]; then
    IFS=',' read -ra KV <<< "$PROPS"
    for kv in "${KV[@]}"; do k="${kv%%=*}"; sed -i "/^$k=/d" "$SRV/server.properties"; echo "$kv" >> "$SRV/server.properties"; done
fi
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 300 "$JAVA" ${CB_JVMARGS:-} -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 140); do
    sleep 2
    grep -qaE 'CB-S9TEST DONE|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-S9TEST|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply' "boot-$TAG.log" | head -80
grep -aE "^(spawn-|level-name)" server.properties
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
