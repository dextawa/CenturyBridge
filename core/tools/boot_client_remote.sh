#!/usr/bin/env bash
# boot_client_remote.sh <name> <T> <tag> [mod jar...]: a real Fabric client of release T, headless on
# beta2 (Xvfb + Mesa llvmpipe, software GL), driven from this machine -- no Java or Minecraft runs here.
#
# Run from the root of a checkout. Uploads the tree as remote_step.sh does (core/src, core/shims,
# core/segments, core/tools, build.sh) to ~/cbwork/agents/<name>/ plus the given local mod jars, then
# starts the run on beta2 detached (setsid nohup; an ssh drop does not kill it) and polls it:
#   1. mods: by default the given jars are 1.20.1 mods climbed to T (climb_runtime.sh builds this
#      checkout's layers into agents/<name>/runtime-T, climb_mods.sh converts the mods and assembles
#      the mods directory exactly as boot_climb.sh does for servers). CB_CLIMB=0 drops the jars in as
#      they are (a native run; no jars = vanilla Fabric + Fabric API, the calibration).
#   2. client: KnotClient with T's launch runtime (jvmargs.txt + launch-info.json, made by
#      bootstrap_client.py on beta2, so Linux natives), -Xmx4G, offline user cbtest, under
#      xvfb-run -s '-screen 0 1280x720x24', LIBGL_ALWAYS_SOFTWARE=1 GALLIUM_DRIVER=llvmpipe. The game
#      directory <clients>/game-T persists (Fabric's remapped game jar is cached there); one run per
#      release at a time (flock), its mods/, config/, logs/ and saves/ are reset every run.
#   3. waits for the ready line (CB_READY, a grep -E regex; default the sound engine start that ends
#      the first resource load, or "joined the game" with CB_WORLD), takes screenshots CB_SHOTS
#      seconds after it (960x540 PNG), then kills the client by its own PID.
#   4. fetches screenshots, latest.log, crash reports and summary.txt (timings, peak RSS, the test
#      mods' "CB-<X> ..." lines, the problems) to CB_OUT (default $TMPDIR/cb-client-<name>/<tag>).
#
# Environment:
#   CB_CLIMB=1|0          climb the jars from 1.20.1 (default 1) or use them as they are
#   CB_FAPI=1|0           with CB_CLIMB=0: add T's Fabric API (default 1; a climb always has it)
#   CB_REUSE_LAYERS=1     keep agents/<name>/wt-T/runtime-T when it exists (default: rebuild the layers
#                         in wt-T, a copy of the uploaded tree per target)
#   CB_UPLOAD=0           do not re-upload and recompile the tree (mods are always uploaded)
#   CB_SHOTS="10 25 40"   screenshot offsets in seconds after the ready line (CB_SHOTS="" for none;
#                         screenshots the mods take into the game's screenshots/ come back as mod-*.png)
#   CB_READY=<regex>      the ready line; CB_WAIT=300 the seconds to wait for it. A client test mod's
#                         tally: CB_READY='CB-CLIENTTEST [0-9]+/[0-9]+' CB_SHOTS="" CB_EXIT_WAIT=30
#   CB_EXIT_WAIT=<s>      after the ready line and the shots, wait up to this long for the client to stop
#                         itself (a test mod that quits when done) before killing it (default 0)
#   CB_WORLD=<save dir>   a world on beta2, copied into saves/cbworld and entered with
#                         --quickPlaySingleplayer cbworld (the ready line default becomes "joined the
#                         game"); CB_WORLD=seed:<seed> generates it once with T's vanilla dedicated server
#                         (peaceful), kept in <clients>/worlds/T-<seed>
#   CB_GREP=<regex>       the test mods' tally lines for the summary (default 'CB-[A-Z0-9]+')
#   CB_CLIENTS=<dir>      on beta2: launch runtimes client-T/, asset store assets/, game dirs game-T/
#                         (default ~/cbwork/agents/<name>/clients); CB_CLIENT_RT / CB_ASSETS override
#                         the runtime and asset store of this run (else <clients>/client-T, then
#                         ~/cbtest/client-T; assets: <clients>/assets, then the runtime's launch-info)
#   CB_JAVA=<java>        on beta2 (default: JDK 25 for 26.x, the system JDK 21 before)
#
# A launch runtime for T is made on beta2 with
#   cd ~/cbwork && python3 bootstrap_client.py T <clients>/client-T --assets-from <clients>
set -uo pipefail

if [ "${1:-}" != __remote ] && [ "${1:-}" != __inner ]; then
    # ---------------------------------------------------------------- this machine
    set -e
    NAME="$1"; T="$2"; TAG="$3"; shift 3
    SSH="ssh -o BatchMode=yes -o ConnectTimeout=30 -o ServerAliveInterval=30 -o ServerAliveCountMax=10 -p 20022 dext@beta2.dext.top"
    SCP="scp -q -P 20022 -o BatchMode=yes -o ConnectTimeout=30"
    TMP="${TMPDIR:-/tmp}/cb-client-$NAME"
    OUT="${CB_OUT:-$TMP/$TAG}"
    AG="cbwork/agents/$NAME"
    RUN="$AG/client/$TAG"
    mkdir -p "$TMP"
    retry() { local i; for i in 1 2 3 4 5; do "$@" && return 0; echo "(beta2 unreachable, retrying)"; sleep 20; done; return 1; }
    if [ "${CB_UPLOAD:-1}" != 0 ]; then
        rm -f "$TMP/up.tar"
        tar --force-local --exclude=__pycache__ -cf "$TMP/up.tar" core/src core/shims core/segments core/tools $([ -d core/mods ] && echo core/mods) build.sh
        retry $SSH "mkdir -p ~/$AG"
        retry $SCP "$TMP/up.tar" "dext@beta2.dext.top:$AG/up.tar"
        retry $SSH "set -e; cd ~/$AG && rm -rf core build.sh && tar xf up.tar && rm up.tar
          find core build.sh -name '*.sh' -o -name '*.py' | xargs sed -i 's/\r\$//'
          export PATH=\$HOME/cbtest/jdk-25.0.4.1+1/bin:\$PATH
          LIBS=\$(find ~/cbwork/data/runtime/client-1.20.4/libraries/org/ow2/asm ~/cbwork/data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
          rm -rf core/build/classes && mkdir -p core/build/classes
          javac -nowarn -proc:none -cp \"\$LIBS\" -d core/build/classes \$(find core/src -name '*.java') 2>&1 | grep -v '^Note\|^注' || true
          echo 'tree uploaded, engine compiled'"
    fi
    retry $SSH "rm -rf ~/$RUN && mkdir -p ~/$RUN/in"
    for j in "$@"; do retry $SCP "$j" "dext@beta2.dext.top:$RUN/in/"; done
    ENV=""
    for v in CB_CLIMB CB_FAPI CB_REUSE_LAYERS CB_SHOTS CB_EXIT_WAIT CB_READY CB_WAIT CB_WORLD CB_GREP CB_CLIENTS CB_CLIENT_RT CB_ASSETS CB_JAVA; do
        [ -n "${!v+set}" ] && ENV="$ENV $v=$(printf '%q' "${!v}")"
    done
    # started/ is made before the launch (mkdir is atomic): an ssh drop after the launch must not make
    # the retry start a second run in the same directories
    retry $SSH "cd ~/cbwork && if mkdir ~/$RUN/started 2>/dev/null; then env$ENV setsid nohup bash ~/$AG/core/tools/boot_client_remote.sh __remote $NAME $T $TAG > ~/$RUN/run.out 2>&1 < /dev/null & echo \$! > ~/$RUN/started/pid; echo started remote run; else echo remote run already started; fi"
    SEEN=0
    while :; do
        sleep 20
        NEW=$($SSH "tail -n +$((SEEN + 1)) ~/$RUN/run.out" 2>/dev/null) || { echo "(poll failed, retrying)"; continue; }
        if [ -n "$NEW" ]; then
            printf '%s\n' "$NEW"
            SEEN=$((SEEN + $(printf '%s\n' "$NEW" | wc -l)))
        fi
        printf '%s\n' "$NEW" | grep -q '^===END===' && break
    done
    rm -rf "$OUT" && mkdir -p "$OUT"
    retry $SCP -r "dext@beta2.dext.top:$RUN/out/." "$OUT/"
    echo "fetched: $OUT ($(ls "$OUT" | tr '\n' ' '))"
    exit 0
fi

if [ "$1" = __remote ]; then
    # ---------------------------------------------------------------- beta2, detached
    shift; NAME="$1"; T="$2"; TAG="$3"
    cd ~/cbwork
    AG="agents/$NAME"; RUN="$AG/client/$TAG"; OUT="$HOME/cbwork/$RUN/out"
    SELF="$HOME/cbwork/$AG/core/tools/boot_client_remote.sh"
    CLIENTS="${CB_CLIENTS:-$HOME/cbwork/$AG/clients}"
    mkdir -p "$OUT/shots" "$CLIENTS"
    end() { echo "$1"; echo "===END==="; exit 0; }
    # gen_world <seed>: the world a vanilla dedicated server of T generates from <seed> (peaceful, so the
    # player stands still), made once and kept in <clients>/worlds/T-<seed>; sets GENERATED. The server
    # jar comes from Mojang's version manifest into <clients>/server-T.
    gen_world() {
        local seed="$1" srv="$CLIENTS/server-$T" dest="$CLIENTS/worlds/$T-$1" port fifo spid i
        GENERATED="$dest"
        [ -f "$dest/level.dat" ] && { echo "world: $dest (cached)"; return 0; }
        mkdir -p "$srv" "$CLIENTS/worlds"
        if [ ! -f "$srv/server.jar" ]; then
            python3 - "$T" "$srv/server.jar" <<'PYEOF' || return 1
import json, sys, urllib.request
ver, dest = sys.argv[1], sys.argv[2]
mf = json.load(urllib.request.urlopen("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"))
vj = json.load(urllib.request.urlopen(next(v["url"] for v in mf["versions"] if v["id"] == ver)))
urllib.request.urlretrieve(vj["downloads"]["server"]["url"], dest)
PYEOF
        fi
        port=$(python3 -c 'import socket; s=socket.socket(); s.bind(("127.0.0.1", 0)); print(s.getsockname()[1])')
        rm -rf "$srv/world" "$srv/logs"
        echo "eula=true" > "$srv/eula.txt"
        printf '%s\n' "level-name=world" "level-seed=$seed" "online-mode=false" "server-port=$port" \
            "server-ip=127.0.0.1" "difficulty=peaceful" "spawn-monsters=false" "enable-query=false" \
            "enable-rcon=false" "max-players=1" > "$srv/server.properties"
        fifo="$srv/stdin"; rm -f "$fifo"; mkfifo "$fifo"
        (cd "$srv" && exec "$JAVA" -Xmx2G -jar server.jar nogui < "$fifo" > gen.log 2>&1) &
        spid=$!
        exec 7>"$fifo"
        for i in $(seq 1 150); do sleep 2; grep -qa 'Done (' "$srv/gen.log" && break; kill -0 $spid 2>/dev/null || break; done
        echo stop >&7
        for i in $(seq 1 60); do sleep 1; kill -0 $spid 2>/dev/null || break; done
        kill $spid 2>/dev/null
        exec 7>&-; rm -f "$fifo"
        grep -qa 'Done (' "$srv/gen.log" || { tail -15 "$srv/gen.log"; return 1; }
        rm -f "$srv/world/session.lock"
        mv "$srv/world" "$dest"
        echo "world: $dest (generated, seed $seed, port $port)"
    }
    # one of the shared measurement slots (as remote_step.sh): a climb converts at -Xmx8g, the client
    # takes 4g; held for the whole run
    mkdir -p .slots; SLOT=''
    while [ -z "$SLOT" ]; do
        for i in $(seq 1 "${CB_SLOTS:-3}"); do
            exec 9>.slots/$i
            if flock -n 9; then SLOT=$i; break; fi
            exec 9>&-
        done
        [ -n "$SLOT" ] || { echo 'waiting for a measurement slot'; sleep 30; }
    done
    echo "== slot $SLOT"
    MODS="$RUN/mods"
    shopt -s nullglob
    IN=("$RUN"/in/*.jar)
    if [ "${CB_CLIMB:-1}" = 1 ]; then
        # climb_runtime.sh wipes <workDir>/layers, so each T builds and climbs in its own copy of the
        # tree, agents/<name>/wt-T (layers/ and runtime-T/ stay there for CB_REUSE_LAYERS), and runs for
        # different targets can build at the same time
        WT="$AG/wt-$T"
        if [ "${CB_REUSE_LAYERS:-0}" = 1 ] && [ -d "$WT/runtime-$T" ]; then
            echo "== layers: reusing $WT/runtime-$T ($(ls "$WT/runtime-$T" | wc -l) jars)"
        else
            echo "== layers: climb_runtime.sh $WT $T"
            S=$SECONDS
            rm -rf "$WT" && mkdir -p "$WT/core" && cp "$AG/build.sh" "$WT/" \
                && (cd "$AG/core" && tar cf - --exclude=./build/awstub .) | (cd "$WT/core" && tar xf -) \
                || end "===RESULT: LAYERS FAILED (tree copy)==="
            bash "$AG/core/tools/climb_runtime.sh" "$WT" "$T" > "$OUT/layers.log" 2>&1 \
                || { tail -8 "$OUT/layers.log"; end "===RESULT: LAYERS FAILED==="; }
            echo "   $(grep -c '^layer' "$OUT/layers.log") layers in $((SECONDS - S)) s"
        fi
        echo "== climb: ${#IN[@]} mod(s) 1.20.1 -> $T"
        bash "$AG/core/tools/climb_mods.sh" "$WT" "$T" "$RUN/conv" "$MODS" "${IN[@]}" || end "===RESULT: CLIMB FAILED==="
        cp "$RUN/conv/climb.log" "$OUT/climb.log"
    else
        rm -rf "$MODS" && mkdir -p "$MODS"
        [ "${CB_FAPI:-1}" = 1 ] && cp data/runtime/client-$T/mods/fabric-api-*.jar "$MODS/"
        [ ${#IN[@]} -gt 0 ] && cp "${IN[@]}" "$MODS/"
        echo "mods ($(ls "$MODS" | wc -l)): $(ls "$MODS" | tr '\n' ' ')"
    fi
    # the launch runtime and asset store
    RT="${CB_CLIENT_RT:-}"
    if [ -z "$RT" ]; then
        for d in "$CLIENTS/client-$T" "$HOME/cbtest/client-$T"; do
            [ -f "$d/jvmargs.txt" ] && { RT="$d"; break; }
        done
    fi
    [ -n "$RT" ] && [ -f "$RT/jvmargs.txt" ] && [ -f "$RT/launch-info.json" ] \
        || end "===RESULT: NO LAUNCH RUNTIME for $T (python3 bootstrap_client.py $T $CLIENTS/client-$T --assets-from $CLIENTS)==="
    AIDX=$(python3 -c 'import json,sys; print(json.load(open(sys.argv[1]))["assetIndex"])' "$RT/launch-info.json")
    ASSETS="${CB_ASSETS:-}"
    if [ -z "$ASSETS" ]; then
        for d in "$CLIENTS/assets" "$(python3 -c 'import json,sys; print(json.load(open(sys.argv[1]))["assetsDir"])' "$RT/launch-info.json")"; do
            [ -f "$d/indexes/$AIDX.json" ] && { ASSETS="$d"; break; }
        done
    fi
    [ -n "$ASSETS" ] && [ -f "$ASSETS/indexes/$AIDX.json" ] || end "===RESULT: NO ASSET INDEX $AIDX for $T==="
    # natives: LWJGL loads them from the natives jars on the classpath, so they must be Linux ones
    NAT=$(tr ':' '\n' < "$RT/jvmargs.txt" | grep -c 'natives-linux')
    [ "$NAT" -gt 0 ] || end "===RESULT: NO LINUX NATIVES on $RT's classpath (bootstrap it on beta2)==="
    case "$T" in
        26.*) JAVA="${CB_JAVA:-$HOME/cbtest/jdk-25.0.4.1+1/bin/java}" ;;
        *) JAVA="${CB_JAVA:-/usr/bin/java}" ;;
    esac
    GAME="$CLIENTS/game-$T"
    mkdir -p "$GAME"
    exec 8>"$GAME.lock"
    flock -n 8 || { echo "waiting for another client run of $T"; flock 8; }
    rm -rf "$GAME/mods" "$GAME/config" "$GAME/logs" "$GAME/crash-reports" "$GAME/saves" "$GAME/screenshots"
    mkdir -p "$GAME/saves"
    cp -r "$MODS" "$GAME/mods"
    QP=""
    if [ -n "${CB_WORLD:-}" ]; then
        WORLD="$CB_WORLD"
        case "$WORLD" in
            seed:*) gen_world "${WORLD#seed:}" || end "===RESULT: WORLD GENERATION FAILED==="; WORLD="$GENERATED" ;;
        esac
        cp -r "$WORLD" "$GAME/saves/cbworld" || end "===RESULT: NO WORLD $WORLD==="
        QP="--quickPlaySingleplayer cbworld"
    fi
    # first-launch screens off, nothing that pauses on a lost focus; GUI scale 2 (640x360 GUI pixels)
    OPT="$GAME/options.txt"; touch "$OPT"
    for kv in "onboardAccessibility:false" "pauseOnLostFocus:false" "tutorialStep:none" \
              "skipMultiplayerWarning:true" "joinedFirstServer:true" 'renderClouds:"false"' "narrator:0" \
              "guiScale:2"; do
        k=${kv%%:*}
        if grep -q "^$k:" "$OPT"; then sed -i "s|^$k:.*|$kv|" "$OPT"; else echo "$kv" >> "$OPT"; fi
    done
    {
        echo "release $T  java $("$JAVA" -version 2>&1 | head -1)"
        echo "runtime $RT  assets $ASSETS (index $AIDX)  linux natives $NAT"
        echo "mods: $(ls "$GAME/mods" | tr '\n' ' ')"
    } > "$OUT/summary.txt"
    cat "$OUT/summary.txt"
    echo "== client"
    READY="${CB_READY:-}"
    [ -n "$READY" ] || { [ -n "$QP" ] && READY='joined the game' || READY='Sound engine started'; }
    xvfb-run -a -s '-screen 0 1280x720x24' bash "$SELF" __inner "$JAVA" "$RT" "$GAME" "$ASSETS" "$AIDX" "$T" "$OUT" "$READY" "$QP" \
        >> "$OUT/summary.txt" 2>&1
    cp "$GAME/logs/latest.log" "$OUT/latest.log" 2>/dev/null
    # screenshots the mods took themselves (the game's screenshots/ folder)
    for f in "$GAME"/screenshots/*.png; do
        convert "$f" -resize 960x540 "$OUT/shots/mod-$(basename "$f")"
    done
    [ -d "$GAME/crash-reports" ] && cp -r "$GAME/crash-reports" "$OUT/"
    {
        echo "=== fixture"
        grep -aE "${CB_GREP:-CB-[A-Z0-9]+}" "$OUT/latest.log" 2>/dev/null | head -60
        echo "=== mixins that failed to apply"
        grep -aoE 'Mixin apply for mod [^ ]+ failed [^ ]+ from mod [^ ]+ -> [^:]+: [A-Za-z.]+' "$OUT/latest.log" 2>/dev/null \
            | sed -E 's/Mixin apply for mod ([^ ]+) failed ([^ ]+) from mod [^ ]+ -> ([^:]+): .*\.([A-Za-z]+)$/\2 -> \3 (\4)/' | sort -u
        echo "=== problems"
        grep -anE '/ERROR|Exception|Caused by|NoSuchMethod|NoSuchField|AbstractMethod|IncompatibleClass|IllegalAccess|VerifyError|ClassCast|NoClassDefFound|has crashed|Crash Report|Incompatible mods' "$OUT/latest.log" 2>/dev/null \
            | grep -viE 'Error loading class|realms|telemetry|authlib|InvalidCredentials|401|user properties|profile key pair|Failed to verify authentication|Mixin apply for mod|InvalidInjectionException' | cut -c1-400 | head -40
    } >> "$OUT/summary.txt"
    rm -rf "$GAME/mods" "$GAME/saves"
    tail -n +4 "$OUT/summary.txt"
    end "===RESULT: $(grep -o 'RESULT [A-Z ]*' "$OUT/summary.txt" | head -1)==="
fi

# -------------------------------------------------------------------- inside xvfb-run
shift; JAVA="$1"; RT="$2"; GAME="$3"; ASSETS="$4"; AIDX="$5"; T="$6"; OUT="$7"; READY="$8"; QP="$9"
export LIBGL_ALWAYS_SOFTWARE=1 GALLIUM_DRIVER=llvmpipe
LOG="$OUT/client.out"
FAIL='Minecraft has crashed|---- Minecraft Crash Report|Exception in thread "main"|Incompatible mods found|Mod resolution failed|Failed to start the minecraft server'
echo "display $DISPLAY"
# log4j writes logs/ relative to the working directory, not --gameDir
cd "$GAME" || exit 1
S0=$(date +%s.%N)
"$JAVA" $(tr '\n' ' ' < "$RT/jvmargs.txt") -Xmx4G net.fabricmc.loader.impl.launch.knot.KnotClient \
    --version "$T" --gameDir "$GAME" --assetsDir "$ASSETS" --assetIndex "$AIDX" \
    --accessToken 0 --username cbtest --width 1280 --height 720 $QP > "$LOG" 2>&1 < /dev/null &
CPID=$!
echo "client pid $CPID"
PEAK=0
el() { echo "$(date +%s.%N) $S0" | awk '{printf "%.1f", $1 - $2}'; }
rss() { local r; r=$(ps -o rss= -p "$CPID" 2>/dev/null | tr -d ' '); [ -n "$r" ] && [ "$r" -gt "$PEAK" ] && PEAK=$r; }
shot() {
    import -display "$DISPLAY" -window root "png:$OUT/shots/full.png" 2>/dev/null \
        && convert "$OUT/shots/full.png" -resize 960x540 "$OUT/shots/$1.png" && rm -f "$OUT/shots/full.png"
    echo "shot $1.png at $(el) s"
}
STATE=wait
for i in $(seq 1 "${CB_WAIT:-300}"); do
    sleep 1; rss
    if grep -qaE "$READY" "$LOG"; then STATE=ready; break; fi
    if grep -qaE "$FAIL" "$LOG"; then STATE=failed; break; fi
    kill -0 "$CPID" 2>/dev/null || { STATE=exited; break; }
done
echo "ready line ($READY): $STATE at $(el) s"
if [ "$STATE" = ready ]; then
    AT=0
    for t in ${CB_SHOTS-10 25 40}; do
        while [ "$AT" -lt "$t" ]; do sleep 1; AT=$((AT + 1)); rss; done
        kill -0 "$CPID" 2>/dev/null || { echo "client gone before +$t s"; break; }
        shot "$t"
    done
    # CB_EXIT_WAIT: a test mod that stops the client itself gets this long to do it
    for i in $(seq 1 "${CB_EXIT_WAIT:-0}"); do
        kill -0 "$CPID" 2>/dev/null || break
        sleep 1; rss
    done
else
    sleep 3; shot "$STATE"
fi
kill -0 "$CPID" 2>/dev/null && ALIVE=yes || ALIVE=no
grep -qaE "$FAIL" "$LOG" && STATE=failed
kill "$CPID" 2>/dev/null; sleep 5; kill -9 "$CPID" 2>/dev/null
echo "peak RSS $((PEAK / 1024)) MB, alive at the end: $ALIVE"
case "$STATE" in
    ready) [ "$ALIVE" = yes ] && echo "RESULT READY AND KILLED BY THE SCRIPT" || echo "RESULT READY AND STOPPED ITSELF" ;;
    failed) echo "RESULT CRASHED" ;;
    exited) echo "RESULT EXITED BEFORE READY" ;;
    *) echo "RESULT NOT READY" ;;
esac
