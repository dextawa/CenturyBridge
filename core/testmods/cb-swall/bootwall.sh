#!/usr/bin/env bash
# bootwall.sh <tag> <1.21.11 mod jar...>: real-server check for the wall step (swall) -- 1.21.11 mods converted 1.21.11 -> 26.1
# with this agent's engine and wiring (Main convert-climb), booted on a Fabric 26.1 server (<WD>/srv)
# with Fabric API, this agent's 26.1 step layer and the native 26.1 mods named in CB_NATIVE (space-separated jars,
# installed unconverted). Prints the result and the CBTEST lines. Run on beta2 from ~/cbwork.
set -uo pipefail
cd ~/cbwork
WD=${CB_WD:-agents/swall7}; TAG="$1"; shift 1
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
SRV=$WD/srv
CONV=$WD/boot/$TAG
rm -rf "$WD/boot" && mkdir -p "$CONV"
"$JAVA" -Xmx4g -Dcb.layersDir="$WD/build" -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.21.11 26.1 "$CONV/conv" "$@" \
    > "$CONV/climb.log" 2>&1 || { echo "climb failed"; tail -5 "$CONV/climb.log"; exit 1; }
rm -rf "$SRV/mods" "$SRV/world" "$SRV/logs" "$SRV/crash-reports" && rm -f "$SRV"/boot-*.log && mkdir -p "$SRV/mods"
cp data/runtime/client-26.1/mods/fabric-api-*.jar "$SRV/mods/"
cp $WD/build/centurybridge-0.3.0+26.1.jar "$SRV/mods/"
cp $WD/build/deps/*.jar "$SRV/mods/" 2>/dev/null
cp "$CONV"/conv/*+26.1.jar "$SRV/mods/"
for n in ${CB_NATIVE:-}; do cp "$n" "$SRV/mods/"; done
cp "$SRV/server.properties.orig" "$SRV/server.properties"
echo "mods: $(ls "$SRV/mods" | tr '\n' ' ')"
cd "$SRV"
timeout 400 "$JAVA" -Xmx3g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
WANT='CBTEST SUMMARY'
[ -n "${CB_NATIVE:-}" ] && WANT='CBNATIVE DONE'
for i in $(seq 1 180); do
    sleep 2
    grep -qaE "$WANT|crashed|Crash report|Exception in thread" "boot-$TAG.log" && break
done
sleep 5
kill $PID 2>/dev/null
wait $PID 2>/dev/null
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CBTEST (FAIL|SUMMARY)|CBNATIVE|swalln|ERROR|Caused by|Exception|Mixin apply|crash' "boot-$TAG.log" | head -60
echo "pass lines: $(grep -ac 'CBTEST PASS' "boot-$TAG.log")"
rm -rf world
