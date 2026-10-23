"""Wiring for 1.21.1's recipe classes on 1.21.2 (s7item, step 1.21.1 -> 1.21.2; semantics in rt/CbItemRecipeClasses).

  methodRenames /   the typed members 1.21.2 moved: AbstractCookingRecipe matches/assemble and StonecutterRecipe
  overrideRenames   matches are SingleItemRecipe's; BookCloningRecipe's getRemainingItems(CraftingInput) is
                    CraftingRecipe's; SmithingTransform/TrimRecipe matches are SmithingRecipe's (1.21.1's bodies and
                    1.21.2's agree, compared on the bytecode); a subclass's override takes the new name, its super calls too
  superCtorReshapes ShapelessRecipe (NonNullList -> List), ShapedRecipePattern (empty ingredient -> empty Optional),
                    SmithingTransform/TrimRecipe (ingredients -> Optionals), AbstractCookingRecipe / SingleItemRecipe
                    (type / serializer dropped, kept on the instance for getType() / getSerializer())
  abstractFills     getType()/getSerializer() of those, SmithingRecipe's Optional ingredients from 1.21.1's predicates
  smithing members  isTemplate/Base/AdditionIngredient(stack): the override (adapterHooks), else the ingredient's test
  fields            the protected fields of AbstractCookingRecipe / SingleItemRecipe; CookingBookCategory.CODEC
  RecipeHolder      constructed from an id (-> the recipe registry key), id() the key's location

  python gen_s7item_recipeclasses.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemRecipeClasses"
N = "net/minecraft/"
ING = "Lnet/minecraft/class_1856;"
STACK = "Lnet/minecraft/class_1799;"
OPT = "Ljava/util/Optional;"

# (owner, old name, desc, new name, what)
RENAMES = [
    ("class_1874", "method_59981", "(Lnet/minecraft/class_9696;Lnet/minecraft/class_1937;)Z", "method_64719",
     "AbstractCookingRecipe.matches(input, level) is SingleItemRecipe's since 1.21.2 (same body: the ingredient tests the item)"),
    ("class_1874", "method_59982", "(Lnet/minecraft/class_9696;Lnet/minecraft/class_7225$class_7874;)" + STACK, "method_59998",
     "AbstractCookingRecipe.assemble(input, registries) is SingleItemRecipe's since 1.21.2 (same body: a copy of the result)"),
    ("class_3975", "method_60003", "(Lnet/minecraft/class_9696;Lnet/minecraft/class_1937;)Z", "method_64719",
     "StonecutterRecipe.matches(input, level) is SingleItemRecipe's since 1.21.2 (same body)"),
    ("class_1850", "method_17707", "(Lnet/minecraft/class_9694;)Lnet/minecraft/class_2371;", "method_17704",
     "BookCloningRecipe.getRemainingItems(input) is CraftingRecipe.getRemainingItems's override since 1.21.2 (same body)"),
    ("class_8060", "method_59999", "(Lnet/minecraft/class_9697;Lnet/minecraft/class_1937;)Z", "method_61702",
     "SmithingTransformRecipe.matches(input, level) is SmithingRecipe's default since 1.21.2 (same tests)"),
    ("class_8062", "method_60001", "(Lnet/minecraft/class_9697;Lnet/minecraft/class_1937;)Z", "method_61702",
     "SmithingTrimRecipe.matches(input, level) is SmithingRecipe's default since 1.21.2 (same tests)"),
]

SMITHING = [("method_48453", "LegacyTemplate", "isTemplateIngredient", "method_64722", "templateIngredient"),
            ("method_48454", "LegacyBase", "isBaseIngredient", "method_64723", "baseIngredient"),
            ("method_30029", "LegacyAddition", "isAdditionIngredient", "method_64724", "additionIngredient")]


def conv(name, desc):
    return "conv " + RT + "#" + name + desc


def main():
    out = {"methodRenames": {}, "overrideRenames": [], "superCtorReshapes": {}, "abstractFills": [],
           "instanceRedirects": {}, "superRedirects": {}, "adapterHooks": [], "instanceFieldGetters": {},
           "fieldGetters": {}}
    for owner, old, desc, new, why in RENAMES:
        out["methodRenames"][N + owner + "." + old + desc] = new
        out["overrideRenames"].append({"old": old + desc, "new": new, "onlyUnder": N + owner, "superCalls": "rename",
                                       "basis": why})

    rs = out["superCtorReshapes"]
    rs[N + "class_1867.<init>(Ljava/lang/String;Lnet/minecraft/class_7710;" + STACK + "Lnet/minecraft/class_2371;)V"] = {
        "desc": "(Ljava/lang/String;Lnet/minecraft/class_7710;" + STACK + "Ljava/util/List;)V",
        "ops": ["keep", "keep", "keep", conv("list", "(Lnet/minecraft/class_2371;)Ljava/util/List;")],
        "basis": "1.21.2's ShapelessRecipe takes the ingredients as a List"}
    rs[N + "class_8957.<init>(IILnet/minecraft/class_2371;" + OPT + ")V"] = {
        "desc": "(IILjava/util/List;" + OPT + ")V",
        "ops": ["keep", "keep", conv("optionals", "(Lnet/minecraft/class_2371;)Ljava/util/List;"), "keep"],
        "basis": "1.21.2's pattern holds Optional ingredients, empty where 1.21.1 put the empty ingredient"}
    to_opt = conv("optional", "(" + ING + ")" + OPT)
    rs[N + "class_8060.<init>(" + ING * 3 + STACK + ")V"] = {
        "desc": "(" + OPT * 3 + STACK + ")V", "ops": [to_opt, to_opt, to_opt, "keep"],
        "basis": "1.21.2's smithing recipes take Optional ingredients (1.21.1's empty ingredient: empty)"}
    rs[N + "class_8062.<init>(" + ING * 3 + ")V"] = {
        "desc": "(" + OPT * 3 + ")V", "ops": [to_opt, to_opt, to_opt],
        "basis": "1.21.2's smithing recipes take Optional ingredients (1.21.1's empty ingredient: empty)"}
    cook_old = "(Lnet/minecraft/class_3956;Ljava/lang/String;Lnet/minecraft/class_7709;" + ING + STACK + "FI)V"
    rs[N + "class_1874.<init>" + cook_old] = {
        "desc": "(Ljava/lang/String;Lnet/minecraft/class_7709;" + ING + STACK + "FI)V",
        "ops": ["drop", "keep", "keep", "keep", "keep", "keep", "keep"],
        "post": RT + "#cookingCreated(Lnet/minecraft/class_1874;Lnet/minecraft/class_3956;)V", "postArgs": [0],
        "basis": "1.21.2's AbstractCookingRecipe takes no type (the class answers getType()); 1.21.1 answered the "
                 "type passed here, kept on the instance"}
    single_old = "(Lnet/minecraft/class_3956;Lnet/minecraft/class_1865;Ljava/lang/String;" + ING + STACK + ")V"
    rs[N + "class_3972.<init>" + single_old] = {
        "desc": "(Ljava/lang/String;" + ING + STACK + ")V",
        "ops": ["drop", "drop", "keep", "keep", "keep"],
        "post": RT + "#singleCreated(Lnet/minecraft/class_3972;Lnet/minecraft/class_3956;Lnet/minecraft/class_1865;)V",
        "postArgs": [0, 1],
        "basis": "1.21.2's SingleItemRecipe takes no type or serializer (the class answers them); 1.21.1 answered the "
                 "ones passed here, kept on the instance"}

    out["abstractFills"] += [
        {"under": N + "class_1874", "method": "method_17716()Lnet/minecraft/class_3956;",
         "call": RT + ".cookingType(Lnet/minecraft/class_1874;)Lnet/minecraft/class_3956;",
         "basis": "1.21.1's AbstractCookingRecipe.getType(): the type its constructor was given"},
        {"under": N + "class_3972", "method": "method_17716()Lnet/minecraft/class_3956;",
         "call": RT + ".singleType(Lnet/minecraft/class_3972;)Lnet/minecraft/class_3956;",
         "basis": "1.21.1's SingleItemRecipe.getType(): the type its constructor was given"},
        {"under": N + "class_3972", "method": "method_8119()Lnet/minecraft/class_1865;",
         "call": RT + ".singleSerializer(Lnet/minecraft/class_3972;)Lnet/minecraft/class_1865;",
         "basis": "1.21.1's SingleItemRecipe.getSerializer(): the serializer its constructor was given"},
    ]
    for old, legacy, why, new, newwhy in SMITHING:
        member = old + "(" + STACK + ")Z"
        for owner in ("class_8059", "class_8060", "class_8062"):
            out["instanceRedirects"][N + owner + "." + member] = RT
        for owner in ("class_8060", "class_8062"):
            out["superRedirects"][N + owner + "." + member] = RT + "#super_" + old
        out["adapterHooks"].append({"under": N + "class_8059", "adapter": RT + "$" + legacy, "requires": [member],
                                    "synth": [], "basis": "a 1.21.1 smithing recipe class overriding " + why
                                                          + "(stack): calls of it reach the override"})
        out["abstractFills"].append({
            "under": N + "class_8059", "method": new + "()" + OPT,
            "call": RT + "." + newwhy + "(Lnet/minecraft/class_8059;)" + OPT,
            "basis": "a 1.21.1 SmithingRecipe implementing " + why + "(stack): the items it accepts"})

    for f, desc in [("field_17544", "Lnet/minecraft/class_3956;"), ("field_9062", "Ljava/lang/String;"),
                    ("field_40241", "Lnet/minecraft/class_7709;"), ("field_9061", ING), ("field_9059", STACK),
                    ("field_9057", "F"), ("field_9058", "I")]:
        out["instanceFieldGetters"][N + "class_1874." + f + ":" + desc] = RT
    for f, desc in [("field_17642", ING), ("field_17643", STACK), ("field_17645", "Ljava/lang/String;")]:
        out["instanceFieldGetters"][N + "class_3972." + f + ":" + desc] = RT
    out["fieldGetters"][N + "class_7709.field_40245:Lnet/minecraft/class_3542$class_7292;"] = RT

    out["instanceRedirects"][N + "class_8957.method_59997()Lnet/minecraft/class_2371;"] = RT
    # CraftingInput.stackedContents(): 1.21.1's StackedContents (s7entity's facade, classRenames of class_1662)
    out["instanceRedirects"][N + "class_9694.method_59988()Lnet/minecraft/class_1662;"] = RT
    out["instanceRedirects"][N + "class_8786.comp_1932()Lnet/minecraft/class_2960;"] = RT
    rs[N + "class_8786.<init>(Lnet/minecraft/class_2960;Lnet/minecraft/class_1860;)V"] = {
        "desc": "(Lnet/minecraft/class_5321;Lnet/minecraft/class_1860;)V",
        "ops": [conv("key", "(Lnet/minecraft/class_2960;)Lnet/minecraft/class_5321;"), "keep"],
        "basis": "1.21.2's RecipeHolder is keyed by the recipe registry key of the id"}
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
