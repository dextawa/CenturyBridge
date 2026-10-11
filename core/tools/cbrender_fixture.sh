#!/usr/bin/env bash
# cbrender_fixture.sh <tag> <mod jar...> : headless 26.2 client (Xvfb + Mesa llvmpipe) on a
# fresh singleplayer world, with fabric-api, the CenturyBridge jar and the given mods.
# The world is generated once by the dedicated server and copied to the client's saves;
# the client enters it through --quickPlaySingleplayer. Screenshots every 20 s, then the
# fixture's own log lines (CB-RENDERTEST ...) and anything that broke.
set -uo pipefail
TAG="${1:-run}"; shift || true
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
RT=~/cbtest/client-26.2
SRV=~/cbtest/server-26.2
DISP=:96

if [ ! -d "$RT/saves/cbrt" ]; then
  cd "$SRV" || exit 1
  rm -rf world-cbrt-gen mods && mkdir mods
  [ -f server.properties ] || cp server.properties.orig server.properties
  sed -i 's/^level-name=.*/level-name=world-cbrt-gen/' server.properties
  FIFO=$(mktemp -u); mkfifo "$FIFO"
  ( timeout 300 "$JAVA" -Xmx3g -jar fabric-server.jar nogui < "$FIFO" > gen-cbrt.log 2>&1 ) &
  exec 3>"$FIFO"
  for i in $(seq 1 120); do sleep 2; grep -qa 'Done (' gen-cbrt.log && break; done
  echo stop >&3; sleep 12; exec 3>&-; rm -f "$FIFO"
  mkdir -p "$RT/saves" && cp -r world-cbrt-gen "$RT/saves/cbrt" && echo "world generated"
fi

cd "$RT" || exit 1
rm -f mods/*.jar
cp ~/cbtest/fabric-api-0.158.0+26.2.jar ~/cbtest/centurybridge-0.3.0+26.2.jar mods/
for j in "$@"; do cp "$j" mods/; done
echo "mods: $(ls mods | tr '\n' ' ')"
pkill -f "Xvfb $DISP" 2>/dev/null; sleep 1
Xvfb $DISP -screen 0 1280x720x24 -nolisten tcp >/dev/null 2>&1 &
XPID=$!
sleep 2
export DISPLAY=$DISP LIBGL_ALWAYS_SOFTWARE=1 GALLIUM_DRIVER=llvmpipe
LOG="client-$TAG.log"
rm -f shot-$TAG-*.png
"$JAVA" $(tr '\n' ' ' < jvmargs.txt) net.fabricmc.loader.impl.launch.knot.KnotClient \
  --version 26.2 --gameDir "$RT" --assetsDir "$RT/assets" --assetIndex 32 \
  --accessToken 0 --username cbtest --quickPlaySingleplayer cbrt > "$LOG" 2>&1 &
CPID=$!
for t in 40 60 80 100 120 140; do
  sleep 20
  import -display $DISP -window root "shot-$TAG-$t.png" 2>/dev/null
  kill -0 $CPID 2>/dev/null || { echo "client exited by t=$t"; break; }
done
kill $CPID 2>/dev/null; sleep 3; kill -9 $CPID 2>/dev/null
kill $XPID 2>/dev/null
echo "=== shots ==="; ls shot-$TAG-*.png 2>/dev/null | tr '\n' ' '; echo
echo "=== fixture ==="
grep -a 'CB-RENDERTEST' "$LOG" | sed 's/^.*CB-RENDERTEST/CB-RENDERTEST/' | head -30
echo "=== problems ==="
grep -anE '/ERROR|Exception|Caused by|NoSuchMethod|NoSuchField|AbstractMethod|IncompatibleClass|IllegalAccess|VerifyError|ClassCast|crash' "$LOG" \
  | grep -viE 'Error loading class|realms|telemetry' | head -40
echo "=== END ==="
