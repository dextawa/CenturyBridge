#!/usr/bin/env bash
# cbrender_fixture.sh <ver> <tag> <mod jar...> : headless client (Xvfb + Mesa llvmpipe) on a
# fresh singleplayer world, on beta2.
#   26.2   fabric-api + the CenturyBridge jar + the given (converted) mods
#   1.20.1 the given mods alone, natively: the reference render a converted run is compared to
# Each version's world is generated once, from one fixed seed, by that version's own dedicated
# server; every run starts from a fresh copy of it and enters through --quickPlaySingleplayer.
# Screenshots are taken at fixed offsets after the fixture spawns its entities (the fixture
# fixes camera and pose, so runs line up frame to frame), then the fixture's own log lines
# (CB-RENDERTEST ...) and anything that broke.
set -uo pipefail
VER="${1:?ver}"; TAG="${2:?tag}"; shift 2
SEED=centurybridge
DISP=:96
ASSETS=~/cbtest/client-26.2/assets # one content-addressed store serves every version
case "$VER" in
  26.2)
    JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java; RT=~/cbtest/client-26.2; SRV=~/cbtest/server-26.2
    SRVJAR=fabric-server.jar; AIDX=32
    EXTRA=(~/cbtest/fabric-api-0.158.0+26.2.jar ~/cbtest/centurybridge-0.3.0+26.2.jar) ;;
  1.20.1)
    JAVA=java; RT=~/cbtest/client-1.20.1; SRV=~/cbtest/server-1.20.1
    SRVJAR=server.jar; AIDX=5
    EXTRA=() ;;
  *) echo "unknown version $VER"; exit 1 ;;
esac

setprop() { # setprop <file> <key> <value>: key=value (server.properties) or key:value (options.txt)
  local f="$1" k="$2" v="$3" sep="="
  case "$f" in *options.txt) sep=":" ;; esac
  touch "$f"
  if grep -q "^$k$sep" "$f"; then sed -i "s|^$k$sep.*|$k$sep$v|" "$f"; else echo "$k$sep$v" >> "$f"; fi
}

# ---- the world template: generated once per version and seed ----
if [ "$(cat "$SRV/world-cbrt-gen/.cb-seed" 2>/dev/null)" != "$SEED" ]; then
  mkdir -p "$SRV" && cd "$SRV" || exit 1
  if [ ! -f "$SRVJAR" ]; then
    python3 - "$VER" "$SRVJAR" <<'EOF'
import json, sys, urllib.request
ver, dest = sys.argv[1], sys.argv[2]
mf = json.load(urllib.request.urlopen("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"))
vj = json.load(urllib.request.urlopen(next(v["url"] for v in mf["versions"] if v["id"] == ver)))
urllib.request.urlretrieve(vj["downloads"]["server"]["url"], dest)
EOF
  fi
  rm -rf world-cbrt-gen mods && mkdir mods
  echo "eula=true" > eula.txt
  [ -f server.properties ] || { [ -f server.properties.orig ] && cp server.properties.orig server.properties; }
  setprop server.properties level-name world-cbrt-gen
  setprop server.properties level-seed "$SEED"
  setprop server.properties online-mode false
  setprop server.properties server-port 25741
  FIFO=$(mktemp -u); mkfifo "$FIFO"
  ( timeout 300 "$JAVA" -Xmx3g -jar "$SRVJAR" nogui < "$FIFO" > gen-cbrt.log 2>&1 ) &
  exec 3>"$FIFO"
  for i in $(seq 1 140); do sleep 2; grep -qa 'Done (' gen-cbrt.log && break; done
  echo stop >&3; sleep 15; exec 3>&-; rm -f "$FIFO"
  grep -qa 'Done (' gen-cbrt.log || { echo "world generation failed:"; tail -20 gen-cbrt.log; exit 1; }
  echo "$SEED" > world-cbrt-gen/.cb-seed
  echo "world generated (seed $SEED)"
fi

# ---- the client: fresh world copy, fixed options, the mods ----
cd "$RT" || exit 1
mkdir -p saves mods
rm -rf saves/cbrt && cp -r "$SRV/world-cbrt-gen" saves/cbrt
for kv in "onboardAccessibility false" "pauseOnLostFocus false" 'renderClouds "false"' \
          "tutorialStep none" "skipMultiplayerWarning true" "joinedFirstServer true"; do
  setprop options.txt ${kv%% *} "${kv#* }"
done
rm -f mods/*.jar
for j in "${EXTRA[@]}" "$@"; do cp "$j" mods/; done
echo "mods: $(ls mods | tr '\n' ' ')"
pkill -f "Xvfb $DISP" 2>/dev/null; sleep 1
Xvfb $DISP -screen 0 1280x720x24 -nolisten tcp >/dev/null 2>&1 &
XPID=$!
sleep 2
export DISPLAY=$DISP LIBGL_ALWAYS_SOFTWARE=1 GALLIUM_DRIVER=llvmpipe
LOG="client-$TAG.log"
rm -f shot-$TAG-*.png
"$JAVA" $(tr '\n' ' ' < jvmargs.txt) net.fabricmc.loader.impl.launch.knot.KnotClient \
  --version "$VER" --gameDir "$RT" --assetsDir "$ASSETS" --assetIndex "$AIDX" \
  --accessToken 0 --username cbtest --quickPlaySingleplayer cbrt > "$LOG" 2>&1 &
CPID=$!
SPAWNED=
for i in $(seq 1 150); do
  sleep 2
  grep -qa 'CB-RENDERTEST spawned' "$LOG" && { SPAWNED=1; break; }
  kill -0 $CPID 2>/dev/null || break
done
if [ -n "$SPAWNED" ]; then
  for t in 5 10 20; do
    sleep $(( t == 5 ? 5 : t == 10 ? 5 : 10 ))
    import -display $DISP -window root "shot-$TAG-$t.png" 2>/dev/null
    kill -0 $CPID 2>/dev/null || { echo "client exited by +$t s"; break; }
  done
else
  import -display $DISP -window root "shot-$TAG-nospawn.png" 2>/dev/null
  echo "fixture never spawned its entities"
fi
kill $CPID 2>/dev/null; sleep 3; kill -9 $CPID 2>/dev/null
kill $XPID 2>/dev/null
echo "=== shots ==="; ls shot-$TAG-*.png 2>/dev/null | tr '\n' ' '; echo
echo "=== fixture ==="
grep -aE 'CB-(RENDERTEST|ITEMTEST)' "$LOG" | grep -v 'feature render' | sed -E 's/^.*(CB-(RENDERTEST|ITEMTEST))/\1/' | head -40
echo "=== problems ==="
grep -anE '/ERROR|Exception|Caused by|NoSuchMethod|NoSuchField|AbstractMethod|IncompatibleClass|IllegalAccess|VerifyError|ClassCast|crash' "$LOG" \
  | grep -viE 'Error loading class|realms|telemetry' | head -40
echo "=== END ==="
