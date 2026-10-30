#!/usr/bin/env bash
# cb-s205r: a 1.20.1-era mod with its own recipe type and legacy recipe serializer ("CB-S205R ..."; S205RTestMod).
#   bash core/testmods/cb-s205r/build.sh <outDir>   (from the repository root on beta2; CB_DATA=<data dir>) -> <outDir>/cb-s205r.jar
# Climb it from 1.20.1 (boot_climb.sh / boot_client_remote.sh, the default CB_FROM).
set -euo pipefail
OUTD="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
cls=$(mktemp -d)
cp="$DATA/jars/client-1.20.1-intermediary.jar"
for j in $(find "$DATA/runtime/client-1.20.1/libraries" "$DATA/runtime/client-1.20.1/.fabric/processedMods" -name '*.jar'); do cp="$cp:$j"; done
javac --release 17 -nowarn -proc:none -cp "$cp" -d "$cls" $(find core/testmods/cb-s205r/src -name '*.java')
cp -r core/testmods/cb-s205r/res/. "$cls/"
(cd "$cls" && jar cf "$OUTD/cb-s205r.jar" .)
rm -rf "$cls"
echo "-> $OUTD/cb-s205r.jar"
