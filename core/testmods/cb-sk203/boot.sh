#!/usr/bin/env bash
# boot.sh <workDir> <tag> [native-only]: real 1.20.3 server check for Kilt E3 (skilteng). cb-sk203 (1.20.2, intermediary,
# its own access widener) is compiled against the 1.20.2 stub widened by it, climbed 1.20.2 -> 1.20.3 (boot_climb.sh,
# CB_FROM=1.20.2) and booted next to cb-sk203n (native 1.20.3, not converted) on agents/s203/srv-1.20.3.
# Expected: "CB-SK203 DONE 7/7"; "CB-SK203N stoneId N (vanilla N) getIdCalls 2" (two getId calls on the probe keys
# after cb-sk203's removals; 0 in native-only). Run on beta2 from ~/cbwork after climb_runtime.sh <workDir> 1.20.3.
set -uo pipefail
cd ~/cbwork
WD="$1"; TAG="$2"; MODE="${3:-}"
export PATH=$HOME/cbtest/jdk-25.0.4.1+1/bin:$PATH
SRV=$HOME/cbwork/agents/s203/srv-1.20.3
TM=$WD/tm-sk203; rm -rf $TM && mkdir -p $TM/a $TM/n
R=data/runtime/client-1.20.2
STUB=$TM/stub-1.20.2.jar
(cd $WD && CB_DATA=$HOME/cbwork/data bash core/tools/make_aw_stub.sh 1.20.2 $HOME/cbwork/data/jars/client-1.20.2-intermediary.jar \
    $HOME/cbwork/$STUB core/testmods/cb-sk203/res/cb_sk203.accesswidener > /dev/null) || { echo "aw stub 1.20.2 failed"; exit 1; }
javac -nowarn -proc:none --release 17 -cp "$STUB:$(find $R/libraries $R/.fabric/processedMods -name '*.jar' | tr '\n' ':')" -d $TM/a \
    $(find $WD/core/testmods/cb-sk203/src -name '*.java') || { echo "cb-sk203 compile failed"; exit 1; }
cp $WD/core/testmods/cb-sk203/res/* $TM/a/ && jar cf $TM/cb-sk203.jar -C $TM/a .
R=data/runtime/client-1.20.3
STUBN=$TM/stub-1.20.3.jar
(cd $WD && CB_DATA=$HOME/cbwork/data bash core/tools/make_aw_stub.sh 1.20.3 $HOME/cbwork/data/jars/client-1.20.3-intermediary.jar \
    $HOME/cbwork/$STUBN core/testmods/cb-sk203/native/res/cb_sk203n.accesswidener > /dev/null) || { echo "aw stub 1.20.3 failed"; exit 1; }
javac -nowarn -proc:none --release 17 -cp "$STUBN:$(find $R/libraries $R/.fabric/processedMods -name '*.jar' | tr '\n' ':')" -d $TM/n \
    $(find $WD/core/testmods/cb-sk203/native/src -name '*.java') || { echo "cb-sk203n compile failed"; exit 1; }
cp $WD/core/testmods/cb-sk203/native/res/* $TM/n/ && jar cf $TM/cb-sk203n.jar -C $TM/n .
rm -f $STUB $STUBN
if [ "$MODE" = "native-only" ]; then MODS=""; else MODS="$TM/cb-sk203.jar"; fi
CB_FROM=1.20.2 CB_NATIVE_MODS="$TM/cb-sk203n.jar" CB_SERVER_DIR=$SRV bash $WD/core/tools/boot_climb.sh $WD 1.20.3 $TAG $MODS > $WD/boot-$TAG.out 2>&1
head -40 $WD/boot-$TAG.out
grep -a 'CB-SK203' $SRV/boot-$TAG.log | cut -c1-300
rm -rf $SRV/world-$TAG
