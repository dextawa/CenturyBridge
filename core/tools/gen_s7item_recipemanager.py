"""Wiring for 1.21.1's RecipeManager members on 1.21.2 (s7item, step 1.21.1 -> 1.21.2; semantics in rt/CbItemRecipeManager).

  instanceRedirects  hasErrors / getRecipeFor(.., lastId) / getAllRecipesFor / getRecipesFor / getRemainingItemsFor /
                     byKey(id) / getOrderedRecipes / getRecipeIds / replaceRecipes / apply(Map, ..) on 1.21.2's
                     RecipeManager (server and the layer's client one alike); CachedCheck.getRecipeFor(input, Level)
  staticRedirects    fromJson(id, json, registries); createCheck(type) -> 1.21.1's quick check
  superRedirects     a subclass's super.<member>(..): 1.21.1's body
  adapterHooks       a subclass's own override of a member: calls of it reach the override
  lambdaAdapters     a 1.21.1 CachedCheck lambda (input, Level) as 1.21.2's (input, ServerLevel)
  overrideHooks      a subclass's apply(Map<id, json>, ..) answers 1.21.2's apply(RecipeMap, ..) (the recipes encoded back
                     to JSON)

  python gen_s7item_recipemanager.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemRecipeManager"
RM = "net/minecraft/class_1863"
T = "Lnet/minecraft/class_3956;"
I = "Lnet/minecraft/class_9695;"
L = "Lnet/minecraft/class_1937;"
APPLY_OLD = "method_20705(Ljava/util/Map;Lnet/minecraft/class_3300;Lnet/minecraft/class_3695;)V"
APPLY_NEW = "method_20705(Lnet/minecraft/class_10289;Lnet/minecraft/class_3300;Lnet/minecraft/class_3695;)V"

LEGACY = ["LegacyHasErrors", "LegacyRecipeForLast", "LegacyAllRecipesFor", "LegacyRecipesFor", "LegacyRemainingFor",
          "LegacyByKey", "LegacyOrdered", "LegacyIds", "LegacyReplace"]
MEMBERS = [
    "method_35227()Z",
    "method_42299(" + T + I + L + "Lnet/minecraft/class_2960;)Ljava/util/Optional;",
    "method_30027(" + T + ")Ljava/util/List;",
    "method_17877(" + T + I + L + ")Ljava/util/List;",
    "method_8128(" + T + I + L + ")Lnet/minecraft/class_2371;",
    "method_8130(Lnet/minecraft/class_2960;)Ljava/util/Optional;",
    "method_59822()Ljava/util/Collection;",
    "method_8127()Ljava/util/stream/Stream;",
    "method_20702(Ljava/lang/Iterable;)V",
]


def main():
    out = {"instanceRedirects": {}, "superRedirects": {}, "staticRedirects": {}, "overrideHooks": []}
    out["adapterHooks"] = []
    for m, legacy in zip(MEMBERS, LEGACY):
        out["instanceRedirects"][RM + "." + m] = RT
        out["superRedirects"][RM + "." + m] = RT + "#super_" + m[:m.index("(")]
        out["adapterHooks"].append({"under": RM, "adapter": RT + "$" + legacy, "requires": [m], "synth": [],
                                    "basis": "a 1.21.1 RecipeManager subclass overriding " + m[:m.index("(")]
                                             + ": calls of it reach the override (CbItemRecipeManager)"})
    out["lambdaAdapters"] = {
        RM + "$class_7266.method_42303(" + I + L + ")Ljava/util/Optional;": {
            "iface": RT + "$LegacyCachedCheck",
            "adapt": RT + "#fromLambda(L" + RT + "$LegacyCachedCheck;)Lnet/minecraft/class_1863$class_7266;",
            "basis": "a 1.21.1 CachedCheck lambda answers getRecipeFor(input, Level); 1.21.2 asks with a ServerLevel"}}
    out["instanceRedirects"][RM + "." + APPLY_OLD] = RT
    out["superRedirects"][RM + "." + APPLY_OLD] = RT + "#super_method_20705"
    out["staticRedirects"][RM + ".method_17720(Lnet/minecraft/class_2960;Lcom/google/gson/JsonObject;"
                           "Lnet/minecraft/class_7225$class_7874;)Lnet/minecraft/class_8786;"] = RT
    out["staticRedirects"][RM + ".method_42302(" + T + ")Lnet/minecraft/class_1863$class_7266;"] = RT
    out["instanceRedirects"][RM + "$class_7266.method_42303(" + I + L + ")Ljava/util/Optional;"] = RT
    out["overrideHooks"].append({
        "old": APPLY_OLD, "new": APPLY_NEW, "onlyUnder": RM,
        "adapter": RT + "$LegacyApply",
        "bridge": RT + ".apply(L" + RT + "$LegacyApply;Lnet/minecraft/class_10289;Lnet/minecraft/class_3300;"
                  "Lnet/minecraft/class_3695;)V",
        "guard": "top/dext/centurybridge/step/v1_21_2/rt/Guard",
        "basis": "a 1.21.1 RecipeManager subclass's apply(Map<id, json>, ..): 1.21.2 applies a parsed RecipeMap; the "
                 "override gets those recipes as JSON (encoded back with the recipe codec)"})
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
