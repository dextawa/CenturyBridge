#!/usr/bin/env bash
# Build the 1.20.6 source fixture, then climb it to 1.21 for the real-client record-subclass test.
#   bash core/testmods/cb-recordtest/build.sh <out.jar>   (CB_DATA=<data dir>, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
LIBS=$(find "$DATA"/runtime/client-1.20.6/libraries "$DATA"/runtime/client-1.20.6/.fabric/processedMods -name '*.jar' | tr '\n' "${CB_CPSEP:-:}")
javac --release 21 -nowarn -proc:none -cp "$DATA/jars/client-1.20.6-intermediary.jar${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-recordtest/src -name '*.java')
cp -r core/testmods/cb-recordtest/res/. "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
