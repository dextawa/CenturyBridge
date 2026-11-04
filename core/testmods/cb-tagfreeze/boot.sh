#!/usr/bin/env bash
# boot.sh <workDir> <mode>: step 1.21.1 -> 1.21.2 real-server check of the freeze fix (CbItemRegistries.method_40276,
# Kilt's "Unbound tags" stop). <workDir> holds the compiled engine and the built 1.21.2 step layer (remote_step.sh).
#   native-nolayer  cb-tfn (native 1.21.2) alone: vanilla refuses an unbound set, bindAllTagsToEmpty-then-freeze passes,
#                   vanilla code refreezing a registry a mixin unfroze is refused ("Tags already present before freezing")
#   native-layer    the same with the work dir's layer: must print the same lines
#   conv            + cb-tf (built against 1.21.1) converted 1.21.1 -> 1.21.2 with the work dir's engine and wiring:
#                   freeze passes, the never-bound tag empty, the bound tag keeps its holder; then freeze -> unfreeze ->
#                   add -> freeze passes (1.21.1's refreeze, CbItemRegistries' redirect)
# Run on beta2 from ~/cbwork. Server: <workDir>/srv-1.21.2, copied once from agents/skilteng/srv/srv-1.21.2
# (CB_SRV_SRC to override).
set -uo pipefail
cd ~/cbwork
WD="$1"; MODE="$2"; TAG=tf-$MODE
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
export PATH=$HOME/cbtest/jdk-25.0.4.1+1/bin:$PATH
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
LAYER=$WD/build/centurybridge-0.3.0+1.21.2.jar
TM=$WD/tmtf; rm -rf "$TM" && mkdir -p "$TM"
cpof() { local cp="data/jars/client-$1-intermediary.jar" j; for j in $(find data/runtime/client-$1/libraries -name '*.jar'); do cp="$cp:$j"; done; echo "$cp"; }
mk() { # <ver> <src> <res> <jar>
    rm -rf "$TM/c" && mkdir -p "$TM/c"
    javac --release 21 -nowarn -proc:none -cp "$(cpof $1)" -d "$TM/c" $(find "$2" -name '*.java') > "$TM/javac-$4.log" 2>&1 \
        || { echo "compile $4 failed"; tail -20 "$TM/javac-$4.log"; exit 1; }
    cp "$3"/* "$TM/c/" && (cd "$TM/c" && jar cf "$HOME/cbwork/$TM/$4" .)
}
mk 1.21.2 $WD/core/testmods/cb-tagfreeze/native/src $WD/core/testmods/cb-tagfreeze/native/res cb-tfn.jar
SRV=$WD/srv-1.21.2
if [ ! -f "$SRV/fabric-server.jar" ]; then
    SRC=${CB_SRV_SRC:-agents/skilteng/srv/srv-1.21.2}
    mkdir -p "$SRV/.fabric" && cp -r "$SRC/fabric-server.jar" "$SRC/libraries" "$SRC/versions" "$SRC/eula.txt" "$SRC/server.properties.orig" "$SRV/" \
        && { cp -r "$SRC/.fabric/server" "$SRC/.fabric/remappedJars" "$SRV/.fabric/" 2>/dev/null || true; } \
        || { echo "cannot copy a 1.21.2 server from $SRC"; exit 1; }
fi
rm -rf "$SRV/mods" "$SRV/world-$TAG" "$SRV/logs" && mkdir -p "$SRV/mods"
cp data/runtime/client-1.21.2/mods/fabric-api-*.jar "$TM/cb-tfn.jar" "$SRV/mods/"
if [ "$MODE" != native-nolayer ]; then
    [ -f "$LAYER" ] || { echo "no $LAYER"; exit 1; }
    cp "$LAYER" "$SRV/mods/"
fi
if [ "$MODE" = conv ]; then
    mk 1.21.1 $WD/core/testmods/cb-tagfreeze/src $WD/core/testmods/cb-tagfreeze/res cb-tf.jar
    "$JAVA" -Xmx4g -Dcb.wiring=$WD/core/segments/steps/1.21.1__1.21.2.json -Dcb.layerJar="$LAYER" -cp "$WD/core/build/classes:$LIBS" \
        top.dext.centurybridge.Main convert "$WD/core/segments" 1.21.1 1.21.2 "$TM/conv" "$TM/cb-tf.jar" > "$TM/convert.log" 2>&1 \
        || { echo "convert failed"; tail -5 "$TM/convert.log"; exit 1; }
    grep -a "method_40276" "$TM/conv/full-audit.tsv" 2>/dev/null | head -3
    cp "$TM"/conv/*.jar "$SRV/mods/"
fi
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 400 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 190); do
    sleep 2
    grep -qa 'CB-TFN summary' "boot-$TAG.log" && { [ "$MODE" != conv ] || grep -qa 'CB-TF summary' "boot-$TAG.log"; } && break
    grep -qaE 'Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-TF|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply' "boot-$TAG.log" | head -40
kill $PID 2>/dev/null; wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
