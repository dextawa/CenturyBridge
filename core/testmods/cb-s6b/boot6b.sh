#!/usr/bin/env bash
# boot6b.sh <tag>: step-6b real-server check -- cb-s6b (compiled against 1.21.5) converted 1.21.5 -> 1.21.6 with this
# agent's engine and wiring, booted on a Fabric 1.21.6 server (the agent's own copy, srv-1.21.6, made on first use with
# Fabric's server launcher) with Fabric API and this agent's step layer. Run on beta2 from ~/cbwork after remote_step
# built the layer (agents/<CB_AGENT, default s6b>/build/centurybridge-0.3.0+1.21.6.jar).
set -uo pipefail
cd ~/cbwork
WD=agents/${CB_AGENT:-s6b}; TAG="$1"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
JAVAC=~/cbtest/jdk-25.0.4.1+1/bin/javac
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
SRV=$WD/srv-1.21.6
if [ ! -f "$SRV/fabric-server.jar" ]; then
    mkdir -p "$SRV"
    curl -sfL -o "$SRV/fabric-server.jar" "https://meta.fabricmc.net/v2/versions/loader/1.21.6/0.19.3/1.0.3/server/jar" || { echo "no launcher"; exit 1; }
    echo "eula=true" > "$SRV/eula.txt"
    printf 'online-mode=false\nlevel-type=minecraft\\:flat\nspawn-protection=0\nmax-tick-time=-1\nlevel-name=world\n' > "$SRV/server.properties.orig"
fi
# the test mod, against 1.21.5's intermediary stub and the Fabric loader's API
mkdir -p $WD/boot; B=$WD/boot/$TAG; rm -rf "$B" && mkdir -p "$B/cls"
FV=$WD/boot/fapi-1.21.5; if [ ! -d "$FV" ]; then mkdir -p "$FV" && (cd "$FV" && unzip -qo ~/cbwork/data/runtime/client-1.21.5/mods/fabric-api-*.jar 'META-INF/jars/*'); fi
CP="data/jars/client-1.21.5-intermediary.jar:$(ls $FV/META-INF/jars/*.jar | tr '\n' ':'):$(find data/runtime/server-1.21.6/libraries data/runtime/client-1.21.6/libraries -name '*.jar' | tr '\n' ':')"
"$JAVAC" -nowarn -proc:none --release 21 -cp "$CP" -d "$B/cls" $(find $WD/core/testmods/cb-s6b/src -name '*.java') 2>&1 | grep -v '^Note\|^注' | head -20
cp $WD/core/testmods/cb-s6b/res/* "$B/cls/"
(cd "$B/cls" && "${JAVA%java}jar" cf ../cb-s6b.jar .)
"$JAVA" -Xmx4g -Dcb.layerJar=$WD/build/centurybridge-0.3.0+1.21.6.jar -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.21.5 1.21.6 "$B/conv" "$B/cb-s6b.jar" \
    > "$B/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$B/climb.log"; exit 1; }
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
cp data/runtime/client-1.21.6/mods/fabric-api-*.jar "$SRV/mods/"
cp $WD/build/centurybridge-0.3.0+1.21.6.jar "$SRV/mods/"
cp "$B"/conv/*+1.21.6.jar "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 400 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 190); do
    sleep 2
    grep -qaE 'CB-S6B DONE|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-S6B|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply' "boot-$TAG.log" | head -80
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
