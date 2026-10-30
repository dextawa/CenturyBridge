#!/usr/bin/env bash
# boot.sh <workDir> <tag> [native-only]: real-server check of the access-widener climb. cb-swallaw (a 1.21.11 mod,
# built against 1.21.11 with its own access widener applied, as loom would) climbs 1.21.11 -> 26.2 with <workDir>'s
# engine (climb_mods.sh, CB_FROM=1.21.11) and boots on a Fabric 26.2 server (<workDir>/srv-26.2, a copy of
# ~/cbtest/server-26.2) beside cb-swallaw-n (native/, built against 26.2, not converted). Expected: "CB-SWALLAW ok"
# and "CB-SWALLAWN ok"; native-only boots the control alone. Prints the climbed access widener.
# Run on beta2 from ~/cbwork after climb_runtime.sh <workDir> 26.2.
set -uo pipefail
cd ~/cbwork
WD="$1"; TAG="$2"; MODE="${3:-}"
JDK=~/cbtest/jdk-25.0.4.1+1/bin
export PATH=$JDK:$PATH
TM=$WD/tm-swallaw; rm -rf "$TM" && mkdir -p "$TM"
SRV=$WD/srv-26.2
[ -f "$SRV/fabric-server.jar" ] || cp -r ~/cbtest/server-26.2 "$SRV" || { echo "no server copy"; exit 1; }
build() { # <ver> <stub> <src> <res> <aw> <out>
    local CLS; CLS=$(mktemp -d); local R=data/runtime/client-$1
    CB_DATA=data bash $WD/core/tools/make_aw_stub.sh "$1" "$2" "$CLS.stub.jar" "$5" > /dev/null || return 1
    local CP="$CLS.stub.jar"; for j in $(find $R/libraries $R/.fabric/processedMods -name '*.jar'); do CP="$CP:$j"; done
    javac --release 21 -nowarn -proc:none -cp "$CP" -d "$CLS" $(find "$3" -name '*.java') || return 1
    cp "$4"/* "$CLS/"; rm -f "$CLS.stub.jar"; (cd "$CLS" && jar cf "$HOME/cbwork/$6" .); rm -rf "$CLS"
}
S=$WD/core/testmods/cb-swallaw
build 26.2 data/jars/client-26.2.jar $S/native/src $S/native/res $S/native/res/cb_swallawn.accesswidener $TM/cb-swallaw-n.jar \
    || { echo "native build failed"; exit 1; }
if [ "$MODE" = native-only ]; then
    rm -rf "$SRV/mods" && mkdir -p "$SRV/mods" && cp data/runtime/client-26.2/mods/fabric-api-*.jar "$TM/cb-swallaw-n.jar" "$SRV/mods/"
else
    build 1.21.11 data/jars/client-1.21.11-intermediary.jar $S/src $S/res $S/res/cb_swallaw.accesswidener $TM/cb-swallaw.jar \
        || { echo "test mod build failed"; exit 1; }
    CB_FROM=1.21.11 CB_NATIVE_MODS="$TM/cb-swallaw-n.jar" bash $WD/core/tools/climb_mods.sh "$WD" 26.2 "$TM/climb" "$SRV/mods" "$TM/cb-swallaw.jar" || exit 1
    echo "== climbed access widener:"; unzip -p "$SRV"/mods/cb-swallaw*+26.2.jar cb_swallaw.accesswidener
    grep -ah "access widener" "$TM"/climb/conv/*/report.md "$TM"/climb/conv/report.md 2>/dev/null | sort -u | head
fi
rm -rf "$SRV/world-$TAG"; cp "$SRV/server.properties.orig" "$SRV/server.properties" 2>/dev/null
sed -i "s/^level-name=.*/level-name=world-$TAG/" "$SRV/server.properties"
cd "$SRV"
timeout 300 "$JDK/java" -Xmx4g -jar fabric-server.jar nogui > "boot-$TAG.log" 2>&1 < /dev/null &
PID=$!
for i in $(seq 1 140); do
    sleep 2
    grep -qaE 'Done \(|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start|Exception in thread "main"' "boot-$TAG.log" && break
done
grep -qa 'Done (' "boot-$TAG.log" && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
grep -aE 'CB-SWALLAW|IllegalAccess|classTweaker|/ERROR|Caused by' "boot-$TAG.log" | head -20
kill $PID 2>/dev/null; wait $PID 2>/dev/null; rm -rf "world-$TAG"
