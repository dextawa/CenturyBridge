#!/usr/bin/env bash
# boot.sh <tag> [native-only]: step 1.21.1 -> 1.21.2 real-server check for s212. cb-s212 (built against 1.21.1 here) is
# converted 1.21.1 -> 1.21.2 with this work dir's engine, wiring and built step layer and booted on a Fabric 1.21.2
# server (own copy, <work dir>/srv-1.21.2) with Fabric API and the step layer, next to cb-s212n (native/, built against
# 1.21.2, not converted). "native-only" boots cb-s212n without cb-s212: its 1.21.2 mixins must all fire and vanilla's
# answers stay 1.21.2's.
# Run on beta2 from ~/cbwork after the work dir's layer was built; CB_WD=<work dir> (default agents/s212);
# CB_SRV_SRC=<a Fabric 1.21.2 server dir to copy once> (default agents/s7item/srv-1.21.2).
set -uo pipefail
cd ~/cbwork
WD=${CB_WD:-agents/s212}; TAG="$1"; MODE="${2:-}"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
export PATH=$HOME/cbtest/jdk-25.0.4.1+1/bin:$PATH
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
LAYER=$WD/build/centurybridge-0.3.0+1.21.2.jar
[ -f "$LAYER" ] || { echo "no $LAYER (build the layer first)"; exit 1; }
SRV=$WD/srv-1.21.2
if [ ! -d "$SRV" ]; then
    SRC=${CB_SRV_SRC:-agents/s7item/srv-1.21.2}
    mkdir -p "$SRV/.fabric" && cp -r "$SRC/fabric-server.jar" "$SRC/libraries" "$SRC/versions" "$SRC/eula.txt" "$SRC/server.properties.orig" "$SRV/" \
        && cp -r "$SRC/.fabric/server" "$SRC/.fabric/remappedJars" "$SRV/.fabric/" \
        || { echo "cannot copy a 1.21.2 server from $SRC"; exit 1; }
fi
CONV=$WD/boot/$TAG
rm -rf "$CONV" && mkdir -p "$CONV"
NCLS=$(mktemp -d)
NCP="data/jars/client-1.21.2-intermediary.jar"
for j in $(find data/runtime/client-1.21.2/libraries -name '*.jar'); do NCP="$NCP:$j"; done
javac --release 21 -nowarn -proc:none -cp "$NCP" -d "$NCLS" $(find $WD/core/testmods/cb-s212/native/src -name '*.java') \
    || { echo "native test mod build failed"; exit 1; }
cp $WD/core/testmods/cb-s212/native/res/* "$NCLS/"
(cd "$NCLS" && jar cf "$HOME/cbwork/$CONV/cb-s212n.jar" .)
rm -rf "$NCLS"
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
if [ "$MODE" != "native-only" ]; then
    (cd $WD && CB_DATA=$HOME/cbwork/data bash core/testmods/cb-s212/build.sh "$HOME/cbwork/$CONV/cb-s212.jar") || { echo "test mod build failed"; exit 1; }
    "$JAVA" -Xmx4g -Dcb.wiring=$WD/core/segments/steps/1.21.1__1.21.2.json -Dcb.layerJar="$LAYER" -cp "$WD/core/build/classes:$LIBS" \
        top.dext.centurybridge.Main convert "$WD/core/segments" 1.21.1 1.21.2 "$CONV/conv" "$CONV/cb-s212.jar" > "$CONV/convert.log" 2>&1 \
        || { echo "convert failed"; tail -5 "$CONV/convert.log"; exit 1; }
    grep -aE "mixin (bridged|not bridged|repaired|disabled)" "$CONV/conv/report.md" 2>/dev/null | head -20
    cp "$CONV"/conv/*.jar "$SRV/mods/"
fi
cp data/runtime/client-1.21.2/mods/fabric-api-*.jar "$SRV/mods/"
cp "$LAYER" "$SRV/mods/"
cp "$CONV/cb-s212n.jar" "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 600 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 290); do
    sleep 2
    grep -qa 'CB-S212N summary' "boot-$TAG.log" && { [ "$MODE" = "native-only" ] || grep -qa 'CB-S212 summary' "boot-$TAG.log"; } && break
    grep -qaE 'Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-S212|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply|at top.dext|at cb\.' "boot-$TAG.log" | head -80
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
