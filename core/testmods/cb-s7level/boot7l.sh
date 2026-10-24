#!/usr/bin/env bash
# boot7l.sh <tag>: step-7 real-server check for s7level, run on beta2 from ~/cbwork after remote_step.sh built this
# agent's engine and layer (agents/s7level/core/build/classes, agents/s7level/build/centurybridge-0.3.0+1.21.2.jar).
# Compiles core/testmods/cb-s7level against 1.21.1 (its intermediary stub with 1.21.1 Fabric API's access wideners,
# as a mod's build sees it), converts it 1.21.1 -> 1.21.2 with this agent's engine and wiring, and boots a Fabric
# 1.21.2 server (the agent's own copy, agents/s7level/srv-1.21.2) with Fabric API, the layer and the converted mod.
set -uo pipefail
cd ~/cbwork
WD=agents/s7level; TAG="$1"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
JAVAC=~/cbtest/jdk-25.0.4.1+1/bin/javac
JAR=~/cbtest/jdk-25.0.4.1+1/bin/jar
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
TM=$WD/tm
mkdir -p "$TM"
# the 1.21.1 compile stub a mod sees: Fabric API's access wideners applied (empty own AW)
if [ ! -f "$TM/v-aw.jar" ]; then
    : > "$TM/none.aw"; printf 'accessWidener\tv2\tintermediary\n' > "$TM/none.aw"
    (cd "$WD" && PATH="$(dirname "$JAVA"):$PATH" CB_DATA=$HOME/cbwork/data bash core/tools/make_aw_stub.sh 1.21.1 "$HOME/cbwork/data/jars/client-1.21.1-intermediary.jar" \
        "$HOME/cbwork/$TM/v-aw.jar" "$HOME/cbwork/$TM/none.aw") > "$TM/aw.log" 2>&1 || { echo "aw stub failed"; tail -5 "$TM/aw.log"; exit 1; }
fi
R=data/runtime/client-1.21.1
CP="$TM/v-aw.jar:$(find $R/libraries -name '*.jar' | tr '\n' ':')$(find $R/.fabric/processedMods -name '*.jar' | tr '\n' ':')"
rm -rf "$TM/classes" && mkdir -p "$TM/classes"
"$JAVAC" -nowarn -proc:none --release 21 -cp "$CP" -d "$TM/classes" $(find $WD/core/testmods/cb-s7level/src -name '*.java') \
    > "$TM/javac.log" 2>&1 || { echo "test mod compile failed"; tail -20 "$TM/javac.log"; exit 1; }
cp $WD/core/testmods/cb-s7level/res/* "$TM/classes/"
"$JAR" cf "$TM/cb-s7level.jar" -C "$TM/classes" .
SRV=$WD/srv-1.21.2
CONV=$WD/boot/$TAG
rm -rf "$CONV" && mkdir -p "$CONV"
"$JAVA" -Xmx4g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.21.1 1.21.2 "$CONV/conv" "$TM/cb-s7level.jar" \
    > "$CONV/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$CONV/climb.log"; exit 1; }
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
cp data/runtime/client-1.21.2/mods/fabric-api-*.jar "$SRV/mods/"
cp $WD/build/centurybridge-0.3.0+1.21.2.jar "$SRV/mods/"
cp "$CONV"/conv/*+1.21.2.jar "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 300 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 140); do
    sleep 2
    grep -qaE 'CB-LEVELTEST DONE|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-LEVELTEST|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange|IllegalAccess|Mixin apply' "boot-$TAG.log" | head -80
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
