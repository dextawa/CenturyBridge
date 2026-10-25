#!/usr/bin/env bash
# cb-smx1n: a native 1.20.2 mod (intermediary names, no refmap, no fabric-api, never converted) whose mixins sit on the
# 1.20.2 methods vanilla calls where the 1.20.2 layer consults restored 1.20.1 members: ShapedRecipe.Serializer.codec()
# (its recipe of group "cbsmx1-native", data/cb_smx1n/recipes/native_probe.json, makes 9) and MobEffect
# add/removeAttributeModifiers(attributes[, amplifier]) (Speed adds and removes a modifier "cbsmx1-native"). cb-smx1's
# Driver checks both still act with the layer's routes on. Boot it beside cb-smx1: CB_NATIVE_MODS for boot_climb.sh.
#
#   bash core/testmods/cb-smx1n/build.sh <out.jar>     (from the repository root; javac only;
#   CB_DATA=<data dir> when it is not ./data, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
MIXIN="${CB_MIXIN:-$DATA/runtime/client-1.20.3/libraries/net/fabricmc/sponge-mixin/0.17.3+mixin.0.8.7/sponge-mixin-0.17.3+mixin.0.8.7.jar}"
LIBS=$(find "$DATA"/runtime/client-1.20.2/libraries -name '*.jar' ! -name 'sponge-mixin-*' | tr '\n' "${CB_CPSEP:-:}")
javac --release 17 -nowarn -proc:none -cp "$DATA/jars/client-1.20.2-intermediary.jar${CB_CPSEP:-:}$MIXIN${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-smx1n/src -name '*.java')
cp -r core/testmods/cb-smx1n/res/. "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
