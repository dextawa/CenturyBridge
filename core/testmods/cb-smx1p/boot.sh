#!/usr/bin/env bash
# boot.sh <tag>: step 1.21.10 -> 1.21.11 real-server check of the int permission levels and RespawnAnchorBlock.canSetSpawn --
# cb-smx1p (built against 1.21.10 here) converted 1.21.10 -> 1.21.11 with this work dir's engine, wiring and built step
# layer, booted on a Fabric 1.21.11 server (own copy, <wd>/srv-1.21.11, from Fabric's server launcher on first use) with
# Fabric API and the step layer.
# Run on beta2 from ~/cbwork after the work dir's layer was built (remote_step.sh).
set -uo pipefail
cd ~/cbwork
WD=${CB_WD:-agents/smx1f}; TAG="$1"
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
(cd $WD && CB_DATA=$HOME/cbwork/data bash core/testmods/cb-smx1p/build.sh "$HOME/cbwork/$CONV/cb-smx1p.jar") || { echo "test mod build failed"; exit 1; }
"$JAVA" -Xmx4g -Dcb.wiring=$WD/core/segments/steps/1.21.10__1.21.11.json -Dcb.layerJar="$LAYER" -cp "$WD/core/build/classes:$LIBS" \
    top.dext.centurybridge.Main convert "$WD/core/segments" 1.21.10 1.21.11 "$CONV/conv" "$CONV/cb-smx1p.jar" > "$CONV/convert.log" 2>&1 \
    || { echo "convert failed"; tail -5 "$CONV/convert.log"; exit 1; }
grep -aiE 'verdict|direct|degraded|partial' "$CONV/convert.log" | head -5
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
cp data/runtime/client-1.21.11/mods/fabric-api-*.jar "$SRV/mods/"
cp "$LAYER" "$SRV/mods/"
cp "$CONV"/conv/*.jar "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 600 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 290); do
    sleep 2
    grep -qa 'CB-SMX1P summary' "boot-$TAG.log" && break
    grep -qaE 'Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-SMX1P|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply|at top.dext|at cb\.' "boot-$TAG.log" | head -80
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
