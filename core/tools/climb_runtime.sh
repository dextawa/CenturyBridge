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
BUILT=()
# every layer up to T is built first: a layer's climb reads, at each step, that step's layer jar
# (LayerMembers: the members a later layer restores, which the climbing layer's mixins may shadow or access)
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
        # the separately released mods this layer depends on (build.sh put them in layers/deps/ as it built them)
        for d in "$WD"/layers/deps/*.jar; do
            [ -f "$d" ] || continue
            case " $DEPS_SEEN " in *" $(basename "$d") "*) ;; *) cp "$d" "$OUT/"; DEPS_SEEN="$DEPS_SEEN $(basename "$d")";
                echo "dependency $(basename "$d") (layer $W): installed as is";; esac
        done
        BUILT+=("$W")
    fi
    [ "$W" = "$T" ] && break
done
# newest layer first: a layer's climb reads, at each step, every earlier layer as that layer's own climb left it at the
# step (layers/climb-<R>/steps/<V>__<W>/, Main.convert): a restorer stripped in its climb is not there at runtime
for ((bi = ${#BUILT[@]} - 1; bi >= 0; bi--)); do
    W=${BUILT[$bi]}
    JAR="$WD/layers/centurybridge-0.3.0+$W.jar"
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
    # the converter's own account of the climb: the last step's verdict and the symbols it left as ISSUE (a layer's
    # report has no "unaccounted" line; this used to print nothing for every layer)
    if [ "$W" != "$T" ]; then
        VERDICT=$(sed -n 's/^## .* -- \(.*\)$/\1/p' "$WD"/layers/climb-$W/steps/*__$T/report.md 2>/dev/null | head -1)
        ISSUES=$(cat "$WD"/layers/climb-$W/steps/*/full-audit.tsv 2>/dev/null | awk -F'\t' '$2 ~ /^ISSUE/' | wc -l)
        echo "layer $W -> $T: ${VERDICT:-no report}, $ISSUES converter ISSUE symbols"
    else
        echo "layer $W -> $T: built against $T (build.sh's gates)"
    fi
done
# no climbed layer may declare a method (or field) twice with one name and descriptor: the JVM refuses the class when
# it loads (ClassFormatError), and nothing else notices -- a rename past the layer's own W can land on a member the
# class already has (the wall's RealmsError.errorCode()I on a record component errorCode; dup_members.py)
python3 "$WD/core/tools/dup_members.py" $(ls "$OUT"/centurybridge-0.3.0+*.jar) \
    || { echo "climbed layers -> $T: DUPLICATE MEMBERS (ClassFormatError when the class loads)"; exit 1; }
# every reference the climbed layers make, judged on T (link-check: T's game jar, Fabric API, libraries, the other
# layers and the dependency mods; access through the access wideners the game loads with each layer). build.sh's gates
# judge a layer against its own W only, climb_mixin_check.sh judges its mixins, and the damage tables list V's
# public/protected members: a layer's plain code reading a member its own access widener opened (the 1.20.5 layer's
# CbFieldReshapesGen reading BlockBehaviour.Properties.field_10666, replaced at 1.21.2) is judged by nothing else.
# CLAIMED/BLIND/KNOWN rows are references the JVM cannot link on T; listed in <runtime>/link-check/link-findings.tsv.
# The layers load together, so each one's access widener applies to all (-Dcb.linkcheck.sharedAw: the CenturyBridge
# layer jars only, never the dependency mods); a finding only another layer's access widener satisfies is listed with
# its opener in <runtime>/link-check/shared-aw.tsv: if the opener drops its line, the finding comes back.
case "$T" in 26.*) GAME="data/jars/client-$T.jar" ;; *) GAME="data/jars/client-$T-intermediary.jar" ;; esac
FAPI=$(ls data/runtime/client-$T/mods/fabric-api-*.jar 2>/dev/null | head -1)
LAYERS=$(ls "$OUT"/centurybridge-0.3.0+*.jar)
OTHERS=$(ls "$OUT"/*.jar | grep -vxF "$LAYERS")
LC="$OUT/link-check"
rm -rf "$LC"
"$JAVA" -Xmx16g "-Dcb.linkcheck.sharedAw=centurybridge-0.3.0+*.jar" -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main link-check "$LC" - $LAYERS -- \
    $OTHERS "$GAME" $FAPI "data/runtime/client-$T/libraries" > "$WD/layers/link-check-$T.log" 2>&1 \
    || { echo "climbed layers -> $T: LINK-CHECK FAILED"; tail -5 "$WD/layers/link-check-$T.log"; exit 1; }
# CLAIMED / BLIND / KNOWN: a reference the JVM cannot link on T. TOMBSTONE rows (KNOWN) are calls the converter
# replaced with a lazy failure (centurybridge/gen/Tombstones*), counted apart: still a crash where reached
count() {
    awk -F'\t' -v j="$1" -v t="$2" 'NR > 1 && (j == "" || $3 == j) && ($1 == "CLAIMED" || $1 == "BLIND" || $1 == "KNOWN") && (($2 == "TOMBSTONE") == (t == 1))' \
        "$LC/link-findings.tsv" | wc -l
}
for J in $LAYERS; do
    B=$(basename "$J")
    echo "    link-check ${B%.jar}: $(count "$B" 0) dangling references, $(count "$B" 1) tombstoned calls"
done
echo "climbed layers -> $T link-check: $(count "" 0) dangling references, $(count "" 1) tombstoned calls ($LC/link-findings.tsv)"
echo "climbed layers -> $T link-check: $(($(wc -l < "$LC/shared-aw.tsv") - 1)) findings satisfied only by another layer's access widener ($LC/shared-aw.tsv)"
ls "$OUT"
# the ratchet: no layer may dangle more on T than core/segments/layer-linkcheck-baseline.tsv allows (per layer x T,
# dangling + tombstoned). CB_LAYER_BASELINE=write rewrites T's rows in <workDir>'s copy instead (a burn-down lowers
# them; copy the file back and commit it). Every T at once: layer_linkcheck_steps.sh.
BASE="$WD/core/segments/layer-linkcheck-baseline.tsv"
python3 "$WD/core/tools/layer_linkcheck.py" ratchet "$LC/link-findings.tsv" "$T" "$BASE" \
    $([ "${CB_LAYER_BASELINE:-}" = write ] && echo --write) || exit 1
