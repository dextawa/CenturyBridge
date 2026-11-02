#!/usr/bin/env bash
# boot.sh <workDir> <mode>: the 26.2 Slime/AbstractCubeMob baseGuard check (MixinBridge.baseGuard, moved-out subclass a loss).
#   native-nolayer  cb-slmn (native 26.2, AbstractCubeMob.tick FIELD squish inject) alone: fires for Slime and MagmaCube
#   native-layer    the same with the work dir's 26.2 step layer: must print the same lines
#   conv            + cb-slm (built against 26.1.2: Slime.tick FIELD squish inject, Slime.tick HEAD inject) converted
#                   26.1.2 -> 26.2 with the work dir's engine: convField/convHead fire for Slime, 0 for MagmaCube (the loss)
# Run on beta2 from ~/cbwork after the work dir's engine and 26.2 layer were built. Server: <workDir>/srv-slm (copied once
# from ~/cbtest/server-26.2).
set -uo pipefail
cd ~/cbwork
WD="$1"; MODE="$2"; TAG=slm-$MODE
JDK=~/cbtest/jdk-25.0.4.1+1/bin
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
LAYER=$WD/build/centurybridge-0.3.0+26.2.jar
TM=$WD/tmslm; rm -rf "$TM" && mkdir -p "$TM"
cpof() { local R=data/runtime/client-$1; echo "data/jars/client-$1.jar:$(find $R/libraries -name '*.jar' | tr '\n' ':')$(find $R/.fabric/processedMods -name '*.jar' 2>/dev/null | tr '\n' ':')"; }
mk() { # <ver> <src> <res> <jar>
    rm -rf "$TM/c" && mkdir -p "$TM/c"
    "$JDK/javac" -nowarn -proc:none --release 25 -cp "$(cpof $1)" -d "$TM/c" $(find "$2" -name '*.java') > "$TM/javac-$4.log" 2>&1 \
        || { echo "compile $4 failed"; tail -20 "$TM/javac-$4.log"; exit 1; }
    cp "$3"/* "$TM/c/" && "$JDK/jar" cf "$TM/$4" -C "$TM/c" .
}
mk 26.2 $WD/core/testmods/cb-slm/native/src $WD/core/testmods/cb-slm/native/res cb-slmn.jar
SRV=$WD/srv-slm
if [ ! -f "$SRV/fabric-server.jar" ]; then
    mkdir -p "$SRV" && cp -r ~/cbtest/server-26.2/fabric-server.jar ~/cbtest/server-26.2/libraries ~/cbtest/server-26.2/versions \
        ~/cbtest/server-26.2/eula.txt ~/cbtest/server-26.2/server.properties.orig "$SRV/"
fi
rm -rf "$SRV/mods" "$SRV/world-$TAG" "$SRV/logs" && mkdir -p "$SRV/mods"
cp data/runtime/client-26.2/mods/fabric-api-*.jar "$TM/cb-slmn.jar" "$SRV/mods/"
if [ "$MODE" != native-nolayer ]; then
    [ -f "$LAYER" ] || { echo "no $LAYER"; exit 1; }
    cp "$LAYER" "$SRV/mods/"
fi
if [ "$MODE" = conv ]; then
    mk 26.1.2 $WD/core/testmods/cb-slm/src $WD/core/testmods/cb-slm/res cb-slm.jar
    "$JDK/java" -Xmx4g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 26.1.2 26.2 \
        "$TM/conv" "$TM/cb-slm.jar" > "$TM/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$TM/climb.log"; exit 1; }
    grep -rhaE "mixin (bridged|not bridged)|disabled injector|loss:" "$TM/conv" --include=report.md | grep -a "cb.slm\|Slime\|SlimeTick" | cut -c1-400 | head -10
    cp "$TM"/conv/*+26.2.jar "$SRV/mods/"
fi
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 300 "$JDK/java" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 140); do
    sleep 2
    grep -qaE 'CB-SLM END|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-SLM|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|NoSuchField|AbstractMethod|VerifyError|Mixin apply' "boot-$TAG.log" | head -40
kill $PID 2>/dev/null; wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
