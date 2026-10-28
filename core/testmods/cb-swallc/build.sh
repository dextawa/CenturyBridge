#!/usr/bin/env bash
# cb-swallc: a 1.21.11-era client mod checking the 26.1 wall layer's client routes (see SwallClient).
#   bash core/testmods/cb-swallc/build.sh <out.jar>   (from the repository root on beta2; CB_DATA=<data dir>)
# Climb it with boot_client_remote.sh (CB_CLIMB=1 CB_FROM=1.21.11 CB_WORLD=seed:<seed>) to 26.1.
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
RT="$DATA/runtime/client-1.21.11"
CP="$DATA/jars/client-1.21.11-intermediary.jar"
for j in $(find "$RT/libraries" "$RT/.fabric/processedMods" -name '*.jar'); do CP="$CP:$j"; done
javac --release 21 -nowarn -proc:none -cp "$CP" -d "$CLS" $(find core/testmods/cb-swallc/src -name '*.java')
cp -r core/testmods/cb-swallc/res/. "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
