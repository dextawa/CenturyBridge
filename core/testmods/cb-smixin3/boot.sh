#!/usr/bin/env bash
# boot.sh <tag>: real-server check of smixin3's guarded descend (mechanism o), run on beta2 from ~/cbwork after
# remote_step.sh built this agent's engine and 26.2 layer. Compiles cb-smixin3 against 26.1.2 twice -- the test mod
# (GoalSelectorMixin on 26.1.2's GoalSelector.removeGoal) and the control cb-smixin3v (same driver, no
# GoalSelectorMixin, marker resource cb_smixin3_control) -- converts each 26.1.2 -> 26.2 with this agent's engine,
# wiring and layer, and boots a Fabric 26.2 server (agents/smixin3/srv-26.2) with Fabric API, the layer and that mod.
set -uo pipefail
cd ~/cbwork
WD=agents/smixin3; TAG="$1"
JDK=$(ls -d ~/cbtest/jdk-25*/bin | head -1)
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
TM=$WD/tm; rm -rf "$TM" && mkdir -p "$TM"
R=data/runtime/client-26.1.2
CP="data/jars/client-26.1.2.jar:$(find $R/libraries -name '*.jar' | tr '\n' ':')$(find $R/.fabric/processedMods -name '*.jar' 2>/dev/null | tr '\n' ':')"
SRC=$WD/core/testmods/cb-smixin3
for v in test control; do
    mkdir -p "$TM/$v"
    "$JDK/javac" -nowarn -proc:none --release 25 -cp "$CP" -d "$TM/$v" $(find $SRC/src -name '*.java') > "$TM/javac-$v.log" 2>&1 \
        || { echo "compile failed"; tail -20 "$TM/javac-$v.log"; exit 1; }
    cp $SRC/res/* "$TM/$v/"
done
rm "$TM/control/cb/smixin3/mixin/GoalSelectorMixin.class"
sed -i 's/, "GoalSelectorMixin"//' "$TM/control/cb_smixin3.mixins.json"
sed -i 's/"id": "cb_smixin3"/"id": "cb_smixin3v"/' "$TM/control/fabric.mod.json"
touch "$TM/control/cb_smixin3_control"
"$JDK/jar" cf "$TM/cb-smixin3.jar" -C "$TM/test" .
"$JDK/jar" cf "$TM/cb-smixin3v.jar" -C "$TM/control" .
SRV=$WD/srv-26.2
if [ ! -f "$SRV/fabric-server.jar" ]; then
    mkdir -p "$SRV"
    cp -r ~/cbtest/server-26.2/fabric-server.jar ~/cbtest/server-26.2/libraries ~/cbtest/server-26.2/versions \
        ~/cbtest/server-26.2/eula.txt ~/cbtest/server-26.2/server.properties.orig "$SRV/"
fi
for m in cb-smixin3 cb-smixin3v; do
    CONV=$WD/boot/$TAG-$m
    rm -rf "$CONV" && mkdir -p "$CONV"
    "$JDK/java" -Xmx4g -Dcb.wiring=$WD/core/segments/steps/26.1.2__26.2.json -Dcb.layerJar=$WD/build/centurybridge-0.3.0+26.2.jar \
        -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert "$WD/core/segments" 26.1.2 26.2 "$CONV/conv" "$TM/$m.jar" \
        > "$CONV/convert.log" 2>&1 || { echo "convert failed"; tail -5 "$CONV/convert.log"; exit 1; }
    echo "== $m: conversion"
    grep -aE 'removeGoal|guard' "$CONV"/conv/*report* | head -6
    rm -rf "$SRV/mods" "$SRV/world-$TAG" "$SRV/logs" && mkdir -p "$SRV/mods"
    cp data/runtime/client-26.2/mods/fabric-api-*.jar "$SRV/mods/"
    cp $WD/build/centurybridge-0.3.0+26.2.jar "$SRV/mods/"
    cp "$CONV"/conv/*.jar "$SRV/mods/"
    cp "$SRV/server.properties.orig" "$SRV/server.properties"
    sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
    echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
    cd "$SRV"
    timeout 300 "$JDK/java" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG-$m.log" 2>&1 < /dev/null &
    PID=$!
    cd ~/cbwork
    for i in $(seq 1 140); do
        sleep 2
        grep -qaE 'CB-SMIXIN3 summary|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "$SRV/boot-$TAG-$m.log" && break
    done
    sleep 2
    grep -qa 'Done (' "$SRV/boot-$TAG-$m.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
    grep -aE 'CB-SMIXIN3|/ERROR|Caused by|Exception|crashed|NoSuchMethod|NoSuchField|VerifyError|IncompatibleClassChange|Mixin apply|InvalidInjection' "$SRV/boot-$TAG-$m.log" | head -40
    kill $PID 2>/dev/null
    wait $PID 2>/dev/null
    rm -rf "$SRV/world-$TAG"
done
echo "===END==="
