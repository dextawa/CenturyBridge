#!/usr/bin/env bash
#   bash core/testmods/cb-randomtest/build.sh <out.jar>   (CB_DATA=<data dir>, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
LIBS=$(find "$DATA"/runtime/client-1.20.6/libraries "$DATA"/runtime/client-1.20.6/.fabric/processedMods -name '*.jar' | tr '\n' "${CB_CPSEP:-:}")
javac --release 21 -nowarn -proc:none -cp "$DATA/jars/client-1.20.6-intermediary.jar${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-randomtest/src -name '*.java')
cp -r core/testmods/cb-randomtest/res/. "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
