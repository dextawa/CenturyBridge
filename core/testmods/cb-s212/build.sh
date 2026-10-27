#!/usr/bin/env bash
# cb-s212: a 1.21.1-era mod (intermediary names, no refmap, no fabric-api) whose mixins name 1.21.1 members that step
# 1.21.1 -> 1.21.2 reshaped (s212-mixintargets-gen.json, s212.json):
#   AbstractFurnaceBlockEntity.getTotalCookTime(Level, be) -> (ServerLevel, be)   RETURN, captures both, sets the answer
#   AbstractFurnaceBlockEntity.canBurn(RegistryAccess, RecipeHolder, NonNullList, int) -> (.., SingleRecipeInput, ..)  HEAD, captures all
#   DimensionDataStorage.getDataFile(String) File -> Path                          @Invoker (callReshapes ret conv)
# ServerDriverMixin runs Driver once the world is loaded: "CB-S212 ok|FAIL <case> <detail>", ending
# "CB-S212 summary ok=N fail=M". native/ is cb-s212n, a 1.21.2 mod (not converted) mixing the same 1.21.2 methods: its
# mixins must fire next to the converted ones (and alone, native-only).
#
#   bash core/testmods/cb-s212/build.sh <out.jar>     (from the repository root; javac only;
#   CB_DATA=<data dir> when it is not ./data, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
MIXIN=$(find "$DATA"/runtime/client-1.21.1/libraries -name 'sponge-mixin-*.jar' | head -1)
LIBS=$(find "$DATA"/runtime/client-1.21.1/libraries -name '*.jar' ! -name 'sponge-mixin-*' | tr '\n' "${CB_CPSEP:-:}")
javac --release 21 -nowarn -proc:none -cp "$DATA/jars/client-1.21.1-intermediary.jar${CB_CPSEP:-:}$MIXIN${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-s212/src -name '*.java')
cp core/testmods/cb-s212/res/* "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
