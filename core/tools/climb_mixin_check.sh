#!/usr/bin/env bash
# climb_mixin_check.sh <workDir> <T> [<runtimeDir>]: the climbed CenturyBridge layers for target T (climb_runtime.sh's
# <workDir>/runtime-T, or <runtimeDir>) run through build gate 3 (Main mixin-check) against T itself -- T's stub, T's
# code jar for the injection-point and handler checks, T's Fabric API (processedMods) and libraries. A layer is built
# and gated against its own W; once it climbs W -> T its mixins are W-era mixins the later steps had to carry (their
# selectors, @Shadow/@Accessor names, @At targets, handler descriptors), and nothing else judges them before a real
# T client loads them and drops each one that no longer fits with a WARN. Every finding here is such a dropped mixin.
# Only the mixins the climbed configs still list are judged (what the client applies); the ones the climb's converter
# delisted are counted per layer ("delisted": in the jar, in no config) -- a layer feature gone without a WARN.
# The other jars of the runtime are indexed as targets too (a mixin retargeted onto a later layer's facade).
# The separately released dependency mods in the runtime are written against their own release and are not checked.
# Collisions: every call site a layer @Redirects is recorded (-Dcb.redirectSites); one site redirected by mixins of
# two layers is a collision -- Mixin keeps the first, skips the other ("@Redirect conflict"), and a required one then
# fails its injection check (1.21.2: the 1.20.2 layer's and the 1.21.2 layer's fabric-tag-api-v1 copies both redirected
# TagLoader's calls; TagLoader did not transform and no world loaded). The rule: at most one layer serves an API on T.
# An API a later release brings back natively retires the earlier layer's copy at that step (retiredClasses, with
# classRenames onto the native class); an API a later layer gives back again is served by that layer alone. Listed in
# <out>/collisions.tsv (site, then every redirecting layer:mixin.handler).
# Run on beta2 from ~/cbwork; <workDir> (relative to it) holds the compiled engine (core/build/classes).
# Output: <runtimeDir>/mixin-check/<layer jar>.txt per layer (CB_OUT=<dir> elsewhere), and a summary line per layer on stdout.
set -uo pipefail
cd ~/cbwork
WD="$1"; T="$2"; RT="${3:-$WD/runtime-$T}"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
[ -d "$RT" ] || { echo "no $RT (run climb_runtime.sh first)"; exit 1; }
[ -d "$WD/core/build/classes/top/dext/centurybridge" ] || { echo "engine not compiled in $WD/core/build/classes"; exit 1; }
case "$T" in
    26.*) STUB="data/jars/client-$T.jar"; CODE="data/jars/client-$T.jar" ;;
    *) STUB="data/jars/client-$T-intermediary.jar"; CODE="data/jars/client-$T-intermediary-code.jar" ;;
esac
FAPI="data/runtime/client-$T/.fabric/processedMods"
for f in "$STUB" "$CODE" "$FAPI"; do [ -e "$f" ] || { echo "missing $f"; exit 1; }; done
OUT="${CB_OUT:-$RT/mixin-check}"
rm -rf "$OUT" && mkdir -p "$OUT"
TOTAL=0
for J in "$RT"/centurybridge-0.3.0+*.jar; do
    [ -f "$J" ] || continue
    L=$(basename "$J" .jar)
    X="$OUT/x-$L"
    mkdir -p "$X" && (cd "$X" && unzip -q -o "$OLDPWD/$J" '*.class' '*.json' 2>/dev/null)
    EXTRA=$(ls "$RT"/*.jar | grep -vxF "$J" | paste -sd:)
    "$JAVA" -Xmx6g -Dcb.extraJars="$EXTRA" -Dcb.libs="data/runtime/client-$T/libraries" -Dcb.fapi="$FAPI" -Dcb.codeJar="$CODE" -Dcb.redirectSites="$OUT/$L.redirects" \
        -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main mixin-check "$X" "$STUB" > "$OUT/$L.txt" 2>&1
    RC=$?
    rm -rf "$X"
    N=$(sed -n 's/^mixin-check: .*, \([0-9]*\) unresolved targets$/\1/p' "$OUT/$L.txt")
    S=$(sed -n 's/^mixin-check bodies.* \([0-9]*\) silent.*/\1/p' "$OUT/$L.txt")
    U=$(sed -n 's/^mixin-check bodies.* \([0-9]*\) unscanned$/\1/p' "$OUT/$L.txt")
    [ -n "$N" ] || { echo "$L: mixin-check did not run (rc=$RC)"; tail -3 "$OUT/$L.txt"; continue; }
    MX=$(grep -E '^    [A-Za-z0-9_$]+: ' "$OUT/$L.txt" | grep -vE '^    (silent|unscanned|unlisted): ' | sed -E 's/^    ([A-Za-z0-9_$]+): .*/\1/' | sort -u | wc -l)
    D=$(sed -n 's/^mixin-check: \([0-9]*\) mixin classes no config lists.*/\1/p' "$OUT/$L.txt")
    echo "$L -> $T: $N problems in $MX mixins, $S silent, $U unscanned, ${D:-0} delisted"
    TOTAL=$((TOTAL + N))
done
# a site (target class, method, instruction index) redirected by more than one layer
for R in "$OUT"/*.redirects; do
    [ -f "$R" ] || continue
    L=$(basename "$R" .redirects)
    awk -F'	' -v L="$L" '{print $1"	"$2"	"$3"	"$4"	"L":"$5}' "$R"
done | sort -t$'	' -k1,3 | awk -F'	' '
    { k = $1 "	" $2 "	" $3; if (k == pk) { by = by "	" $5; n++ } else { if (n > 1) print pk "	" pi by; pk = k; pi = $4; by = "	" $5; n = 1 } }
    END { if (n > 1) print pk "	" pi by }' > "$OUT/collisions.tsv"
C=$(wc -l < "$OUT/collisions.tsv")
echo "climbed layers -> $T: $TOTAL problems, $C redirect collisions (per layer: $OUT/<layer>.txt; $OUT/collisions.tsv)"
cut -f1-4 "$OUT/collisions.tsv" | sed 's/^/    collision: /'
# return-value chain: one target method answered by two or more layers' cancellable RETURN/TAIL injectors that call
# setReturnValue -- the first-applied cancels, every later one (another layer's, Fabric API's, a mod's) never runs
# (core/tools/return_chain_check.py; 26.2 bed layers lost to the 26.1 / 1.20.5 createRoots injectors). A failing check.
python3 "$WD/core/tools/return_chain_check.py" "$OUT/return-chain.tsv" "$RT"/centurybridge-0.3.0+*.jar
RCC=$?
[ "$RCC" -eq 0 ] || { echo "climbed layers -> $T: return-value chain broken ($OUT/return-chain.tsv)"; exit 1; } 
