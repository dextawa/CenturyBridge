#!/usr/bin/env bash
# boot_data.sh <server-dir> <fapi-jar> <cb-jar> <tag> [mod-dir]
# Boots a dedicated server with the given mods and reports what the data loaders made
# of them: recipe count, and every line where a recipe, loot table, advancement, tag or
# other data file failed to load.
set -uo pipefail
SRV="$1"; FAPI="$2"; CB="$3"; TAG="$4"; MODS="${5:-}"
cd "$SRV" || exit 1
rm -rf world-$TAG mods logs
mkdir mods
[ -f server.properties ] || cp server.properties.orig server.properties
sed -i 's/^level-name=.*/level-name=world-'"$TAG"'/' server.properties
sed -i 's/^online-mode=.*/online-mode=false/' server.properties
cp "$FAPI" "$CB" mods/
[ -n "$MODS" ] && cp "$MODS"/*.jar mods/
LOG=boot-$TAG.log
timeout 300 ${JAVA:-java} -Xmx6g -jar fabric-server.jar nogui > $LOG 2>&1 &
PID=$!
for i in $(seq 1 140); do
    sleep 2
    grep -qaE 'Done \(|Minecraft has crashed|Crash report|A mod crashed|Incompatible|Failed to start' $LOG && break
done
sleep 1
grep -qa 'Done (' $LOG && echo "===RESULT: DONE===" || echo "===RESULT: NOT DONE==="
echo "mods: $(ls mods | tr '\n' ' ')"
grep -aoE 'Loaded [0-9]+ recipes|Loaded [0-9]+ advancements' $LOG
echo "data errors: $(grep -acE 'Couldn.t (parse|load)|Failed to (parse|load)|Parsing error|Unknown (recipe|item|block|loot)|Invalid|No key|not a valid|Couldn.t read' $LOG)"
grep -aE 'Couldn.t (parse|load)|Failed to (parse|load)|Parsing error|Unknown (recipe|item|block|loot)|Invalid|No key|not a valid|Couldn.t read' $LOG | sed 's/^\[[^]]*\] //' | cut -c1-260 | sort | uniq -c | sort -rn | head -25
echo "--- other errors ---"
grep -anE '/ERROR|Caused by|Exception|NoClassDefFound|NoSuchMethod|AbstractMethod' $LOG | grep -vE 'Couldn.t (parse|load)|Failed to (parse|load)|Parsing error' | cut -c1-260 | head -15
kill $PID 2>/dev/null; sleep 2; kill -9 $PID 2>/dev/null
echo "===END==="
