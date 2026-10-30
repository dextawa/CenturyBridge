#!/usr/bin/env bash
# boot.sh <workDir> <tag> [native-only]: real 1.21.2 server check for the furnace recipesUsed retype (skilteng).
# cb-sk212 (1.21.1, intermediary, its own access widener) is compiled against the 1.21.1 stub widened by it, climbed
# 1.21.1 -> 1.21.2 (boot_climb.sh, CB_FROM=1.21.1) and booted next to cb-sk212n (native 1.21.2, not converted) on
# agents/skilteng/srv/srv-1.21.2 (libraries from CB_SRV_SRC, default agents/s212/srv-1.21.2). Expected: "CB-SK212 DONE 6/6"; "CB-SK212N savesWithProbe N" with N >= 2 (the furnace saves
# cb-sk212 made while its probe key was in 1.21.2's map); 0 in native-only.
# Run on beta2 from ~/cbwork after climb_runtime.sh <workDir> 1.21.2.
set -uo pipefail
cd ~/cbwork
WD="$1"; TAG="$2"; MODE="${3:-}"
export PATH=$HOME/cbtest/jdk-25.0.4.1+1/bin:$PATH
SRV=$HOME/cbwork/agents/skilteng/srv/srv-1.21.2
if [ ! -f "$SRV/fabric-server.jar" ]; then
    # the game, libraries and remapped jars from a 1.21.2 server another agent provisioned (loader 0.19.3); the launcher
    # from Fabric meta when that copy has none (as cb-s203's boot203.sh)
    SRC=${CB_SRV_SRC:-$HOME/cbwork/agents/s212/srv-1.21.2}
    mkdir -p "$SRV/.fabric" && cp -r "$SRC/libraries" "$SRC/versions" "$SRV/" && cp -r "$SRC/.fabric/." "$SRV/.fabric/"         || { echo "cannot copy a 1.21.2 server from $SRC"; exit 1; }
    cp ~/cbtest/server-1.21.4/eula.txt ~/cbtest/server-1.21.4/server.properties.orig "$SRV/" || { echo "no eula / properties"; exit 1; }
    curl -fsSL -o "$SRV/fabric-server.jar" "https://meta.fabricmc.net/v2/versions/loader/1.21.2/0.19.3/1.1.0/server/jar"         || { echo "server launcher download failed"; exit 1; }
fi
TM=$WD/tm-sk212; rm -rf $TM && mkdir -p $TM/a $TM/n
R=data/runtime/client-1.21.1
STUB=$TM/stub-1.21.1.jar
(cd $WD && CB_DATA=$HOME/cbwork/data bash core/tools/make_aw_stub.sh 1.21.1 $HOME/cbwork/data/jars/client-1.21.1-intermediary.jar \
    $HOME/cbwork/$STUB core/testmods/cb-sk212/res/cb_sk212.accesswidener > /dev/null) || { echo "aw stub 1.21.1 failed"; exit 1; }
javac -nowarn -proc:none --release 21 -cp "$STUB:$(find $R/libraries $R/.fabric/processedMods -name '*.jar' | tr '\n' ':')" -d $TM/a \
    $(find $WD/core/testmods/cb-sk212/src -name '*.java') || { echo "cb-sk212 compile failed"; exit 1; }
cp $WD/core/testmods/cb-sk212/res/* $TM/a/ && jar cf $TM/cb-sk212.jar -C $TM/a .
R=data/runtime/client-1.21.2
javac -nowarn -proc:none --release 21 -cp "data/jars/client-1.21.2-intermediary.jar:$(find $R/libraries $R/.fabric/processedMods -name '*.jar' | tr '\n' ':')" -d $TM/n \
    $(find $WD/core/testmods/cb-sk212/native/src -name '*.java') || { echo "cb-sk212n compile failed"; exit 1; }
cp $WD/core/testmods/cb-sk212/native/res/* $TM/n/ && jar cf $TM/cb-sk212n.jar -C $TM/n .
rm -f $STUB
if [ "$MODE" = "native-only" ]; then MODS=""; else MODS="$TM/cb-sk212.jar"; fi
CB_FROM=1.21.1 CB_NATIVE_MODS="$TM/cb-sk212n.jar" CB_SERVER_DIR=$SRV bash $WD/core/tools/boot_climb.sh $WD 1.21.2 $TAG $MODS > $WD/boot-$TAG.out 2>&1
head -40 $WD/boot-$TAG.out
grep -a 'CB-SK212' $SRV/boot-$TAG.log | cut -c1-300
rm -rf $SRV/world-$TAG
