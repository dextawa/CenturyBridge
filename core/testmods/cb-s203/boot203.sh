#!/usr/bin/env bash
# boot203.sh <tag>: real 1.20.3 server check for s203, on beta2 from ~/cbwork, in the uploaded tree agents/s203/<tag>.
# Builds the climb runtime for 1.20.3 (the 1.20.2 layer climbed one step + the 1.20.3 layer), compiles cb-s203 against
# 1.20.2 and cb-s203n against 1.20.3, converts cb-s203 1.20.2 -> 1.20.3 (convert-climb, CB_FROM=1.20.2), boots a Fabric
# 1.20.3 server (agents/s203/srv-1.20.3) with Fabric API, the layers, the converted mod and the native one.
set -uo pipefail
cd ~/cbwork
TAG="$1"; WD=agents/s203/$TAG
JDK=~/cbtest/jdk-25.0.4.1+1/bin
export PATH=$JDK:$PATH
SRV=$HOME/cbwork/agents/s203/srv-1.20.3
if [ ! -f "$SRV/fabric-server.jar" ]; then
    mkdir -p "$SRV"
    curl -fsSL -o "$SRV/fabric-server.jar" "https://meta.fabricmc.net/v2/versions/loader/1.20.3/0.19.3/1.1.0/server/jar" \
        || { echo "server launcher download failed"; exit 1; }
    cp ~/cbtest/server-1.20.2/eula.txt ~/cbtest/server-1.20.2/server.properties.orig "$SRV/"
fi
[ -d "$WD/runtime-1.20.3" ] || bash $WD/core/tools/climb_runtime.sh $WD 1.20.3 > $WD/rt-1.20.3.log 2>&1 \
    || { tail -20 $WD/rt-1.20.3.log; echo 'RUNTIME FAILED'; exit 1; }
TM=$WD/tm; rm -rf $TM && mkdir -p $TM/a $TM/n
R=data/runtime/client-1.20.2
CP="data/jars/client-1.20.2-intermediary.jar:$(find $R/libraries $R/.fabric/processedMods -name '*.jar' | tr '\n' ':')"
javac -nowarn -proc:none --release 17 -cp "$CP" -d $TM/a $(find $WD/core/testmods/cb-s203/src -name '*.java') > $TM/javac-a.log 2>&1 \
    || { echo "cb-s203 compile failed"; tail -20 $TM/javac-a.log; exit 1; }
cp -r $WD/core/testmods/cb-s203/res/. $TM/a/ && jar cf $TM/cb-s203.jar -C $TM/a .
R=data/runtime/client-1.20.3
CP="data/jars/client-1.20.3-intermediary.jar:$(find $R/libraries $R/.fabric/processedMods -name '*.jar' | tr '\n' ':')"
javac -nowarn -proc:none --release 17 -cp "$CP" -d $TM/n $(find $WD/core/testmods/cb-s203n/src -name '*.java') > $TM/javac-n.log 2>&1 \
    || { echo "cb-s203n compile failed"; tail -20 $TM/javac-n.log; exit 1; }
cp -r $WD/core/testmods/cb-s203n/res/. $TM/n/ && jar cf $TM/cb-s203n.jar -C $TM/n .
CB_FROM=1.20.2 CB_NATIVE_MODS="$TM/cb-s203n.jar" CB_SERVER_DIR=$SRV bash $WD/core/tools/boot_climb.sh $WD 1.20.3 $TAG $TM/cb-s203.jar > $WD/boot-$TAG.out 2>&1
cat $WD/boot-$TAG.out | head -60
grep -a 'CB-S203' $SRV/boot-$TAG.log | cut -c1-300
rm -rf $SRV/world-$TAG
