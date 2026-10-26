"""mixinTargets of step 1.21.1 -> 1.21.2 (s212): `Main gen-mixin-targets` over V/W bytecode (a V method W no longer
declares with V's descriptor, the one W method of that name, arguments one for one by type), minus EXCLUDE (judged from
both bodies with javap -c -p not to hold V's code where a V handler expects it, or left to another mechanism).

  On beta2 (Java there only), with this fragment absent (gen-mixin-targets counts its own entries as wired):
    zcat core/segments/damage-mixin/1.21.1__1.21.2.tsv.gz > dm.tsv
    java -cp core/build/classes:<asm+gson> top.dext.centurybridge.Main gen-mixin-targets \
        data/jars/client-1.21.1-intermediary-code.jar data/jars/client-1.21.2-intermediary-code.jar \
        core/segments/steps/1.21.1__1.21.2.json dm.tsv mt-gen.json
  Here, from the repository root:
    python3 core/tools/gen_s212_mixintargets.py mt-gen.json core/segments/steps/1.21.1__1.21.2.json.d/s212-mixintargets-gen.json

Regenerate, never hand-edit. Judged 2026-10-26 (77 candidates; the kept ones: W's method is V's under the same name,
called from V's callers, with V's values among its arguments -- mostly 1.21.2 passing the ServerLevel in).
"""
import json
import sys

RET = "the return type changed to an unrelated type: a V handler's CallbackInfoReturnable / answer would carry the wrong type"
# V method (or V class prefix ending in ".") -> why gen-mixin-targets' same-name pick is not taken (javap of both bodies)
EXCLUDE = {
    "net/minecraft/class_156.method_28122(Ljava/lang/String;)Ljava/util/concurrent/ExecutorService;":
        "Util.makeExecutor: 1.21.2 answers TracingExecutor (a record implementing Executor only, not ExecutorService); " + RET,
    "net/minecraft/class_156.method_27959(Ljava/lang/String;Z)Ljava/util/concurrent/ExecutorService;":
        "Util.makeIoExecutor: 1.21.2 answers TracingExecutor (Executor only); " + RET,
    "net/minecraft/class_1662$class_1663.method_7422()[I":
        "StackedContents.RecipePicker.getUniqueAvailableIngredientItems: int[] of item ids -> List of ingredient items; " + RET,
    "net/minecraft/class_1741.comp_2301()Ljava/util/function/Supplier;":
        "ArmorMaterial.repairIngredient: Supplier<Ingredient> -> TagKey<Item> (another value); " + RET,
    "net/minecraft/class_1749.method_42296(Lnet/minecraft/class_1937;Lnet/minecraft/class_239;Lnet/minecraft/class_1799;Lnet/minecraft/class_1657;)Lnet/minecraft/class_1690;":
        "BoatItem.getBoat: Boat -> AbstractBoat made through the item's EntityType (a raft or chest boat is no Boat); "
        "a V handler's CallbackInfoReturnable<Boat> would hand it another class",
    "net/minecraft/class_26.method_17922(Ljava/lang/String;)Ljava/io/File;":
        "DimensionDataStorage.getDataFile: File -> Path; " + RET + " (served by a callReshapes ret conv in s212.json)",
    "net/minecraft/class_2852.<init>()V":
        "ChunkSerializer's implicit constructor (a static-only class) vs SerializableChunkData's canonical record constructor: "
        "another class's construction under a reused name",
    "net/minecraft/class_3899.method_17606()Ljava/util/stream/Stream;":
        "ChunkTaskPriorityQueue.getAcquired / pop: Stream -> the 1.21.2 queue's own entry type; " + RET,
    "net/minecraft/class_374$class_375.method_2002(ILnet/minecraft/class_332;)Z":
        "ToastInstance.render(width, graphics) answered whether the toast finished; 1.21.2's render(graphics, width) is void "
        "(the visibility update moved to update()); " + RET,
    "net/minecraft/class_4390.method_21245(Lnet/minecraft/class_4877;)V":
        "RealmsCreateRealmScreen: 1.21.1's createWorld(server) and 1.21.2's method of that name share 1 of 10 referenced "
        "members (javap): the code is another flow",
    "net/minecraft/class_508.method_2615()Lnet/minecraft/class_8786;":
        "RecipeBookPage.getLastClickedRecipe: RecipeHolder -> RecipeDisplayId; " + RET,
    "net/minecraft/class_512.method_2623()Lnet/minecraft/class_314;":
        "RecipeBookTabButton.getCategory: RecipeBookCategories -> ExtendedRecipeBookCategory; " + RET,
    "net/minecraft/class_514.method_2640(Lnet/minecraft/class_516;Lnet/minecraft/class_513;)V":
        "RecipeButton.init: 1.21.2 rebuilt it over RecipeDisplayEntry and SlotSelectTime (2 of 21 referenced members kept)",
    "net/minecraft/class_514.method_2644()Ljava/util/List;":
        "RecipeButton.getOrderedRecipes: List<RecipeHolder> -> 1.21.2's getDisplayStacks(ItemStack) of item stacks, another value",
    "net/minecraft/class_7288.method_42746()V":
        "LivingEntityEmissiveLayer.onlyDrawSelectedParts: void reset of part visibility -> boolean over a render state",
}
# V classes the step's classRenames send to a layer facade (mixins on them are the facade's question, not a same-name W
# method of vanilla's class); wiring-check refuses a mixinTargets entry the renamed class does not declare
FACADE = "classRenames sends the V class to a layer facade (s7item / s7level recipe book and queue facades)"
for c in ("class_3847$class_3849", "class_507", "class_508", "class_513", "class_514", "class_517"):
    EXCLUDE["net/minecraft/" + c + "."] = FACADE


def excluded(k):
    if k in EXCLUDE:
        return True
    owner = k[:k.rindex(".", 0, k.index("("))]
    return owner + "." in EXCLUDE


def main():
    src, out = sys.argv[1], sys.argv[2]
    mt = json.load(open(src, encoding="utf-8"))["mixinTargets"]
    keep = {k: v for k, v in sorted(mt.items()) if not excluded(k)}
    root = {"_s212_mixinTargets_basis": "gen_s212_mixintargets.py over Main gen-mixin-targets (V/W bytecode); regenerate, "
                                        "never hand-edit", "mixinTargets": keep}
    json.dump(root, open(out, "w", encoding="utf-8", newline="\n"), indent=1)
    print(len(keep), "of", len(mt), "entries ->", out)


if __name__ == "__main__":
    main()
