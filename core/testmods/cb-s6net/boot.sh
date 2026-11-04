#!/usr/bin/env bash
# Real behavior check for s6net. Run on beta2 from ~/cbwork after remote_step.sh and after uploading
# core/testmods/cb-s6net into agents/cbgoal-s6-net/core/testmods/. Compiles the fixture against 1.21.5,
# converts it one step with the agent's engine/layer, and boots it on a real Fabric 1.21.6 server.
set -uo pipefail
cd ~/cbwork
WD=agents/${CB_AGENT:-cbgoal-s6-net}; TAG="${1:-roundtrip}"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
JAVAC=~/cbtest/jdk-25.0.4.1+1/bin/javac
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
SRV=$WD/srv-s6net-1.21.6
if [ ! -f "$SRV/fabric-server.jar" ]; then
    mkdir -p "$SRV"
    curl -sfL -o "$SRV/fabric-server.jar" "https://meta.fabricmc.net/v2/versions/loader/1.21.6/0.19.3/1.0.3/server/jar" || { echo "no launcher"; exit 1; }
    echo "eula=true" > "$SRV/eula.txt"
    printf 'online-mode=false\nlevel-type=minecraft\\:flat\nspawn-protection=0\nmax-tick-time=-1\nsync-chunk-writes=false\n' > "$SRV/server.properties.orig"
fi
B=$WD/boot/s6net-$TAG; rm -rf "$B" && mkdir -p "$B/cls"
CP="data/jars/client-1.21.5-intermediary.jar:$(find data/runtime/server-1.21.6/libraries data/runtime/client-1.21.6/libraries -name '*.jar' | tr '\n' ':')"
"$JAVAC" -nowarn -proc:none --release 21 -cp "$CP" -d "$B/cls" $(find "$WD/core/testmods/cb-s6net/src" -name '*.java') \
    || { echo "fixture compilation failed"; exit 1; }
cp "$WD/core/testmods/cb-s6net/res/fabric.mod.json" "$B/cls/"
(cd "$B/cls" && "${JAVA%java}jar" cf ../cb-s6net.jar .)
"$JAVA" -Xmx4g -Dcb.layerJar="$WD/build/centurybridge-0.3.0+1.21.6.jar" -cp "$WD/core/build/classes:$LIBS" \
    top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.21.5 1.21.6 "$B/conv" "$B/cb-s6net.jar" \
    > "$B/climb.log" 2>&1 || { echo "climb failed"; tail -20 "$B/climb.log"; exit 1; }
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
cp data/runtime/client-1.21.6/mods/fabric-api-*.jar "$SRV/mods/"
cp "$WD/build/centurybridge-0.3.0+1.21.6.jar" "$SRV/mods/"
cp "$B"/conv/*+1.21.6.jar "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
printf '\nlevel-name=world-%s\n' "$TAG" >> "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 400 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-s6net-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 190); do
    sleep 2
    grep -qaE 'CB-S6NET DONE|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-s6net-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-s6net-$TAG.log" && echo "===RESULT: SERVER DONE===" || echo "===RESULT: SERVER NOT DONE==="
grep -aE 'CB-S6NET|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply' "boot-s6net-$TAG.log" | head -100
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
