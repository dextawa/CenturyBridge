#!/usr/bin/env bash
# buildn.sh: compile the native 26.1 test mod cb-swalln (sources uploaded to <WD>/tm-src/cb-swalln) against 26.1.
# Output <WD>/tm/cb-swalln.jar. Run on beta2 from ~/cbwork.
set -uo pipefail
cd ~/cbwork
WD=${CB_WD:-agents/swall7}
JDK=~/cbtest/jdk-25.0.4.1+1/bin
R=data/runtime/client-26.1
CP="data/jars/client-26.1.jar:$(find $R/libraries -name '*.jar' | tr '\n' ':')$(find $R/.fabric/processedMods -name '*.jar' | tr '\n' ':')"
SRC=$WD/tm-src/cb-swalln
OUT=$WD/tm/n
rm -rf "$OUT" && mkdir -p "$OUT"
"$JDK/javac" -nowarn -proc:none --release 21 -cp "$CP" -d "$OUT" $(find $SRC/src -name '*.java') || exit 1
cp $SRC/res/*.json "$OUT/"
rm -f $WD/tm/cb-swalln.jar
"$JDK/jar" cf $WD/tm/cb-swalln.jar -C "$OUT" .
ls -la $WD/tm/cb-swalln.jar
