#!/usr/bin/env bash
# cb-clienttest: a 1.20.1-era client mod (intermediary names, no refmap, no Fabric API) that runs client
# checks once a world is joined -- fonts, render type getters, a GuiGraphics/Font/RenderType drawing
# screen, a mod container screen (renderBg/renderLabels), a vanilla inventory screen subclass
# (renderBg + super), the vanilla inventory screen under a mod's AbstractContainerScreen.render
# HEAD/RETURN injectors, the world view -- screenshots each phase through the 1.20.1 Screenshot API, and
# prints "CB-CLIENTTEST ok|FAIL <check> <detail>", "CB-CLIENTTEST summary ok=N fail=M", "CB-CLIENTTEST N/T";
# then it stops the client. Run it with core/tools/boot_client_remote.sh (CB_WORLD, CB_READY='CB-CLIENTTEST [0-9]+/').
#
#   bash core/testmods/cb-clienttest/build.sh <out.jar>     (from the repository root; javac only;
#   CB_DATA=<data dir> when it is not ./data, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
LIBS=$(find "$DATA"/runtime/client-1.20.1/libraries -name '*.jar' | tr '\n' "${CB_CPSEP:-:}")
javac --release 17 -nowarn -proc:none -cp "$DATA/jars/client-1.20.1-intermediary.jar${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-clienttest/src -name '*.java')
cp core/testmods/cb-clienttest/res/* "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
