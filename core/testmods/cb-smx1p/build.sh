#!/usr/bin/env bash
# cb-smx1p: a 1.21.10-era mod (intermediary names, no refmap, no fabric-api) using 1.21.10's int permission levels (CommandSourceStack
# ctor / hasPermission / withPermission / withMaximumPermission, MinecraftServer operator / function / profile levels) and
# RespawnAnchorBlock.canSetSpawn(Level). Driver: "CB-SMX1P ok|FAIL <case> <detail>", "CB-SMX1P summary ok=N fail=M".
#   bash core/testmods/cb-smx1p/build.sh <out.jar>   (from the repository root on beta2; CB_DATA=<data dir>)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
RT="$DATA/runtime/client-1.21.10"
CP="$DATA/jars/client-1.21.10-intermediary.jar"
for j in $(find "$RT/libraries" "$RT/.fabric/processedMods" -name '*.jar'); do CP="$CP:$j"; done
javac --release 21 -nowarn -proc:none -cp "$CP" -d "$CLS" $(find core/testmods/cb-smx1p/src -name '*.java')
cp -r core/testmods/cb-smx1p/res/* "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
