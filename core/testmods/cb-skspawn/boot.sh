#!/usr/bin/env bash
# boot.sh <tag> [native-only|native-nolayer]: step 1.20.4 -> 1.20.5 real-server check for SpawnPlacements.Data (Kilt's
# SpawnPlacementsDataAccessor.getPlacement and new Data(heightmap, Type, predicate)). cb-skspawn (built against the 1.20.4
# stub widened by its own access widener) is converted 1.20.4 -> 1.20.5 with the work dir's engine, wiring and built
# step layer and booted on a Fabric 1.20.5 server (<work dir>/srv-1.20.5) with Fabric API and the layer, next to
# cb-skspawnn (native 1.20.5, not converted). Expected: "CB-SKSPAWN summary ok=4 fail=0" and
# "CB-SKSPAWNN summary ok=3 fail=0"; native-only boots cb-skspawnn with the layer and no converted mod,
# native-nolayer without the layer too (its three checks, vanilla-field-read included, must pass in both).
# Run on beta2 from ~/cbwork after the work dir's layer was built (remote_step.sh); CB_WD=<work dir> (default agents/skilteng).
set -uo pipefail
cd ~/cbwork
WD=${CB_WD:-agents/skilteng}; TAG="$1"; MODE="${2:-}"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
export PATH=$HOME/cbtest/jdk-25.0.4.1+1/bin:$PATH
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
LAYER=$WD/build/centurybridge-0.3.0+1.20.5.jar
[ -f "$LAYER" ] || { echo "no $LAYER (build the layer first)"; exit 1; }
SRV=$WD/srv-1.20.5
[ -d "$SRV" ] || { cp -r ~/cbtest/server-1.20.5 "$SRV" && rm -rf "$SRV/logs" "$SRV"/world* "$SRV/mods" && cp "$SRV/server.properties" "$SRV/server.properties.orig"; }
[ -f "$SRV/fabric-server.jar" ] || { echo "no fabric-server.jar in $SRV"; exit 1; }
CONV=$WD/boot/$TAG
rm -rf "$CONV" && mkdir -p "$CONV/a" "$CONV/n"
T=core/testmods/cb-skspawn
NSTUB=$CONV/stub-1.20.5.jar
(cd $WD && CB_DATA=$HOME/cbwork/data bash core/tools/make_aw_stub.sh 1.20.5 $HOME/cbwork/data/jars/client-1.20.5-intermediary.jar     $HOME/cbwork/$NSTUB $T/native/res/cb_skspawnn.accesswidener > /dev/null) || { echo "aw stub 1.20.5 failed"; exit 1; }
NCP="$NSTUB"
for j in $(find data/runtime/client-1.20.5/libraries -name '*.jar'); do NCP="$NCP:$j"; done
javac --release 21 -nowarn -proc:none -cp "$NCP" -d "$CONV/n" $(find $WD/$T/native/src -name '*.java') \
    || { echo "native test mod build failed"; exit 1; }
cp $WD/$T/native/res/* "$CONV/n/" && jar cf "$CONV/cb-skspawnn.jar" -C "$CONV/n" . && rm -f "$NSTUB"
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
if [ "$MODE" != "native-only" ] && [ "$MODE" != "native-nolayer" ]; then
    STUB=$CONV/stub-1.20.4.jar
    (cd $WD && CB_DATA=$HOME/cbwork/data bash core/tools/make_aw_stub.sh 1.20.4 $HOME/cbwork/data/jars/client-1.20.4-intermediary.jar \
        $HOME/cbwork/$STUB $T/res/cb_skspawn.accesswidener > /dev/null) || { echo "aw stub 1.20.4 failed"; exit 1; }
    R=data/runtime/client-1.20.4
    javac --release 17 -nowarn -proc:none -cp "$STUB:$(find $R/libraries -name '*.jar' | tr '\n' ':')" -d "$CONV/a" \
        $(find $WD/$T/src -name '*.java') || { echo "test mod build failed"; exit 1; }
    cp $WD/$T/res/* "$CONV/a/" && jar cf "$CONV/cb-skspawn.jar" -C "$CONV/a" . && rm -f "$STUB"
    "$JAVA" -Xmx4g -Dcb.wiring=$WD/core/segments/steps/1.20.4__1.20.5.json -Dcb.layerJar="$LAYER" -cp "$WD/core/build/classes:$LIBS" \
        top.dext.centurybridge.Main convert "$WD/core/segments" 1.20.4 1.20.5 "$CONV/conv" "$CONV/cb-skspawn.jar" > "$CONV/convert.log" 2>&1 \
        || { echo "convert failed"; tail -5 "$CONV/convert.log"; exit 1; }
    grep -aE 'class_1318|DataAccessor' "$CONV"/conv/report.md 2>/dev/null | head -10
    cp "$CONV"/conv/*.jar "$SRV/mods/"
fi
cp data/runtime/client-1.20.5/mods/fabric-api-*.jar "$SRV/mods/"
[ "$MODE" = "native-nolayer" ] || cp "$LAYER" "$SRV/mods/"
cp "$CONV/cb-skspawnn.jar" "$SRV/mods/"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 600 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 290); do
    sleep 2
    grep -qa 'CB-SKSPAWNN summary' "boot-$TAG.log" && { [ "$MODE" = "native-only" ] || [ "$MODE" = "native-nolayer" ] || grep -qa 'CB-SKSPAWN summary' "boot-$TAG.log"; } && break
    grep -qaE 'Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-SKSPAWN|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|NoSuchField|AbstractMethod|VerifyError|IllegalAccess|IncompatibleClassChange|Mixin apply|at top.dext|at cb\.' "boot-$TAG.log" | head -60
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
