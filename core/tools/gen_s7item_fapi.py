"""Wiring for s7item's Fabric API share on 1.21.1 -> 1.21.2 (Fabric API 0.116 -> 0.106; semantics in rt/CbItemFapi).

  recipe   CustomIngredient.getMatchingStacks() (-> getMatchingItems()), CustomIngredientSerializer.getCodec(allowEmpty)
           (-> getCodec()): calls, and 1.21.1 implementations answering 1.21.2's shapes (adapterHooks synth)
  item     FabricItem / FabricItemStack.getCreatorNamespace (gone in 0.106); FabricTooltipType (gone; facade)
  datagen  FabricRecipeProvider (superRenames: 0.116's on 0.106's Runner, facade); FabricRecipeExporter (gone; facade,
           injected into RecipeOutput by CbItemRecipeOutputMixin, honored by the builders' save, CbItemRecipeExporter*Mixin)
           FabricTagBuilder.remove / removeAll / removeTag (gone; CbItemDatagenTags writes "fabric:remove");
           FabricProvidedTagBuilder (gone; facade, implemented on TagAppender by CbItemTagAppenderMixin);
           SimpleFabricLootTableProvider.lootContextType (renamed contextType)
  biome    GenerationSettingsContext.addCarver / removeCarver(step, carver), BiomeModifications.addCarver(.., step, ..)

  python gen_s7item_fapi.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemFapi"
F = "net/fabricmc/fabric/api/"
CI = F + "recipe/v1/ingredient/CustomIngredient"
CIS = F + "recipe/v1/ingredient/CustomIngredientSerializer"
FI = F + "item/v1/FabricItem"
FIS = F + "item/v1/FabricItemStack"
GSC = F + "biome/v1/BiomeModificationContext$GenerationSettingsContext"
CARVING = "Lnet/minecraft/class_2893$class_2894;"
KEY = "Lnet/minecraft/class_5321;"
STACK = "Lnet/minecraft/class_1799;"


RT_TAGS = "top/dext/centurybridge/step/v1_21_2/rt/CbItemDatagenTags"
FAC = "top/dext/centurybridge/step/v1_21_2/facade/fapi/"


def main():
    out = {"instanceRedirects": {}, "staticRedirects": {}, "superRedirects": {}, "adapterHooks": [], "classRenames": {}}
    out["instanceRedirects"][CI + ".getMatchingStacks()Ljava/util/List;"] = RT + "#getMatchingStacks"
    out["adapterHooks"].append({
        "under": CI, "adapter": RT + "$LegacyIngredient", "requires": ["getMatchingStacks()Ljava/util/List;"],
        "synth": [{"new": "getMatchingItems()Ljava/util/List;",
                   "call": RT + ".matchingItems(L" + RT + "$LegacyIngredient;)Ljava/util/List;"}],
        "basis": "a 1.21.1 CustomIngredient answers 0.106's getMatchingItems() with its stacks' items (CbItemFapi)"})
    out["instanceRedirects"][CIS + ".getCodec(Z)Lcom/mojang/serialization/MapCodec;"] = RT + "#getCodec"
    out["adapterHooks"].append({
        "under": CIS, "adapter": RT + "$LegacySerializer", "requires": ["getCodec(Z)Lcom/mojang/serialization/MapCodec;"],
        "synth": [{"new": "getCodec()Lcom/mojang/serialization/MapCodec;",
                   "call": RT + ".codec(L" + RT + "$LegacySerializer;)Lcom/mojang/serialization/MapCodec;"}],
        "basis": "a 1.21.1 CustomIngredientSerializer answers 0.106's getCodec() with its non-empty codec (1.21.2's "
                 "ingredients are never empty)"})
    creator = "getCreatorNamespace(" + STACK + ")Ljava/lang/String;"
    out["instanceRedirects"][FI + "." + creator] = RT + "#getCreatorNamespace"
    out["superRedirects"][FI + "." + creator] = RT + "#superGetCreatorNamespace"
    out["adapterHooks"].append({
        "under": FI, "adapter": RT + "$LegacyCreator", "requires": [creator], "synth": [],
        "basis": "a 1.21.1 item's FabricItem.getCreatorNamespace override answers the calls (CbItemFapi)"})
    out["instanceRedirects"][FIS + ".getCreatorNamespace()Ljava/lang/String;"] = RT + "#getCreatorNamespace"
    out["superRedirects"][FIS + ".getCreatorNamespace()Ljava/lang/String;"] = RT + "#superGetCreatorNamespace"
    out["adapterHooks"].append({
        "under": FIS, "adapter": RT + "$LegacyStackCreator", "requires": ["getCreatorNamespace()Ljava/lang/String;"],
        "synth": [], "basis": "a class declaring FabricItemStack.getCreatorNamespace() answers the calls (CbItemFapi)"})
    out["classRenames"][F + "item/v1/FabricTooltipType"] = FAC + "FabricTooltipType"
    out["instanceRedirects"][F + "item/v1/FabricTooltipType.shouldDisplayAllInformation()Z"] = RT + "#shouldDisplayAllInformation"
    out["instanceRedirects"][GSC + ".addCarver(" + CARVING + KEY + ")V"] = RT + "#addCarver"
    out["instanceRedirects"][GSC + ".removeCarver(" + CARVING + KEY + ")Z"] = RT + "#removeCarver"
    key = "Lnet/minecraft/class_5321;"
    out["adapterHooks"].append({
        "under": GSC, "adapter": RT + "$LegacyCarvers",
        "requires": ["addCarver(" + CARVING + key + ")V", "removeCarver(" + CARVING + key + ")Z"],
        "synth": [{"new": "addCarver(" + key + ")V",
                   "call": RT + ".addCarverAir(L" + RT + "$LegacyCarvers;" + key + ")V"},
                  {"new": "removeCarver(" + key + ")Z",
                   "call": RT + ".removeCarverAll(L" + RT + "$LegacyCarvers;" + key + ")Z"}],
        "basis": "a 1.21.1 GenerationSettingsContext implementation answers 0.106's step-less addCarver (its air step, "
                 "1.21.2's one) and removeCarver (0.116's default: every step) (CbItemFapi)"})
    out["staticRedirects"][F + "biome/v1/BiomeModifications.addCarver(Ljava/util/function/Predicate;" + CARVING + KEY + ")V"] = \
        RT + "#addCarver"
    ftb = F + "datagen/v1/provider/FabricTagProvider$FabricTagBuilder"
    ret = ")L" + ftb + ";"
    for name, args in [("remove", "Ljava/lang/Object;"), ("remove", "[Ljava/lang/Object;"),
                       ("remove", "Lnet/minecraft/class_5321;"), ("remove", "[Lnet/minecraft/class_5321;"),
                       ("removeAll", "Ljava/util/Collection;"), ("removeAll", "Ljava/util/stream/Stream;"),
                       ("removeTag", "Lnet/minecraft/class_6862;")]:
        out["instanceRedirects"][ftb + "." + name + "(" + args + ret] = RT_TAGS + "#" + name
    out["classRenames"][F + "datagen/v1/provider/FabricProvidedTagBuilder"] = FAC + "FabricProvidedTagBuilder"
    out["fieldRenames"] = {F + "datagen/v1/provider/SimpleFabricLootTableProvider.lootContextType:Lnet/minecraft/class_176;":
                           "contextType"}
    frp = F + "datagen/v1/provider/FabricRecipeProvider"
    out["instanceRedirects"][frp + ".method_10419(Lnet/minecraft/class_8790;)V"] = FAC + "FabricRecipeProvider#buildRecipes"
    out["instanceRedirects"][frp + ".method_56888(Lnet/minecraft/class_7403;Lnet/minecraft/class_7225$class_7874;)"                              "Ljava/util/concurrent/CompletableFuture;"] = FAC + "FabricRecipeProvider#run"
    out["superRenames"] = {F + "datagen/v1/provider/FabricRecipeProvider": FAC + "FabricRecipeProvider"}
    out["classRenames"][F + "datagen/v1/recipe/FabricRecipeExporter"] = FAC + "FabricRecipeExporter"
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
