#!/usr/bin/env bash
# bootstagswall.sh <tag> <mod jar...>: real-server check of the tag-drift bridge at the wall, 1.21.11 -> 26.1.
# 1.21.11 mods are converted one step with this agent's engine and wiring (Main convert-climb), the agent's 26.1 step
# layer is built through build.sh's gates, and a Fabric 26.1 server (the agent's own copy of swall's server, own port)
# boots with Fabric API, the layer and the converted mods. Prints the result and the CBTEST lines. Run on beta2 from
# ~/cbwork after remote_step.sh has uploaded the agent's tree.
set -uo pipefail
cd ~/cbwork
WD=agents/stags; TAG="$1"; shift 1
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
export PATH=$HOME/cbtest/jdk-25.0.4.1+1/bin:$PATH
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
SRV=$WD/srv261
if [ ! -f "$SRV/fabric-server.jar" ]; then
    mkdir -p "$SRV"
    cp -r agents/swall/srv/fabric-server.jar agents/swall/srv/libraries agents/swall/srv/versions \
        agents/swall/srv/eula.txt agents/swall/srv/server.properties.orig "$SRV/"
    sed -i 's/^server-port=.*/server-port=25892/; s/^query.port=.*/query.port=25892/; s/^level-name=.*/level-name=world/' \
        "$SRV/server.properties.orig"
fi
(cd $WD && CB_DATA=$HOME/cbwork/data CB_SHIM=core/shims/steps/26.1 CB_WIRING=core/segments/steps/1.21.11__26.1.json \
    bash build.sh 26.1 build > build-26.1.log 2>&1) || { echo "layer build failed"; tail -20 $WD/build-26.1.log; exit 1; }
CONV=$WD/boot/$TAG
rm -rf "$CONV" && mkdir -p "$CONV"
"$JAVA" -Xmx4g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.21.11 26.1 "$CONV/conv" "$@" \
    > "$CONV/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$CONV/climb.log"; exit 1; }
rm -rf "$SRV/mods" "$SRV/world" "$SRV/logs" && mkdir -p "$SRV/mods"
cp data/runtime/client-26.1/mods/fabric-api-*.jar "$SRV/mods/"
cp $WD/build/centurybridge-0.3.0+26.1.jar "$SRV/mods/"
cp "$CONV"/conv/*+26.1.jar "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 400 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 180); do
    sleep 2
    grep -qaE 'CBTEST SUMMARY|crashed|Crash report|Exception in thread' "boot-$TAG.log" && break
done
sleep 3
kill $PID 2>/dev/null
wait $PID 2>/dev/null
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CBTEST|ERROR|Caused by|Exception|Mixin apply|crash' "boot-$TAG.log" | head -60
rm -rf world
