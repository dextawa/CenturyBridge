#!/usr/bin/env bash
# cb-s11: a 1.21.10-era mod (intermediary names, no refmap) using the 1.21.10 game rule API and 1.21.10 Fabric API's
# game rule module. Driver runs once the world is loaded: "CB-S11 ok|FAIL <case> <detail>", "CB-S11 summary ok=N fail=M".
#   bash core/testmods/cb-s11/build.sh <out.jar>   (from the repository root on beta2; CB_DATA=<data dir>)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
RT="$DATA/runtime/client-1.21.10"
CP="$DATA/jars/client-1.21.10-intermediary.jar"
for j in $(find "$RT/libraries" "$RT/.fabric/processedMods" -name '*.jar'); do CP="$CP:$j"; done
javac --release 21 -nowarn -proc:none -cp "$CP" -d "$CLS" $(find core/testmods/cb-s11/src -name '*.java')
cp core/testmods/cb-s11/res/* "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
