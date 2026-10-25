#!/usr/bin/env bash
# boot_climb.sh <workDir> <T> <tag> <mod jar...>: real-server check of the climb. The given 1.20.1 mods
# are converted one release at a time to T (Main convert-climb with <workDir>'s engine, segments and
# step wirings), and a Fabric T server (~/cbtest/server-T, fabric-server.jar) boots with Fabric API for
# T, the climbed CenturyBridge layers of <workDir>/runtime-T (climb_runtime.sh) and the converted mods.
# Prints whether it reached "Done" and the lines that matter. Run on beta2 from ~/cbwork.
set -uo pipefail
cd ~/cbwork
WD="$1"; T="$2"; TAG="$3"; shift 3
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
# CB_SERVER_DIR: an agent's own copy (cp -r ~/cbtest/server-T ~/cbwork/agents/<name>/), so two boots
# never share a mods directory. CB_JVM_ARGS: extra server JVM options (a -D switch a test needs)
SRV="${CB_SERVER_DIR:-$HOME/cbtest/server-$T}"
[ -f "$SRV/fabric-server.jar" ] || { echo "no Fabric server at $SRV"; exit 1; }
# the climb and the mods directory are climb_mods.sh's, shared with the client boot (boot_client_remote.sh)
[ -f "$WD/core/tools/climb_mods.sh" ] || { echo "no $WD/core/tools/climb_mods.sh (upload a current tree)"; exit 1; }
bash "$WD/core/tools/climb_mods.sh" "$WD" "$T" "$WD/boot/$TAG" "$SRV/mods" "$@" || exit 1
rm -rf "$SRV/world-$TAG"
cp "$SRV/server.properties.orig" "$SRV/server.properties"
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
LOG="$SRV/boot-$TAG.log"
cd "$SRV"
timeout 300 "$JAVA" -Xmx6g ${CB_JVM_ARGS:-} -jar fabric-server.jar nogui > "$LOG" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 140); do
    sleep 2
    grep -qaE 'Done \(|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "$LOG" && break
done
sleep 1
grep -qa 'Done (' "$LOG" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -anE 'Done \(|/ERROR|Caused by|Exception|crashed|Incompatible|Loading [0-9]+ mods|NoClassDefFound|NoSuchMethod|AbstractMethod|VerifyError|IncompatibleClassChange|Mixin apply' "$LOG" \
    | grep -viE 'Error loading class' | head -40
kill $PID 2>/dev/null   # timeout passes the signal on to the server
wait $PID 2>/dev/null
echo "===END==="
