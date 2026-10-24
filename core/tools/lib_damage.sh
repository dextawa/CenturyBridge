#!/usr/bin/env bash
# calibrated library damage for every climb step (Main lib-diff) -> core/segments/damage-lib/<V>__<W>.tsv
# run on beta2: upload core/src + core/segments/damage-lib/artifacts.tsv to ~/cbwork/agents/$NAME/src, then
#   NAME=<name> bash lib_damage.sh   (CALIB="<releases>" picks the V-vs-V calibration runs, CALIB_ONLY=1 stops after them)
#   calibration: V vs V (same dir, same release) and identical jars must give 0 rows
set -uo pipefail
cd ~/cbwork
export PATH=$HOME/cbtest/jdk-25.0.4.1+1/bin:$PATH
W=agents/${NAME:?NAME}
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
rm -rf $W/classes && mkdir -p $W/classes $W/out
javac -nowarn -proc:none -cp "$LIBS" -d $W/classes $(find $W/src/core/src -name '*.java') || exit 1
ART=$W/src/core/segments/damage-lib/artifacts.tsv
R=(1.20.1 1.20.2 1.20.3 1.20.4 1.20.5 1.20.6 1.21 1.21.1 1.21.2 1.21.3 1.21.4 1.21.5 1.21.6 1.21.7 1.21.8 1.21.9 1.21.10 1.21.11 26.1 26.1.1 26.1.2 26.2)
. "$W/src/core/tools/lib_dirs.sh"
run() { java -Xmx4g -cp "$W/classes:$LIBS" top.dext.centurybridge.Main lib-diff "$@"; }
summ() { echo "$1 rows=$(($(grep -c . $2) - 1)) $(cut -f1 $2 | tail -n +2 | sort | uniq -c | sort -rn | head -8 | awk '{printf "%s:%s ", $2, $1}')"; }
for V in ${CALIB:-1.20.1 1.20.5 1.21.11 26.2}; do
  run $(libdir $V) $(libdir $V) $W/out/calib-$V.tsv $ART $V $V > $W/out/calib-$V.log 2>&1
  summ "calib $V-vs-$V" $W/out/calib-$V.tsv
done
[ -n "${CALIB_ONLY:-}" ] && exit 0
for ((i = 0; i + 1 < ${#R[@]}; i++)); do
  V=${R[$i]}; N=${R[$((i + 1))]}
  run $(libdir $V) $(libdir $N) $W/out/${V}__$N.tsv $ART $V $N > $W/out/${V}__$N.log 2>&1
  summ "${V}__$N" $W/out/${V}__$N.tsv
done
echo LIB_DONE
