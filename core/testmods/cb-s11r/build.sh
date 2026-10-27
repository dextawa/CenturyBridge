#!/usr/bin/env bash
# cb-s11r: a 1.21.8-era client mod registering DimensionRenderingRegistry sky / cloud / weather renderers (see RendClient).
#   bash core/testmods/cb-s11r/build.sh <out.jar>   (from the repository root on beta2; CB_DATA=<data dir>)
# Climb it with boot_client_remote.sh (CB_CLIMB=1 CB_FROM=1.21.8) to the target.
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
RT="$DATA/runtime/client-1.21.8"
CP="$DATA/jars/client-1.21.8-intermediary.jar"
for j in $(find "$RT/libraries" "$RT/.fabric/processedMods" -name '*.jar'); do CP="$CP:$j"; done
javac --release 21 -nowarn -proc:none -cp "$CP" -d "$CLS" $(find core/testmods/cb-s11r/src -name '*.java')
cp -r core/testmods/cb-s11r/res/. "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
