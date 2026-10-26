#!/usr/bin/env bash
# cb-s5c: a 1.21.4-era client mod (intermediary names, no refmap, no Fabric API) for s5's client checks of the
# 1.21.4 -> 1.21.5 step (D:/tmp/main/client_checks/s5.md): its own SwordItem subclass (appendHoverText,
# inventoryTick, getBreakingSound overrides) and a PickaxeItem subclass. Once a world is joined it drives
# client-side inventoryTick (hotbar selected / not selected, armor, offhand, a villager holding the sword), the
# tooltip (normal / advanced, a vanilla sword beside it, a hover screenshot in the inventory screen) and the break
# sounds (the mod sword, an iron sword, wolf armor), prints "CB-S5C ok|FAIL <check> <detail>" and "CB-S5C N/T",
# then stops the client. Run it climbed one step: boot_client_remote.sh with CB_FROM=1.21.4, T=1.21.5.
#   bash core/testmods/cb-s5c/build.sh <out.jar>   (from the repository root on beta2; CB_DATA=<data dir>)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
RT="$DATA/runtime/client-1.21.4"
CP="$DATA/jars/client-1.21.4-intermediary.jar"
for j in $(find "$RT/libraries" -name '*.jar'); do CP="$CP:$j"; done
javac --release 21 -nowarn -proc:none -cp "$CP" -d "$CLS" $(find core/testmods/cb-s5c/src -name '*.java')
cp -r core/testmods/cb-s5c/res/. "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
