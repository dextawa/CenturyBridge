#!/usr/bin/env bash
# Native 1.21 control for cb-smx1's converted mixin route.
#   bash core/testmods/cb-attrnative/build.sh <out.jar>   (CB_DATA=<data dir>, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
MIXIN="${CB_MIXIN:-$DATA/runtime/client-1.20.3/libraries/net/fabricmc/sponge-mixin/0.17.3+mixin.0.8.7/sponge-mixin-0.17.3+mixin.0.8.7.jar}"
LIBS=$(find "$DATA"/runtime/client-1.21/libraries "$DATA"/runtime/client-1.21/.fabric/processedMods -name '*.jar' ! -name 'sponge-mixin-*' | tr '\n' "${CB_CPSEP:-:}")
if [ -n "${CB_LAYER:-}" ]; then
    LIBS="$LIBS${CB_CPSEP:-:}$CB_LAYER"
fi
javac --release 21 -nowarn -proc:none -cp "$DATA/jars/client-1.21-intermediary.jar${CB_CPSEP:-:}$MIXIN${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-attrnative/src -name '*.java')
cp -r core/testmods/cb-attrnative/res/. "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
