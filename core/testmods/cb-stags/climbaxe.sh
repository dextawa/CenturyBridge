#!/usr/bin/env bash
# climbaxe.sh: the climb check for the generated tag-drift bridge. Builds this agent's 1.21.5 step layer through
# build.sh's gates, climbs it 1.21.5 -> 26.1 with Main convert-climb exactly as climb_runtime.sh climbs a layer,
# and prints the compat tag centurybridge:compat/1_21_4/mineable/axe as it ships in the climbed jar, plus the
# getter class, so it can be compared with 1.21.4's mineable/axe and 26.1's. Run on beta2 from ~/cbwork after
# remote_step.sh has uploaded the agent's tree.
set -uo pipefail
cd ~/cbwork
WD=agents/stags
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
export PATH=$HOME/cbtest/jdk-25.0.4.1+1/bin:$PATH
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
(cd $WD && CB_DATA=$HOME/cbwork/data CB_SHIM=core/shims/steps/1.21.5 CB_WIRING=core/segments/steps/1.21.4__1.21.5.json \
    bash build.sh 1.21.5 layers > build-1.21.5.log 2>&1) || { echo "layer build failed"; tail -20 $WD/build-1.21.5.log; exit 1; }
grep -E 'check:|^->' $WD/build-1.21.5.log
OUT=$WD/climbaxe
rm -rf $OUT && mkdir -p $OUT
"$JAVA" -Xmx8g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert-climb "$WD/core/segments" 1.21.5 26.1 \
    $OUT/conv $WD/layers/centurybridge-0.3.0+1.21.5.jar > $OUT/climb.log 2>&1 || { echo "climb failed"; tail -5 $OUT/climb.log; exit 1; }
JAR=$(ls $OUT/conv/*+26.1.jar)
echo "climbed: $JAR"
unzip -l "$JAR" | grep -E 'compat/1_21_4/mineable/axe|CbTagCompat'
unzip -p "$JAR" data/centurybridge/tags/block/compat/1_21_4/mineable/axe.json > $OUT/axe-26.1.json
echo "entries: $(grep -c '"id"' $OUT/axe-26.1.json)"
cd $OUT && unzip -o -q "$JAR" 'top/dext/centurybridge/step/v1_21_5/rt/CbTagCompat.class' && \
    javap -c -p -cp . top.dext.centurybridge.step.v1_21_5.rt.CbTagCompat | grep -E 'Method|Field|String' | head -20
