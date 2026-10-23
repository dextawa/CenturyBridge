"""Wiring for 1.21.1's Recipe members on 1.21.2 (s7item, step 1.21.1 -> 1.21.2; semantics in rt/CbItemRecipes).

1.21.2 dropped getResultItem(registries), getIngredients(), canCraftInDimensions(w, h), getRemainingItems(input),
getToastSymbol() and isIncomplete() from Recipe (and every vanilla recipe class's override of them) for
placementInfo() / display() / recipeBookCategory().

  instanceRedirects  a call on Recipe or on the vanilla class that declared the member in 1.21.1 -> CbItemRecipes:
                     the class's own 1.21.1 override, else 1.21.1's body for its vanilla class
  superRedirects     a subclass's super call -> 1.21.1's body of that vanilla class (CbItemRecipes.super_*)
  adapterHooks       Legacy*: a converted class declaring one of the members gets the interface its override implements,
                     so the statics reach it; V1 (no required shape): every converted class under Recipe, the mark of a
                     1.21.1 class (Recipe's 1.21.1 defaults apply to it, not a 1.21.2 class's placement)
  abstractFills      placementInfo() / recipeBookCategory() (abstract on Recipe since 1.21.2) and AbstractCookingRecipe's
                     furnaceIcon(), for a converted class that inherits them only as abstract

  python gen_s7item_recipes.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemRecipes"
RECIPE = "net/minecraft/class_1860"
P = "Lnet/minecraft/class_7225$class_7874;"
STACK = "Lnet/minecraft/class_1799;"

# member -> (Legacy adapter, 1.21.1 name, abstract on 1.21.1's Recipe, vanilla owners that declared it in 1.21.1 and
# survive in 1.21.2)
MEMBERS = {
    "method_8110(" + P + ")" + STACK: ("LegacyResult", "getResultItem(registries)", True,
        ["class_1851", "class_1852", "class_1853", "class_1867", "class_1869", "class_1874", "class_3972",
         "class_8060", "class_8062"]),
    "method_8113(II)Z": ("LegacyDims", "canCraftInDimensions(width, height)", True,
        ["class_1848", "class_1849", "class_1850", "class_1851", "class_1853", "class_1854", "class_1855",
         "class_1867", "class_1869", "class_1872", "class_1874", "class_1876", "class_3972", "class_4317",
         "class_8059", "class_8164"]),
    "method_8117()Lnet/minecraft/class_2371;": ("LegacyIngredients", "getIngredients()", False,
        ["class_1867", "class_1869", "class_1874", "class_3972"]),
    "method_8111(Lnet/minecraft/class_9695;)Lnet/minecraft/class_2371;": ("LegacyRemaining",
        "getRemainingItems(input)", False, []),
    "method_17447()" + STACK: ("LegacyToast", "getToastSymbol()", False,
        ["class_3859", "class_3861", "class_3862", "class_3920", "class_3975", "class_8059"]),
    "method_31584()Z": ("LegacyIncomplete", "isIncomplete()", False, ["class_1869", "class_8060", "class_8062"]),
}


def main():
    out = {"instanceRedirects": {}, "superRedirects": {}, "adapterHooks": [], "abstractFills": []}
    out["adapterHooks"].append({
        "under": RECIPE, "adapter": RT + "$V1", "requires": [], "synth": [],
        "basis": "every converted class under Recipe is a 1.21.1 recipe class: Recipe's 1.21.1 defaults answer for "
                 "it where it has no override (CbItemRecipes)"})
    for member, (legacy, why, abstract, owners) in MEMBERS.items():
        name = member[:member.index("(")]
        out["instanceRedirects"][RECIPE + "." + member] = RT
        if not abstract:
            out["superRedirects"][RECIPE + "." + member] = RT + "#super_" + name
        for o in owners:
            key = "net/minecraft/" + o + "." + member
            out["instanceRedirects"][key] = RT
            out["superRedirects"][key] = RT + "#super_" + name
        out["adapterHooks"].append({
            "under": RECIPE, "adapter": RT + "$" + legacy, "requires": [member], "synth": [],
            "basis": "a 1.21.1 recipe class overriding " + why + ": calls of it reach the override (CbItemRecipes)"})
    out["abstractFills"] += [
        {"under": RECIPE, "method": "method_61671()Lnet/minecraft/class_9887;",
         "call": RT + ".placementInfo(Lnet/minecraft/class_1860;)Lnet/minecraft/class_9887;",
         "basis": "the placement of the class's 1.21.1 ingredients (getIngredients(); none: not placeable)"},
        {"under": RECIPE, "method": "method_64668()Lnet/minecraft/class_10355;",
         "call": RT + ".recipeBookCategory(Lnet/minecraft/class_1860;)Lnet/minecraft/class_10355;",
         "basis": "the recipe book category 1.21.1's client gave the recipe's type (ClientRecipeBook.getCategory)"},
        {"under": "net/minecraft/class_1874", "method": "method_64663()Lnet/minecraft/class_1792;",
         "call": RT + ".furnaceIcon(Lnet/minecraft/class_1874;)Lnet/minecraft/class_1792;",
         "basis": "the item of the class's 1.21.1 toast symbol"},
    ]
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
