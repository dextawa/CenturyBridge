#!/usr/bin/env bash
# boot.sh <tag>: step 1.21.10 -> 1.21.11 real-server check for s11 -- cb-s11 (built against 1.21.10 here) converted
# 1.21.10 -> 1.21.11 with this agent's engine, wiring and built step layer, booted on a Fabric 1.21.11 server (own copy,
# agents/s11/srv-1.21.11, set up from Fabric's server launcher on first use) with Fabric API and the step layer, next
# to cb-s11n (native/, built against 1.21.11, not converted): its mixins on 1.21.11's GameRules.get / set must still fire.
# Run on beta2 from ~/cbwork after the work dir's layer was built (remote_step.sh).
set -uo pipefail
cd ~/cbwork
WD=${CB_WD:-agents/s11}; TAG="$1"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
export PATH=$HOME/cbtest/jdk-25.0.4.1+1/bin:$PATH
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
LAYER=$WD/build/centurybridge-0.3.0+1.21.11.jar
[ -f "$LAYER" ] || { echo "no $LAYER (build the layer first)"; exit 1; }
SRV=$WD/srv-1.21.11
if [ ! -f "$SRV/fabric-server.jar" ]; then
    mkdir -p "$SRV"
    curl -fsSL -o "$SRV/fabric-server.jar" "https://meta.fabricmc.net/v2/versions/loader/1.21.11/${CB_LOADER:-0.19.3}/1.1.0/server/jar" \
        || { echo "could not fetch the Fabric server launcher"; exit 1; }
    echo "eula=true" > "$SRV/eula.txt"
    cp ~/cbtest/server-1.21.4/server.properties.orig "$SRV/server.properties.orig"
fi
CONV=$WD/boot/$TAG
rm -rf "$CONV" && mkdir -p "$CONV"
(cd $WD && CB_DATA=$HOME/cbwork/data bash core/testmods/cb-s11/build.sh "$HOME/cbwork/$CONV/cb-s11.jar") || { echo "test mod build failed"; exit 1; }
NCLS=$(mktemp -d)
NCP="data/jars/client-1.21.11-intermediary.jar"
for j in $(find data/runtime/client-1.21.11/libraries data/runtime/client-1.21.11/.fabric/processedMods -name '*.jar'); do NCP="$NCP:$j"; done
javac --release 21 -nowarn -proc:none -cp "$NCP" -d "$NCLS" $(find $WD/core/testmods/cb-s11/native/src -name '*.java')     || { echo "native test mod build failed"; exit 1; }
cp $WD/core/testmods/cb-s11/native/res/* "$NCLS/"
(cd "$NCLS" && jar cf "$HOME/cbwork/$CONV/cb-s11n.jar" .)
rm -rf "$NCLS"
"$JAVA" -Xmx4g -Dcb.wiring=$WD/core/segments/steps/1.21.10__1.21.11.json -Dcb.layerJar="$LAYER" -cp "$WD/core/build/classes:$LIBS" \
    top.dext.centurybridge.Main convert "$WD/core/segments" 1.21.10 1.21.11 "$CONV/conv" "$CONV/cb-s11.jar" > "$CONV/convert.log" 2>&1 \
    || { echo "convert failed"; tail -5 "$CONV/convert.log"; exit 1; }
grep -aiE 'verdict|direct|degraded|partial' "$CONV/convert.log" | head -5
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
cp data/runtime/client-1.21.11/mods/fabric-api-*.jar "$SRV/mods/"
cp "$LAYER" "$SRV/mods/"
cp "$CONV"/conv/*.jar "$SRV/mods/"
cp "$CONV/cb-s11n.jar" "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 600 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 290); do
    sleep 2
    grep -qa 'CB-S11 summary' "boot-$TAG.log" && grep -qa 'CB-S11N summary' "boot-$TAG.log" && break
    grep -qaE 'Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-S11|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply|at top.dext|at cb\.' "boot-$TAG.log" | head -80
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
