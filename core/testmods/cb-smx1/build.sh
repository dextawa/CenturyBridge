#!/usr/bin/env bash
# cb-smx1: a 1.20.1-era mod (intermediary names, no refmap, no fabric-api) whose mixins name 1.20.1 members that
# step 1 (1.20.1 -> 1.20.2) moves for mixins: mixinTargets (LootPool's constructor, BeaconBlockEntity
# getValidEffectById -> filterEffect, NbtIo.readUnnamedTag, AdvancementList.add -> AdvancementTree.addAll) and
# fieldRenames (LootItemRandomChanceCondition.probability -> the record component) and a
# @CbVanillaReads route (PathNavigation.isInLiquid, which 1.20.2's navigations ask again through the layer), and the recipe
# readers (ShapedRecipe.Serializer / SimpleCookingSerializer fromJson: a RETURN injector, a HEAD injector and @Redirects
# inside the bodies change the mod's own probe recipes data/cb_smx1/recipes/*; every other recipe is fingerprinted at
# startup and after /reload, to compare a boot with -Dcenturybridge.legacyJsonRoute=false), and StatType.getTranslationKey
# (a RETURN injector renames the "minecraft:killed" key; getDisplayName must show it, and every other stat type's name
# is printed to compare with -Dcenturybridge.legacyStatNameRoute=false), MobEffect add/removeAttributeModifiers (Strength
# gets a modifier of the mod's own; every other effect's attributes are fingerprinted against
# -Dcenturybridge.legacyEffectAttributeRoute=false), and cb-smx1n's native 1.20.2 mixins still acting (boot cb-smx1n beside it:
# CB_NATIVE_MODS). ServerDriverMixin runs Driver
# once the world is loaded: "CB-SMX1 ok|FAIL <case> <detail>", ending "CB-SMX1 summary ok=N fail=M".
#
#   bash core/testmods/cb-smx1/build.sh <out.jar>     (from the repository root; javac only;
#   CB_DATA=<data dir> when it is not ./data, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
MIXIN="${CB_MIXIN:-$DATA/runtime/client-1.20.3/libraries/net/fabricmc/sponge-mixin/0.17.3+mixin.0.8.7/sponge-mixin-0.17.3+mixin.0.8.7.jar}"
LIBS=$(find "$DATA"/runtime/client-1.20.1/libraries -name '*.jar' ! -name 'sponge-mixin-*' | tr '\n' "${CB_CPSEP:-:}")
javac --release 17 -nowarn -proc:none -cp "$DATA/jars/client-1.20.1-intermediary.jar${CB_CPSEP:-:}$MIXIN${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-smx1/src -name '*.java')
cp -r core/testmods/cb-smx1/res/. "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
