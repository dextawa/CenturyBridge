#!/usr/bin/env bash
# Boot a dedicated server with a converted mod set and report the outcome.
#
#   core/tools/boot_server.sh <ver> <convDir> <pickList> [cbJar] [extraJars...]
#
# Uses data/runtime/server-<ver>; its original mods/ is kept as mods.orig and
# server.properties gets level-name=world-lc (fresh world every run). Base
# mods taken from mods.orig: fabric-api + cloth-config. Prints Done / the
# first crash causes / STALLED (log silent 90 s -- a GUI prompt or deadlock;
# a jstack of the main thread is printed). The server is stopped afterwards.
set -uo pipefail
VER="$1"; CONV="$2"; PICK="$3"; CB="${4:-build/centurybridge-0.3.0+$VER.jar}"
shift 4 2>/dev/null || shift $#
ROOT="$(pwd)"
RT="data/runtime/server-$VER"
cd "$RT" || exit 1
[ -d mods.orig ] || cp -r mods mods.orig
[ -f server.properties.orig ] || cp server.properties server.properties.orig
sed -i 's/^level-name=.*/level-name=world-lc/' server.properties
rm -rf mods world-lc && mkdir mods
cp mods.orig/fabric-api-*.jar mods.orig/cloth-config-*.jar mods/ 2>/dev/null
cp "$ROOT/$CB" mods/
tr -d '\r' < "$ROOT/$PICK" | while read -r j; do [ -n "$j" ] && cp "$ROOT/$CONV/$j" mods/; done
for x in "$@"; do cp "$ROOT/$x" mods/; done
echo "mods: $(ls mods | wc -l)"
# Java selection: Java 25 on Windows crashes the NIO selector loopback
# (sun.nio.ch.UnixDomainSockets.connect0 "Invalid argument") during server
# network init -- a JDK bug, not CB. Prefer an explicit CB_JAVA, else a
# locally-installed JDK 21, else PATH java.
JAVA_BIN="${CB_JAVA:-}"
if [ -z "$JAVA_BIN" ]; then
    for cand in "/c/Program Files/Microsoft/jdk-21"* "/c/Program Files/Eclipse Adoptium/jdk-21"* "/c/Program Files/Java/jdk-21"*; do
        if [ -x "$cand/bin/java.exe" ]; then JAVA_BIN="$cand/bin/java.exe"; break; fi
    done
fi
[ -z "$JAVA_BIN" ] && JAVA_BIN="java"
echo "java: $JAVA_BIN"
"$JAVA_BIN" -version 2>&1 | head -1
"$JAVA_BIN" -Xmx2500m -jar fabric-server.jar nogui > boot-lc.log 2>&1 &
last=0; stall=0
for i in $(seq 1 200); do
    sleep 2
    grep -qa "Done (\|Minecraft has crashed\|Crash report\|Incompatible\|A mod crashed" boot-lc.log && break
    s=$(stat -c %s boot-lc.log)
    if [ "$s" = "$last" ]; then stall=$((stall+2)); else stall=0; last=$s; fi
    if [ $stall -ge 90 ]; then
        echo STALLED
        pid=$(powershell -NoProfile -Command "(Get-CimInstance Win32_Process -Filter \"Name='java.exe'\" | Where-Object { \$_.CommandLine -like '*fabric-server*' -and \$_.WorkingSetSize -gt 100MB }).ProcessId")
        [ -n "$pid" ] && jstack "$pid" 2>/dev/null | grep -A12 '"main"\|"Server thread"' | head -30
        break
    fi
done
sleep 2
grep -an "Done (\|/ERROR\|Caused by" boot-lc.log | grep -v "Error loading class" | head -14 | cut -c1-330
powershell -NoProfile -Command "Get-CimInstance Win32_Process -Filter \"Name='java.exe'\" | Where-Object { \$_.CommandLine -like '*fabric-server*' } | ForEach-Object { Stop-Process -Id \$_.ProcessId -Force }" >/dev/null 2>&1
