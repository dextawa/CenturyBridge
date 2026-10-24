#!/usr/bin/env bash
# bootlib.sh <V> <W> <tag> <mod jar...>: one-step real-server check of slib's library bridges -- V-era mods
# converted V -> W with this agent's engine and step wiring (convert-climb V W), booted on a Fabric W server
# (agents/slib/srv-W, its own copy: a Fabric server launcher for W at loader 0.19.3) with Fabric API and the
# step's layer (build/centurybridge-0.3.0+W.jar from remote_step.sh). Prints the CB-SLIB lines.
# Run on beta2 from ~/cbwork.
set -uo pipefail
cd ~/cbwork
WD=agents/slib; V="$1"; W="$2"; TAG="$3"; shift 3
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
SRV=$WD/srv-$W
if [ ! -f "$SRV/fabric-server.jar" ]; then
    mkdir -p "$SRV"
    curl -sfL -o "$SRV/fabric-server.jar" "https://meta.fabricmc.net/v2/versions/loader/$W/0.19.3/1.1.0/server/jar" || { echo "no Fabric server launcher for $W"; exit 1; }
    echo "eula=true" > "$SRV/eula.txt"
    cp ~/cbtest/server-1.21/server.properties.orig "$SRV/server.properties.orig"
fi
CONV=$WD/boot/$TAG
rm -rf "$CONV" && mkdir -p "$CONV"
"$JAVA" -Xmx4g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" "$V" "$W" "$CONV/conv" "$@" \
    > "$CONV/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$CONV/climb.log"; exit 1; }
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
cp data/runtime/client-$W/mods/fabric-api-*.jar "$SRV/mods/"
cp $WD/build/centurybridge-0.3.0+$W.jar "$SRV/mods/"
cp "$CONV"/conv/*+$W.jar "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 400 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 190); do
    sleep 2
    grep -qaE 'CB-SLIB DONE|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -aE 'CB-SLIB|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange' "boot-$TAG.log" | head -80
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
