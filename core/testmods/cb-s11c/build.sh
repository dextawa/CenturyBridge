#!/usr/bin/env bash
# cb-s11c: s11's client checks of the edit-game-rules screen, 1.21.10 -> 1.21.11 (D:/tmp/main/client_checks/s11.md, part
# 1-5). A 1.21.10-era client mod (intermediary names, no refmap) with Fabric's game rule API: boolean / integer / double /
# enum rules, a custom category, and two row subclasses (BooleanRuleEntry / IntegerRuleEntry with 1.21.10's super call)
# added by a mixin on the screen's init. Once a world is joined it opens EditGameRulesScreen, reads every row, edits the
# mod rows through their widgets, presses Done, applies the rules to the world and reads them back; prints
# "CB-S11C ok|FAIL ..", "CB-S11C N/T", then stops the client. Climb it one step: CB_FROM=1.21.10, T=1.21.11.
# The render-type checks (R1-R3) are not covered here.
#   bash core/testmods/cb-s11c/build.sh <out.jar>   (from the repository root on beta2; CB_DATA=<data dir>)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
RT="$DATA/runtime/client-1.21.10"
FAPI=$(mktemp -d)
FJAR="$(cd "$RT/mods" && pwd)/$(ls "$RT/mods" | grep '^fabric-api-' | head -1)"
(cd "$FAPI" && unzip -q "$FJAR" 'META-INF/jars/*')
CP="$DATA/jars/client-1.21.10-intermediary.jar"
for j in $(find "$RT/libraries" "$FAPI/META-INF/jars" -name '*.jar'); do CP="$CP:$j"; done
javac --release 21 -nowarn -proc:none -cp "$CP" -d "$CLS" $(find core/testmods/cb-s11c/src -name '*.java')
cp -r core/testmods/cb-s11c/res/. "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS" "$FAPI"
echo "-> $OUT"
