#!/usr/bin/env bash
# scenario.sh <server-dir> <fapi-jar> <cb-jar> <tag> <mod-dir> <commands-file>
# Boots a dedicated server with the given mods, waits for "Done", then feeds the
# commands (one per line; "#" comments and blank lines skipped; "sleep N" pauses)
# to the server console and prints what the server answered to each -- behaviour
# checks a boot cannot make (a container's items dropping when it is broken, a
# recipe crafting, a loot table rolling).
set -uo pipefail
SRV="$1"; FAPI="$2"; CB="$3"; TAG="$4"; MODS="$5"; CMDS="$6"
cd "$SRV" || exit 1
[ -n "${KEEP_WORLD:-}" ] || rm -rf world-$TAG
rm -rf mods logs
mkdir mods
[ -f server.properties ] || cp server.properties.orig server.properties
sed -i 's/^level-name=.*/level-name=world-'"$TAG"'/' server.properties
sed -i 's/^online-mode=.*/online-mode=false/' server.properties
cp "$FAPI" "$CB" mods/
[ -n "$MODS" ] && cp "$MODS"/*.jar mods/
LOG=scenario-$TAG.log
FIFO=$(mktemp -u)
mkfifo "$FIFO"
( timeout 400 ${JAVA:-java} -Xmx6g -jar fabric-server.jar nogui < "$FIFO" > $LOG 2>&1 ) &
PID=$!
exec 3>"$FIFO"
for i in $(seq 1 150); do
    sleep 2
    grep -qaE 'Done \(|Minecraft has crashed|Crash report|Failed to start' $LOG && break
done
if ! grep -qa 'Done (' $LOG; then
    echo "===RESULT: NOT DONE==="
    grep -anE '/ERROR|Caused by|Exception' $LOG | head -10
    exec 3>&-; rm -f "$FIFO"; kill $PID 2>/dev/null; exit 1
fi
echo "===RESULT: DONE==="
while IFS= read -r line; do
    case "$line" in ''|'#'*) continue;; esac
    if [[ "$line" == sleep* ]]; then sleep "${line#sleep }"; continue; fi
    before=$(wc -l < $LOG)
    echo "$line" >&3
    sleep 1
    echo "> $line"
    tail -n +$((before + 1)) $LOG | sed 's/^\[[^]]*\] \[[^]]*\]: /  /' | head -6
done < "$CMDS"
echo "stop" >&3
sleep 5
exec 3>&-
rm -f "$FIFO"
kill $PID 2>/dev/null
echo "===END==="
