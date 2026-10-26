#!/usr/bin/env bash
# boot.sh <tag> [native-only]: step 1.20.4 -> 1.20.5 real-server check for smx4b. cb-smx4b (built against 1.20.4 here)
# is converted 1.20.4 -> 1.20.5 with this agent's engine, wiring and built step layer and booted on a Fabric 1.20.5
# server (own copy, <work dir>/srv-1.20.5) with Fabric API and the step layer, next to cb-smx4bn (native/, built
# against 1.20.5, not converted). "native-only" boots cb-smx4bn without cb-smx4b: no converted mod mixes the restored
# members, so every route must leave 1.20.5's path untouched and cb-smx4bn's mixins must all fire.
# Run on beta2 from ~/cbwork after the work dir's layer was built (remote_step.sh); CB_WD=<work dir> (default agents/smx4b).
set -uo pipefail
cd ~/cbwork
WD=${CB_WD:-agents/smx4b}; TAG="$1"; MODE="${2:-}"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
export PATH=$HOME/cbtest/jdk-25.0.4.1+1/bin:$PATH
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
LAYER=$WD/build/centurybridge-0.3.0+1.20.5.jar
[ -f "$LAYER" ] || { echo "no $LAYER (build the layer first)"; exit 1; }
SRV=$WD/srv-1.20.5
[ -d "$SRV" ] || { cp -r ~/cbtest/server-1.20.5 "$SRV" && rm -rf "$SRV/logs" "$SRV"/world* "$SRV/mods" && cp "$SRV/server.properties" "$SRV/server.properties.orig"; }
CONV=$WD/boot/$TAG
rm -rf "$CONV" && mkdir -p "$CONV"
NCLS=$(mktemp -d)
NCP="data/jars/client-1.20.5-intermediary.jar"
for j in $(find data/runtime/client-1.20.5/libraries -name '*.jar'); do NCP="$NCP:$j"; done
javac --release 21 -nowarn -proc:none -cp "$NCP" -d "$NCLS" $(find $WD/core/testmods/cb-smx4b/native/src -name '*.java') \
    || { echo "native test mod build failed"; exit 1; }
cp $WD/core/testmods/cb-smx4b/native/res/* "$NCLS/"
(cd "$NCLS" && jar cf "$HOME/cbwork/$CONV/cb-smx4bn.jar" .)
rm -rf "$NCLS"
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
if [ "$MODE" != "native-only" ]; then
    (cd $WD && CB_DATA=$HOME/cbwork/data bash core/testmods/cb-smx4b/build.sh "$HOME/cbwork/$CONV/cb-smx4b.jar") || { echo "test mod build failed"; exit 1; }
    "$JAVA" -Xmx4g -Dcb.wiring=$WD/core/segments/steps/1.20.4__1.20.5.json -Dcb.layerJar="$LAYER" -cp "$WD/core/build/classes:$LIBS" \
        top.dext.centurybridge.Main convert "$WD/core/segments" 1.20.4 1.20.5 "$CONV/conv" "$CONV/cb-smx4b.jar" > "$CONV/convert.log" 2>&1 \
        || { echo "convert failed"; tail -5 "$CONV/convert.log"; exit 1; }
    (cd "$CONV/conv" && for j in *.jar; do unzip -p "$j" META-INF/centurybridge/mixed/1.20.5.txt 2>/dev/null; done) | sed 's/^/mixed: /'
    cp "$CONV"/conv/*.jar "$SRV/mods/"
fi
cp data/runtime/client-1.20.5/mods/fabric-api-*.jar "$SRV/mods/"
cp "$LAYER" "$SRV/mods/"
cp "$CONV/cb-smx4bn.jar" "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 600 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 290); do
    sleep 2
    grep -qa 'CB-SMX4BN summary' "boot-$TAG.log" && { [ "$MODE" = "native-only" ] || grep -qa 'CB-SMX4B summary' "boot-$TAG.log"; } && break
    grep -qaE 'Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-SMX4B|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply|at top.dext|at cb\.' "boot-$TAG.log" | head -80
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
