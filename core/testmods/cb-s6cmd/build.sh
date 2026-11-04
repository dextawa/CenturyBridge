#!/usr/bin/env bash
# Build the 1.21.5 s6cmd JVM harness. Convert its jar 1.21.5 -> 1.21.6 with the step layer, then run
# cb.s6cmd.Harness on the 1.21.6 intermediary code jar + runtime libraries + the built layer jar (use -noverify:
# that analysis code jar lacks stack-map frames in unchanged interface defaults; checks/conversions still execute).
#   bash core/testmods/cb-s6cmd/build.sh <existing-out-dir>
# CB_DATA=<data dir> when it is not ./data; CB_CPSEP=";" on Windows.
set -euo pipefail
OUT="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
S="${CB_CPSEP:-:}"
CLS=$(mktemp -d)
LIBS=$(find "$DATA"/runtime/client-1.21.5/libraries -name '*.jar' | tr '\n' "$S")
javac --release 21 -nowarn -proc:none -cp "$DATA/jars/client-1.21.5-intermediary.jar$S$LIBS" \
    -d "$CLS" $(find core/testmods/cb-s6cmd/src -name '*.java')
(cd "$CLS" && jar cf "$OUT/cb-s6cmd.jar" .)
rm -rf "$CLS"
echo "-> $OUT/cb-s6cmd.jar"
