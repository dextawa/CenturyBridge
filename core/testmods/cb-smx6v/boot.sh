#!/usr/bin/env bash
# boot.sh <layer|nolayer> <cb-smx6v.jar>: cb-smx6v (native 1.21, no conversion) booted on agents/smx6's own Fabric 1.21 server
# with Fabric API, with or without the work dir's built step layer. Run on beta2 from ~/cbwork after the layer was built.
set -uo pipefail
cd ~/cbwork
WD=agents/smx6; MODE="$1"; MOD="$2"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LAYER=$WD/build/centurybridge-0.3.0+1.21.jar
SRV=$WD/srv-1.21
[ -d "$SRV" ] || { cp -r ~/cbtest/server-1.21 "$SRV" && rm -rf "$SRV/logs" "$SRV"/world*; }
TAG=control-$MODE
rm -rf "$SRV/mods" "$SRV/world-$TAG" && mkdir -p "$SRV/mods"
cp data/runtime/client-1.21/mods/fabric-api-*.jar "$SRV/mods/"
cp "$MOD" "$SRV/mods/"
if [ "$MODE" = layer ]; then
    [ -f "$LAYER" ] || { echo "no $LAYER"; exit 1; }
    cp "$LAYER" "$SRV/mods/"
fi
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 400 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 190); do
    sleep 2
    grep -qaE 'Done \(|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
sleep 2
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-SMX6V|/ERROR|Caused by|Exception|crashed|NoClassDefFound|NoSuchMethod|Mixin apply|at cb\.' "boot-$TAG.log" | head -40
kill $PID 2>/dev/null
wait $PID 2>/dev/null
rm -rf "world-$TAG"
echo "===END==="
