#!/usr/bin/env bash
# climb_runtime.sh <workDir> <T>: the CenturyBridge runtime for target T, built the climb way. Every
# step layer of the checkout in <workDir> (relative to ~/cbwork) up to T -- step 1.20.1 -> 1.20.2 is
# the 1.20.2 tree, every later one core/shims/steps/<W> -- is built against its own release W with
# build.sh's gates, then climbed from W to T with Main convert-climb exactly like a mod (a layer is
# W-era code: later steps bridge it the way they bridge any W mod). The climbed layers land in
# <workDir>/runtime-T/, one Fabric mod each; climb_check.sh link-checks against that directory. The mods a layer depends
# on that CenturyBridge releases separately (core/mods/<mod>, e.g. centurybridge-stbir1; build.sh builds them into
# layers/deps/) land there too, unconverted: they are written against the release their fabric.mod.json names and
# link only that release's libraries, so they are installed as they are, never climbed.
# Run on beta2 from ~/cbwork; <workDir> holds core/ and build.sh (an agent dir, or remote_step's upload).
set -uo pipefail
cd ~/cbwork
WD="$1"; T="$2"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
export PATH=$HOME/cbtest/jdk-25.0.4.1+1/bin:$PATH
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
RELEASES=(1.20.1 1.20.2 1.20.3 1.20.4 1.20.5 1.20.6 1.21 1.21.1 1.21.2 1.21.3 1.21.4 1.21.5 1.21.6 1.21.7 1.21.8 1.21.9 1.21.10 1.21.11 26.1 26.1.1 26.1.2 26.2)
OUT="$WD/runtime-$T"
rm -rf "$OUT" "$WD/layers" && mkdir -p "$OUT" "$WD/layers"
DEPS_SEEN=""
# every layer is built first, then each climbs: a layer's step W' reads layers/centurybridge-0.3.0+<W'>.jar, which must
# exist when the layer before it climbs (built one by one, each layer climbed through steps whose layers were not built
# yet -- the members those layers restore and their redirect bodies were missing from the judgement)
LAYERS=()
for ((i = 0; i + 1 < ${#RELEASES[@]}; i++)); do
    V=${RELEASES[$i]}; W=${RELEASES[$((i + 1))]}
    if [ "$V" = 1.20.1 ]; then
        BUILD="bash build.sh 1.20.2 layers"
    elif [ -d "$WD/core/shims/steps/$W" ]; then
        BUILD="CB_SHIM=core/shims/steps/$W CB_WIRING=core/segments/steps/${V}__$W.json bash build.sh $W layers"
    else
        BUILD=""
    fi
    if [ -n "$BUILD" ]; then
        (cd "$WD" && eval "CB_DATA=\$HOME/cbwork/data $BUILD" > "layers/build-$W.log" 2>&1) \
            || { echo "layer $W: BUILD FAILED"; tail -5 "$WD/layers/build-$W.log"; exit 1; }
        LAYERS+=("$W")
    fi
    [ "$W" = "$T" ] && break
done
for W in "${LAYERS[@]}"; do
    {
        JAR="$WD/layers/centurybridge-0.3.0+$W.jar"
        # the separately released mods this layer depends on (build.sh put them in layers/deps/ as it built them)
        for d in "$WD"/layers/deps/*.jar; do
            [ -f "$d" ] || continue
            case " $DEPS_SEEN " in *" $(basename "$d") "*) ;; *) cp "$d" "$OUT/"; DEPS_SEEN="$DEPS_SEEN $(basename "$d")";
                echo "dependency $(basename "$d") (layer $W): installed as is";; esac
        done
        if [ "$W" = "$T" ]; then
            cp "$JAR" "$OUT/"
        else
            rm -rf "$WD/layers/climb-$W"
            # each step reads its own layer (layers/centurybridge-0.3.0+<step's W>.jar), as climb_mods.sh does for mods:
            # the members a later layer restores (LayerMembers) and its redirect bodies are what this layer's mixins meet
            "$JAVA" -Xmx8g -Dcb.layersDir="$WD/layers" -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" "$W" "$T" \
                "$WD/layers/climb-$W" "$JAR" > "$WD/layers/climb-$W.log" 2>&1 \
                || { echo "layer $W: CLIMB FAILED"; tail -5 "$WD/layers/climb-$W.log"; exit 1; }
            cp "$WD/layers/climb-$W/centurybridge-0.3.0+$W+$T.jar" "$OUT/"
        fi
        echo "layer $W -> $T: $(grep -h 'unaccounted' "$WD/layers/climb-$W/steps"/*/report.md 2>/dev/null | head -1)"
    }
done
ls "$OUT"
