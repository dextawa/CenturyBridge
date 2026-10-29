#!/usr/bin/env bash
# cb-smx1c: a 1.20.1-era client mod (intermediary names, no refmap) whose mixin hooks LevelRenderer's chunk-graph walk
# (getRelativeFrom method_3241, initializeQueueForFullUpdate method_38549), which 1.20.2 moved to SectionOcclusionGraph
# (the step's mixinMoves). Climb it to 1.20.2 and enter a world (boot_client_remote.sh CB_WORLD=seed:..); the
# "CB-SMX1C hit" lines climbing = the moved injectors run.
#   bash core/testmods/cb-smx1c/build.sh <outDir>   (from the repository root on beta2; CB_DATA=<data dir>)
set -euo pipefail
OUTD="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
cls=$(mktemp -d)
cp="$DATA/jars/client-1.20.1-intermediary.jar"
for j in $(find "$DATA/runtime/client-1.20.1/libraries" -name '*.jar'); do cp="$cp:$j"; done
javac --release 17 -nowarn -proc:none -cp "$cp" -d "$cls" $(find core/testmods/cb-smx1c/src -name '*.java')
cp -r core/testmods/cb-smx1c/res/. "$cls/"
(cd "$cls" && jar cf "$OUTD/cb-smx1c.jar" .)
rm -rf "$cls"
echo "-> $OUTD/cb-smx1c.jar"
