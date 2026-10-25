#!/usr/bin/env bash
# boot.sh <layer|nolayer>: the s26 control. Compiles core/testmods/cb-s26v against 26.2 (native, no conversion) and
# boots it on agents/s26's own Fabric 26.2 server (agents/s26/srv, as boot26.sh sets it up) with Fabric API, with or
# without the work dir's built step layer, on a fixed-seed world. Run on beta2 from ~/cbwork after remote_step.sh built
# the layer. The "CB-S26V" lines of a layer run and a nolayer run must be identical.
set -uo pipefail
cd ~/cbwork
WD=agents/s26; MODE="$1"
JDK=~/cbtest/jdk-25.0.4.1+1/bin
LAYER=$WD/build/centurybridge-0.3.0+26.2.jar
TM=$WD/tmv
R=data/runtime/client-26.2
CP="data/jars/client-26.2.jar:$(find $R/libraries -name '*.jar' | tr '\n' ':')$(find $R/.fabric/processedMods -name '*.jar' 2>/dev/null | tr '\n' ':')"
rm -rf "$TM" && mkdir -p "$TM/classes"
"$JDK/javac" -nowarn -proc:none --release 25 -cp "$CP" -d "$TM/classes" $(find $WD/core/testmods/cb-s26v/src -name '*.java') \
    > "$TM/javac.log" 2>&1 || { echo "control mod compile failed"; tail -20 "$TM/javac.log"; exit 1; }
cp $WD/core/testmods/cb-s26v/res/* "$TM/classes/"
"$JDK/jar" cf "$TM/cb-s26v.jar" -C "$TM/classes" .
SRV=$WD/srv
[ -f "$SRV/fabric-server.jar" ] || { echo "no $SRV (run boot26.sh once)"; exit 1; }
TAG=control-$MODE
rm -rf "$SRV/mods" "$SRV/world-$TAG" "$SRV/logs" && mkdir -p "$SRV/mods"
cp data/runtime/client-26.2/mods/fabric-api-*.jar "$SRV/mods/"
cp "$TM/cb-s26v.jar" "$SRV/mods/"
if [ "$MODE" = layer ]; then
    [ -f "$LAYER" ] || { echo "no $LAYER"; exit 1; }
    cp "$LAYER" "$SRV/mods/"
fi
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/; s/^level-seed=.*/level-seed=2602612/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 400 "$JDK/java" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 190); do
    sleep 2
    grep -qaE 'CB-S26V END|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-S26V' "boot-$TAG.log" | sed 's/^.*CB-S26V/CB-S26V/' > "control-$MODE.txt"
cat "control-$MODE.txt"
grep -aE '/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|Mixin apply' "boot-$TAG.log" | head -20
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
