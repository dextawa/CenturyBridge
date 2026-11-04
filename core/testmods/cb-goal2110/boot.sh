#!/usr/bin/env bash
# boot.sh <tag>: compile the 1.21.9 fixture and its no-mixin control, convert both one step to
# 1.21.10 with agents/goal2110's tree, then boot each on an isolated real Fabric 1.21.10 server.
# Run on beta2 from ~/cbwork after remote_step.sh has uploaded and built agents/goal2110.
set -uo pipefail
cd ~/cbwork
WD=agents/goal2110; TAG="$1"
JDK=$(ls -d ~/cbtest/jdk-25*/bin | head -1)
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
TM=$WD/tm-goal2110; rm -rf "$TM" && mkdir -p "$TM"
R=data/runtime/client-1.21.9
CP="data/jars/client-1.21.9-intermediary.jar:$(find $R/libraries -name '*.jar' | tr '\n' ':')$(find $R/.fabric/processedMods -name '*.jar' 2>/dev/null | tr '\n' ':')"
SRC=$WD/core/testmods/cb-goal2110
for v in test control; do
    mkdir -p "$TM/$v"
    "$JDK/javac" -nowarn -proc:none --release 21 -cp "$CP" -d "$TM/$v" $(find $SRC/src -name '*.java') > "$TM/javac-$v.log" 2>&1 \
        || { echo "compile failed"; tail -20 "$TM/javac-$v.log"; exit 1; }
    cp $SRC/res/* "$TM/$v/"
done
rm "$TM/control/cb/goal2110/mixin/ItemFrameBoxMixin.class"
sed -i 's/"mixins": \["ItemFrameBoxMixin"\]/"mixins": []/' "$TM/control/cb_goal2110.mixins.json"
sed -i 's/"id": "cb_goal2110"/"id": "cb_goal2110v"/' "$TM/control/fabric.mod.json"
touch "$TM/control/cb_goal2110_control"
"$JDK/jar" cf "$TM/cb-goal2110.jar" -C "$TM/test" .
"$JDK/jar" cf "$TM/cb-goal2110v.jar" -C "$TM/control" .
SRV=$WD/srv-1.21.10
if [ ! -f "$SRV/versions/1.21.10/server-1.21.10.jar" ]; then
    mkdir -p "$SRV/versions/1.21.10" "$TM/server-bundle"
    cp ~/cbtest/server-1.21.4/fabric-server.jar ~/cbtest/server-1.21.4/eula.txt ~/cbtest/server-1.21.4/server.properties.orig "$SRV/"
    mkdir -p "$TM/launcher"
    printf 'fabric-loader-version=0.19.3\ngame-version=1.21.10\n' > "$TM/launcher/install.properties"
    (cd "$TM/launcher" && "$JDK/jar" uf "$HOME/cbwork/$SRV/fabric-server.jar" install.properties)
    cp -r data/runtime/server-1.21.10/libraries "$SRV/"
    python3 -I - "$TM/server-bundle/bundler.jar" <<'EOF'
import json, sys, urllib.request
out = sys.argv[1]
mf = json.load(urllib.request.urlopen("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"))
vj = json.load(urllib.request.urlopen(next(x["url"] for x in mf["versions"] if x["id"] == "1.21.10")))
urllib.request.urlretrieve(vj["downloads"]["server"]["url"], out)
EOF
    (cd "$TM/server-bundle" && unzip -q -o bundler.jar 'META-INF/versions/*')
    cp "$TM"/server-bundle/META-INF/versions/*/server-*.jar "$SRV/versions/1.21.10/server-1.21.10.jar"
fi
for m in cb-goal2110 cb-goal2110v; do
    CONV=$WD/boot/$TAG-$m
    rm -rf "$CONV" && mkdir -p "$CONV"
    "$JDK/java" -Xmx4g -Dcb.wiring=$WD/core/segments/steps/1.21.9__1.21.10.json -Dcb.layerJar=$WD/build/centurybridge-0.3.0+1.21.10.jar \
        -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert "$WD/core/segments" 1.21.9 1.21.10 "$CONV/conv" "$TM/$m.jar" \
        > "$CONV/convert.log" 2>&1 || { echo "convert failed"; tail -5 "$CONV/convert.log"; exit 1; }
    echo "== $m: conversion"
    grep -aE '59943|74964|guarded move' "$CONV"/conv/*report* | head -8
    rm -rf "$SRV/mods" "$SRV/world-$TAG" "$SRV/logs" && mkdir -p "$SRV/mods"
    cp data/runtime/client-1.21.10/mods/fabric-api-*.jar "$SRV/mods/"
    cp $WD/build/centurybridge-0.3.0+1.21.10.jar "$SRV/mods/"
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
        grep -qaE 'CB-GOAL2110 summary|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "$SRV/boot-$TAG-$m.log" && break
    done
    sleep 2
    grep -qa 'Done (' "$SRV/boot-$TAG-$m.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
    grep -aE 'CB-GOAL2110|/ERROR|Caused by|Exception|crashed|NoSuchMethod|NoSuchField|VerifyError|IncompatibleClassChange|Mixin apply|InvalidInjection' "$SRV/boot-$TAG-$m.log" | head -40
    kill $PID 2>/dev/null
    wait $PID 2>/dev/null
    rm -rf "$SRV/world-$TAG"
done
echo "===END==="
