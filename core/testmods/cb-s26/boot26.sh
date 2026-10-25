#!/usr/bin/env bash
# boot26.sh <tag>: real-server check for s26, run on beta2 from ~/cbwork after remote_step.sh built this agent's
# engine and layer (agents/s26/core/build/classes, agents/s26/build/centurybridge-0.3.0+26.2.jar). Compiles
# core/testmods/cb-s26 against 26.1.2 (its client jar: Mojang names, as a 26.1.2 mod's build sees it), converts it
# 26.1.2 -> 26.2 with this agent's engine and wiring, and boots a Fabric 26.2 server (the agent's own copy,
# agents/s26/srv: fabric-server.jar, libraries and versions of ~/cbtest/server-26.2) with Fabric API, the layer and
# the converted mod.
set -uo pipefail
cd ~/cbwork
WD=agents/s26; TAG="$1"
JDK=~/cbtest/jdk-25.0.4.1+1/bin
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
TM=$WD/tm
mkdir -p "$TM"
R=data/runtime/client-26.1.2
CP="data/jars/client-26.1.2.jar:$(find $R/libraries -name '*.jar' | tr '\n' ':')$(find $R/.fabric/processedMods -name '*.jar' 2>/dev/null | tr '\n' ':')"
rm -rf "$TM/classes" && mkdir -p "$TM/classes"
"$JDK/javac" -nowarn -proc:none --release 25 -cp "$CP" -d "$TM/classes" $(find $WD/core/testmods/cb-s26/src -name '*.java') \
    > "$TM/javac.log" 2>&1 || { echo "test mod compile failed"; tail -20 "$TM/javac.log"; exit 1; }
cp $WD/core/testmods/cb-s26/res/* "$TM/classes/"
"$JDK/jar" cf "$TM/cb-s26.jar" -C "$TM/classes" .
SRV=$WD/srv
if [ ! -f "$SRV/fabric-server.jar" ]; then
    mkdir -p "$SRV"
    cp -r ~/cbtest/server-26.2/fabric-server.jar ~/cbtest/server-26.2/libraries ~/cbtest/server-26.2/versions \
        ~/cbtest/server-26.2/eula.txt ~/cbtest/server-26.2/server.properties.orig "$SRV/"
fi
CONV=$WD/boot/$TAG
rm -rf "$CONV" && mkdir -p "$CONV"
"$JDK/java" -Xmx4g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 26.1.2 26.2 "$CONV/conv" "$TM/cb-s26.jar" \
    > "$CONV/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$CONV/climb.log"; exit 1; }
rm -rf "$SRV/mods" "$SRV/world-$TAG" "$SRV/logs" && mkdir -p "$SRV/mods"
cp data/runtime/client-26.2/mods/fabric-api-*.jar "$SRV/mods/"
cp $WD/build/centurybridge-0.3.0+26.2.jar "$SRV/mods/"
cp "$CONV"/conv/*+26.2.jar "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 300 "$JDK/java" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 140); do
    sleep 2
    grep -qaE 'CB-S26 SERVER DONE|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-S26|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|NoSuchField|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply' "boot-$TAG.log" | head -80
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
