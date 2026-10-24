"""Wiring for 1.21.1's recipe data generation API on 1.21.2 (s7item, step 1.21.1 -> 1.21.2; dev-time datagen;
semantics in rt/CbItemDatagen).

  save / accept      RecipeBuilder.save(output, id), the special / smithing builders' save, RecipeOutput.accept(id, ..):
                     calls (the id's key), overrides and implementations (handed the key's location)
  builders           ShapedRecipeBuilder / ShapelessRecipeBuilder factories and constructors: the item lookup they now
                     take, the shapeless result stack
  root advancement   RecipeOutput.includeRootAdvancement() for a 1.21.1 output (abstractFills)
  trim templates     VanillaRecipeProvider.TrimTemplate(item, id) / id()

  python gen_s7item_datagen.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemDatagen"
RL = "Lnet/minecraft/class_2960;"
KEY = "Lnet/minecraft/class_5321;"
OUT = "Lnet/minecraft/class_8790;"
ITEMS = "Lnet/minecraft/class_7871;"
CAT = "Lnet/minecraft/class_7800;"
LIKE = "Lnet/minecraft/class_1935;"
TO_KEY = "conv " + RT + "#key(" + RL + ")" + KEY
WRAP_ID = RT + ".id(" + KEY + ")" + RL
PUSH_ITEMS = "push " + RT + "#items()" + ITEMS

# (owner, save member name, whether it is RecipeBuilder's save)
SAVES = [
    ("net/minecraft/class_5797", "method_17972"),
    ("net/minecraft/class_2447", "method_17972"),
    ("net/minecraft/class_2450", "method_17972"),
    ("net/minecraft/class_2454", "method_17972"),
    ("net/minecraft/class_3981", "method_17972"),
    ("net/minecraft/class_8074", "method_48537"),
    ("net/minecraft/class_8076", "method_48542"),
    ("net/minecraft/class_2456", "method_10475"),
]


def main():
    out = {"callReshapes": {}, "superCtorReshapes": {}, "overrideHooks": [], "abstractFills": []}
    hooked = set()
    for owner, name in SAVES:
        out["callReshapes"][owner + "." + name + "(" + OUT + RL + ")V"] = {
            "desc": "(" + OUT + KEY + ")V", "ops": ["keep", TO_KEY],
            "basis": "1.21.2 saves a recipe under its key in the recipe registry: the id's key"}
        # one hook per old shape and hierarchy root: RecipeBuilder's save covers its implementations and the vanilla
        # builders' subclasses; the special / smithing builders are no RecipeBuilders
        root = "net/minecraft/class_5797" if name == "method_17972" else owner
        if (root, name) not in hooked:
            hooked.add((root, name))
            out["overrideHooks"].append({
                "old": name + "(" + OUT + RL + ")V", "new": name + "(" + OUT + KEY + ")V", "onlyUnder": root,
                "wrap": WRAP_ID,
                "basis": "a 1.21.1 builder's save(output, id) answers 1.21.2's save(output, key) with the key's location; "
                         "its super call takes the call's conversion (CbItemDatagen)"})
    accept = "method_53819"
    out["callReshapes"]["net/minecraft/class_8790." + accept + "(" + RL + "Lnet/minecraft/class_1860;Lnet/minecraft/class_8779;)V"] = {
        "desc": "(" + KEY + "Lnet/minecraft/class_1860;Lnet/minecraft/class_8779;)V", "ops": [TO_KEY, "keep", "keep"],
        "basis": "RecipeOutput.accept: the recipe's key"}
    out["overrideHooks"].append({
        "old": accept + "(" + RL + "Lnet/minecraft/class_1860;Lnet/minecraft/class_8779;)V",
        "new": accept + "(" + KEY + "Lnet/minecraft/class_1860;Lnet/minecraft/class_8779;)V",
        "onlyUnder": "net/minecraft/class_8790", "wrap": WRAP_ID,
        "basis": "a 1.21.1 RecipeOutput's accept(id, ..) answers 1.21.2's accept(key, ..) with the key's location"})
    out["abstractFills"].append({
        "under": "net/minecraft/class_8790", "method": "method_62738()V",
        "call": RT + ".includeRootAdvancement(" + OUT + ")V",
        "basis": "1.21.2's RecipeOutput.includeRootAdvancement(): a 1.21.1 output had no root advancement of its own "
                 "(1.21.1's RecipeProvider wrote it)"})
    shaped = "net/minecraft/class_2447"
    out["callReshapes"][shaped + ".method_10437(" + CAT + LIKE + ")L" + shaped + ";"] = {
        "desc": "(" + ITEMS + CAT + LIKE + ")L" + shaped + ";", "ops": [PUSH_ITEMS, "keep", "keep"],
        "basis": "ShapedRecipeBuilder.shaped(category, item): the item lookup (the built-in registry)"}
    out["callReshapes"][shaped + ".method_10436(" + CAT + LIKE + "I)L" + shaped + ";"] = {
        "desc": "(" + ITEMS + CAT + LIKE + "I)L" + shaped + ";", "ops": [PUSH_ITEMS, "keep", "keep", "keep"],
        "basis": "ShapedRecipeBuilder.shaped(category, item, count): the item lookup"}
    out["superCtorReshapes"][shaped + ".<init>(" + CAT + LIKE + "I)V"] = {
        "desc": "(" + ITEMS + CAT + LIKE + "I)V", "ops": [PUSH_ITEMS, "keep", "keep", "keep"],
        "basis": "ShapedRecipeBuilder(category, item, count): the item lookup (the constructor is opened by the layer's "
                 "access widener)"}
    shapeless = "net/minecraft/class_2450"
    stack1 = "conv " + RT + "#stack(" + LIKE + ")Lnet/minecraft/class_1799;"
    out["callReshapes"][shapeless + ".method_10447(" + CAT + LIKE + ")L" + shapeless + ";"] = {
        "desc": "(" + ITEMS + CAT + "Lnet/minecraft/class_1799;)L" + shapeless + ";", "ops": [PUSH_ITEMS, "keep", stack1],
        "basis": "ShapelessRecipeBuilder.shapeless(category, item): the item lookup and one of the item"}
    out["callReshapes"][shapeless + ".method_10448(" + CAT + LIKE + "I)L" + shapeless + ";"] = {
        "desc": "(" + ITEMS + CAT + LIKE + "I)L" + shapeless + ";", "ops": [PUSH_ITEMS, "keep", "keep", "keep"],
        "basis": "ShapelessRecipeBuilder.shapeless(category, item, count): the item lookup"}
    out["superCtorReshapes"][shapeless + ".<init>(" + CAT + LIKE + "I)V"] = {
        "desc": "(" + ITEMS + CAT + "Lnet/minecraft/class_1799;)V",
        "ops": [PUSH_ITEMS, "keep:0", "conv:1,2 " + RT + "#stack(" + LIKE + "I)Lnet/minecraft/class_1799;"],
        "basis": "ShapelessRecipeBuilder(category, item, count): the item lookup and the result stack"}
    trim = "net/minecraft/class_7803$class_8905"
    out["superCtorReshapes"][trim + ".<init>(Lnet/minecraft/class_1792;" + RL + ")V"] = {
        "desc": "(Lnet/minecraft/class_1792;" + KEY + ")V", "ops": ["keep", TO_KEY],
        "basis": "VanillaRecipeProvider.TrimTemplate(item, id): the recipe key"}
    out["callReshapes"][trim + ".comp_2013()" + RL] = {
        "desc": "()" + KEY, "ops": [], "ret": "conv " + RT + "#id(" + KEY + ")" + RL,
        "basis": "TrimTemplate.id(): the key's location"}
    # a 1.21.1 recipe provider's superclass: 1.21.1's data provider (the facade), its helpers answered by a 1.21.2
    # provider bound to its output (gen_s7item_datagen_recipes.py)
    out["superRenames"] = {"net/minecraft/class_2446": "top/dext/centurybridge/step/v1_21_2/facade/RecipeProvider"}
    adv = "Lnet/minecraft/class_8779;"
    push_types = "push " + RT + "#entityTypes()" + ITEMS
    out["callReshapes"]["net/minecraft/class_2412.method_55581(" + adv + "Ljava/util/function/Consumer;Ljava/util/List;)" + adv] = {
        "desc": "(" + adv + "Ljava/util/function/Consumer;" + ITEMS + "Ljava/util/List;)" + adv,
        "ops": ["keep", "keep", push_types, "keep"],
        "basis": "VanillaAdventureAdvancements helper: the entity type lookup 1.21.2 takes (the built-in registry)"}
    out["callReshapes"]["net/minecraft/class_2414.method_47810(" + adv + "Ljava/util/function/Consumer;Ljava/util/stream/Stream;"
                        "Ljava/util/stream/Stream;)" + adv] = {
        "desc": "(" + adv + "Ljava/util/function/Consumer;" + ITEMS + "Ljava/util/stream/Stream;Ljava/util/stream/Stream;)" + adv,
        "ops": ["keep", "keep", push_types, "keep", "keep"],
        "basis": "VanillaHusbandryAdvancements helper: the entity type lookup 1.21.2 takes (the built-in registry)"}
    out["callReshapes"]["net/minecraft/class_5478.method_30705(" + ITEMS + ITEMS + ")Lnet/minecraft/class_1959;"] = {
        "desc": "(" + ITEMS + ITEMS + "Z)Lnet/minecraft/class_1959;",
        "ops": ["keep", "keep", "push " + RT + "#notPaleGarden()Z"],
        "basis": "OverworldBiomes.darkForest: 1.21.2's takes isPaleGarden; 1.21.1's dark forest is its false branch"}
    out["fieldGetters"] = {
        "net/minecraft/class_6800.field_35767:Lnet/minecraft/class_5321;": RT,
        "net/minecraft/class_6811.field_35975:Lnet/minecraft/class_5321;": RT,
        "net/minecraft/class_8998.field_47509:Lnet/minecraft/class_5321;": RT,
    }
    dr = "top/dext/centurybridge/step/v1_21_2/rt/CbItemDatagenRecipes"
    rp = "net/minecraft/class_2446."
    co = "Ljava/util/concurrent/CompletableFuture;"
    out["instanceRedirects"] = {
        rp + "method_10319(Lnet/minecraft/class_7403;)" + co: dr + "#method_10319",
        rp + "method_10321()Ljava/lang/String;": dr + "#method_10321",
        rp + "method_56888(Lnet/minecraft/class_7403;Lnet/minecraft/class_7225$class_7874;)" + co: dr + "#method_56888",
        rp + "method_46206(Lnet/minecraft/class_7403;Lnet/minecraft/class_7225$class_7874;Lnet/minecraft/class_8779;)" + co:
            dr + "#method_46206",
    }
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
