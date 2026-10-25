#!/usr/bin/env bash
# boot5.sh <tag>: real-server check for s5, run on beta2 from ~/cbwork after remote_step.sh built this agent's engine
# and layer (agents/s5/core/build/classes, agents/s5/build/centurybridge-0.3.0+1.21.5.jar). Compiles
# core/testmods/cb-s5 against 1.21.4 (its intermediary client jar, libraries and Fabric API modules, as a 1.21.4 mod's
# build sees them), converts it 1.21.4 -> 1.21.5 with this agent's engine and wiring, and boots a Fabric 1.21.5 server
# (the agent's own, agents/s5/srv-1.21.5: Fabric's server launcher for 1.21.5 / loader 0.19.3, which fetches the
# game and libraries on its first start) with Fabric API, the layer and the converted mod. Prints the "CB-S5" lines.
set -uo pipefail
cd ~/cbwork
WD=agents/s5; TAG="$1"
JDK=~/cbtest/jdk-25.0.4.1+1/bin
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
TM=$WD/tm
R=data/runtime/client-1.21.4
CP="data/jars/client-1.21.4-intermediary.jar:$(find $R/libraries $R/.fabric/processedMods -name '*.jar' | tr '\n' ':')"
rm -rf "$TM" && mkdir -p "$TM/classes"
"$JDK/javac" -nowarn -proc:none --release 21 -cp "$CP" -d "$TM/classes" $(find $WD/core/testmods/cb-s5/src -name '*.java') \
    > "$TM/javac.log" 2>&1 || { echo "test mod compile failed"; tail -20 "$TM/javac.log"; exit 1; }
cp $WD/core/testmods/cb-s5/res/* "$TM/classes/"
"$JDK/jar" cf "$TM/cb-s5.jar" -C "$TM/classes" .
SRV=$WD/srv-1.21.5
if [ ! -f "$SRV/fabric-server.jar" ]; then
    mkdir -p "$SRV"
    curl -fsSL -o "$SRV/fabric-server.jar" "https://meta.fabricmc.net/v2/versions/loader/1.21.5/0.19.3/1.1.0/server/jar" \
        || { echo "server launcher download failed"; exit 1; }
    cp ~/cbtest/server-1.21.4/eula.txt ~/cbtest/server-1.21.4/server.properties.orig "$SRV/"
fi
CONV=$WD/boot/$TAG
rm -rf "$CONV" && mkdir -p "$CONV"
"$JDK/java" -Xmx4g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.21.4 1.21.5 "$CONV/conv" "$TM/cb-s5.jar" \
    > "$CONV/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$CONV/climb.log"; exit 1; }
rm -rf "$SRV/mods" "$SRV/world-$TAG" "$SRV/logs" && mkdir -p "$SRV/mods"
cp data/runtime/client-1.21.5/mods/fabric-api-*.jar "$SRV/mods/"
cp $WD/build/centurybridge-0.3.0+1.21.5.jar "$SRV/mods/"
cp "$CONV"/conv/*+1.21.5.jar "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 400 "$JDK/java" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 190); do
    sleep 2
    grep -qaE 'CB-S5 DONE|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-S5|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|NoSuchField|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply' "boot-$TAG.log" | head -80
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
