#!/usr/bin/env bash
# layer_linkcheck_steps.sh <workDir> <T>: the climbed layers link-checked at every release up to T, from one
# climb_runtime.sh run to T whose layers/ is still there (each layers/climb-<W>/steps/<V>__<R>/ holds layer W climbed
# to R; layers/centurybridge-0.3.0+<R>.jar is R's own layer). At each R the subjects are every layer up to R as it
# stands there, on R's game jar, Fabric API and libraries, as climb_runtime.sh judges them on T. Then
# layer_linkcheck.py ratchets every R's rows against <workDir>/core/segments/layer-linkcheck-baseline.tsv (exit 1 when
# any (R, layer) rises; CB_LAYER_BASELINE=write rewrites every R's rows instead) and attributes T's rows to the step
# where each started dangling (<workDir>/layer-lc/attributed-<T>.tsv).
# Run on beta2 from ~/cbwork; <workDir> relative to it.
set -uo pipefail
cd ~/cbwork
WD="$1"; T="$2"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
RELEASES=(1.20.1 1.20.2 1.20.3 1.20.4 1.20.5 1.20.6 1.21 1.21.1 1.21.2 1.21.3 1.21.4 1.21.5 1.21.6 1.21.7 1.21.8 1.21.9 1.21.10 1.21.11 26.1 26.1.1 26.1.2 26.2)
L="$WD/layers"
[ -d "$L" ] || { echo "no $L (run climb_runtime.sh $WD $T first)"; exit 1; }
OUT="$WD/layer-lc"
rm -rf "$OUT" && mkdir -p "$OUT"
DEPS=$(ls "$L"/deps/*.jar 2>/dev/null)
for ((i = 1; i < ${#RELEASES[@]}; i++)); do
    R=${RELEASES[$i]}; P=${RELEASES[$((i - 1))]}
    SUBJ=""
    for J in "$L"/centurybridge-0.3.0+*.jar; do
        W=$(basename "$J" .jar); W=${W#centurybridge-0.3.0+}
        if [ "$W" = "$R" ]; then SUBJ="$SUBJ $J"
        elif [ -f "$L/climb-$W/steps/${P}__$R/centurybridge-0.3.0+$W+$R.jar" ]; then SUBJ="$SUBJ $L/climb-$W/steps/${P}__$R/centurybridge-0.3.0+$W+$R.jar"
        fi
    done
    [ -n "$SUBJ" ] || { [ "$R" = "$T" ] && break; continue; }
    case "$R" in 26.*) GAME="data/jars/client-$R.jar" ;; *) GAME="data/jars/client-$R-intermediary.jar" ;; esac
    FAPI=$(ls data/runtime/client-$R/mods/fabric-api-*.jar 2>/dev/null | head -1)
    "$JAVA" -Xmx16g "-Dcb.linkcheck.sharedAw=centurybridge-0.3.0+*.jar" -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main link-check "$OUT/$R" - $SUBJ -- \
        $DEPS "$GAME" $FAPI "data/runtime/client-$R/libraries" > "$OUT/$R.log" 2>&1 \
        || { echo "$R: LINK-CHECK FAILED"; tail -5 "$OUT/$R.log"; exit 1; }
    echo "$R: $(echo $SUBJ | wc -w) layers, $(awk -F'\t' 'NR > 1 && ($1 == "CLAIMED" || $1 == "BLIND" || $1 == "KNOWN")' "$OUT/$R/link-findings.tsv" | wc -l) rows"
    [ "$R" = "$T" ] && break
done
CB_DATA=data python3 "$WD/core/tools/layer_linkcheck.py" steps "$OUT" "$T" "$WD/core/segments/layer-linkcheck-baseline.tsv" "$OUT/attributed-$T.tsv"     $([ "${CB_LAYER_BASELINE:-}" = write ] && echo --write)
