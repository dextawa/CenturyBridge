#!/usr/bin/env bash
# cb-swmesh: a 1.21.11-era client mod dumping model part trees (see SwMeshClient).
#   bash core/testmods/cb-swmesh/build.sh <out.jar>   (from the repository root on beta2; CB_DATA=<data dir>)
# Reference: boot_client_remote.sh CB_CLIMB=0 on 1.21.11; check: CB_CLIMB=1 CB_FROM=1.21.11 on 26.1 (CB_READY='CB-SWMESH [0-9]+/[0-9]+'); the part dumps must match.
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
RT="$DATA/runtime/client-1.21.11"
CP="$DATA/jars/client-1.21.11-intermediary.jar"
for j in $(find "$RT/libraries" "$RT/.fabric/processedMods" -name '*.jar'); do CP="$CP:$j"; done
javac --release 21 -nowarn -proc:none -cp "$CP" -d "$CLS" $(find core/testmods/cb-swmesh/src -name '*.java')
cp -r core/testmods/cb-swmesh/res/. "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
