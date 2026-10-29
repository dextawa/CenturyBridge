#!/usr/bin/env bash
# cb-s212: a 1.21.1-era mod (intermediary names, no refmap, no fabric-api) whose mixins name 1.21.1 members that step
# 1.21.1 -> 1.21.2 reshaped (s212-mixintargets-gen.json, s212.json):
#   AbstractFurnaceBlockEntity.getTotalCookTime(Level, be) -> (ServerLevel, be)   RETURN, captures both, sets the answer
#   AbstractFurnaceBlockEntity.canBurn(RegistryAccess, RecipeHolder, NonNullList, int) -> (.., SingleRecipeInput, ..)  HEAD, captures all
#   DimensionDataStorage.getDataFile(String) File -> Path                          @Invoker (callReshapes ret conv)
#   BlockBehaviour.Properties.drops, a key -> the DependantName field_54007       @Accessor, and getfield/putfield (access widener)
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
# the mod's access widener opens Properties.drops (field_10666): compiled against the stub it widens, as loom would
# (make_aw_stub.sh; the engine must be compiled in core/build/classes)
STUB="$CLS.stub.jar"
CB_DATA="$DATA" bash core/tools/make_aw_stub.sh 1.21.1 "$DATA/jars/client-1.21.1-intermediary.jar" "$STUB" \
    core/testmods/cb-s212/res/cb_s212.accesswidener > /dev/null
javac --release 21 -nowarn -proc:none -cp "$STUB${CB_CPSEP:-:}$MIXIN${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-s212/src -name '*.java')
rm -f "$STUB"
cp core/testmods/cb-s212/res/* "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
