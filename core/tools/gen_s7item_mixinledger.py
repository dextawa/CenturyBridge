"""mixinLedger of the s7item area, step 1.21.1 -> 1.21.2: members the step's wiring rebuilds for 1.21.1 mods' calls
(redirects into the layer) whose code 1.21.2 runs nowhere -- the rows gen_mixin_ledger --strict leaves to the area
agent ("whether the layer's code gives a hook back"). Each family below was judged from both bodies (javap of the
1.21.1 and 1.21.2 code jars): 1.21.2 dropped the member, no vanilla 1.21.2 code calls anything holding its code in
the same class, and the layer's answer runs only for 1.21.1 callers, so an injector there has no vanilla call to
hook. Members whose code moved to another method of the same class are mixinTargets instead
(gen_s7item_mixintargets.py); those whose code moved to another class are named so here.

  python3 core/tools/gen_s7item_mixinledger.py <cause-mixin.tsv> core/segments/steps/1.21.1__1.21.2.json.d/s7item-mixinledger-gen.json

Only rows mixin-cause filed as "target-wired: ... no W method holds its code" / "disabled, target gone" are keyed,
member by member, and only for the members RULES names. Regenerate, never hand-edit.
"""
import csv
import json
import sys

RECIPE_FIT = ("1.21.2 removed Recipe.canCraftInDimensions: the recipe book places by the recipe's PlacementInfo and a "
              "crafting recipe's display; no vanilla code asks a recipe whether it fits a grid (the layer answers only "
              "1.21.1 callers)")
RECIPE_RESULT = ("1.21.2 removed Recipe.getResultItem(registries): result previews are the recipe's RecipeDisplay slot "
                 "displays; no vanilla code asks the result item (the layer answers only 1.21.1 callers)")
RECIPE_INCOMPLETE = ("1.21.2 removed Recipe.isIncomplete: the recipe book tests placementInfo().isImpossibleToPlace(), a "
                     "PlacementInfo (another class) built once per recipe; no vanilla code asks the recipe")
ARMOR_GETTER = ("1.21.2 cut ArmorItem down to a constructor that applies the material to the Properties; defense, "
                "toughness, slot, material, repair and enchantability are the stack's ATTRIBUTE_MODIFIERS / EQUIPPABLE / "
                "REPAIRABLE / ENCHANTABLE components, read by vanilla from the components -- no vanilla call reaches "
                "the layer's 1.21.1 answer")
ITEM_COMPONENT = ("1.21.2 removed the Item method: repair, enchantability and the description id are the item's "
                  "REPAIRABLE / ENCHANTABLE components and the id its Properties gave it at construction, read by "
                  "vanilla from there -- no vanilla call reaches the layer's 1.21.1 answer")
DESCRIPTION_STACK = ("1.21.2 removed Item.getDescriptionId(stack): ShieldItem / PotionItem fold the stack-dependent key "
                     "into getName(stack), which answers a Component (a translatable of that key), and a tipped arrow is "
                     "named from its PotionContents (another class) -- a handler's CallbackInfoReturnable<String> has no "
                     "1.21.2 method answering a String to stand in; the layer answers only 1.21.1 callers")
FOOD_BUILDER = ("1.21.2 moved food effects, the eaten-item remainder and the eating time out of FoodProperties into the "
                "item's CONSUMABLE / USE_REMAINDER components (Consumable, another class); FoodProperties.Builder kept "
                "no method holding this code")
MENU_ACCESS = ("1.21.2's placement asks ServerPlaceRecipe.CraftingMenuAccess to clear the grid and test the recipe: an "
               "anonymous class of the menu (AbstractCraftingMenu$1 / AbstractFurnaceMenu$1), another class; the menu "
               "itself keeps no method holding this code")
TOOL_ATTRIBUTES = ("1.21.2 moved the tool/sword attribute modifiers into ToolMaterial.applyToolProperties / "
                   "applySwordProperties (class_9886), another class building Item.Properties")

# (owner's Mojang name, member name) -> reason; the member's descriptor comes from the rows
P = "net/minecraft/world/item/"
C = P + "crafting/"
RULES = {}
for o in ["ArmorDyeRecipe", "BannerDuplicateRecipe", "DecoratedPotRecipe", "FireworkRocketRecipe", "FireworkStarFadeRecipe",
          "FireworkStarRecipe", "MapCloningRecipe", "RepairItemRecipe", "ShapedRecipe", "ShieldDecorationRecipe",
          "TippedArrowRecipe"]:
    RULES[(C + o, "method_8113")] = RECIPE_FIT
for o in ["FireworkRocketRecipe", "FireworkStarRecipe", "SingleItemRecipe", "SmithingTransformRecipe", "SmithingTrimRecipe"]:
    RULES[(C + o, "method_8110")] = RECIPE_RESULT
for o in ["ShapedRecipe", "SmithingTransformRecipe", "SmithingTrimRecipe"]:
    RULES[(C + o, "method_31584")] = RECIPE_INCOMPLETE
for m in ["method_7687", "method_26353", "method_31570", "method_7685", "method_7844", "method_48398", "method_7686",
          "method_7878", "method_7837"]:
    RULES[(P + "ArmorItem", m)] = ARMOR_GETTER
RULES[(P + "ArmorItem", "<init>")] = (
    "1.21.2's ArmorItem constructor only applies the material to the Properties (ArmorMaterial.humanoidProperties) "
    "before Item's; 1.21.1's body (material, type, defense and toughness fields, the attribute modifier supplier) "
    "has no 1.21.2 counterpart")
RULES[(P + "ArmorItem", "method_7684")] = (
    "1.21.2 moved ArmorItem.dispenseArmor to EquipmentDispenseItemBehavior.dispenseEquipment (class_10205), another "
    "class; a mixin targets ArmorItem")
for m in ["method_7870", "method_18454", "method_55756", "method_57335"]:
    RULES[(P + "AnimalArmorItem", m)] = ARMOR_GETTER.replace("ArmorItem", "AnimalArmorItem")
RULES[(P + "AnimalArmorItem", "<init>")] = (
    "1.21.2's AnimalArmorItem constructors take a 1.21.2 ArmorMaterial and apply it to the Properties; 1.21.1's body "
    "(material holder, texture locations, body type fields) has no 1.21.2 counterpart")
for m in ["method_7870", "method_7869", "method_7878", "method_7837"]:
    RULES[(P + "Item", m)] = ITEM_COMPONENT
RULES[(P + "ProjectileWeaponItem", "method_7837")] = ITEM_COMPONENT
RULES[(P + "ShieldItem", "method_7878")] = ITEM_COMPONENT
for o in ["PotionItem", "ShieldItem", "TippedArrowItem"]:
    RULES[(P + o, "method_7866")] = DESCRIPTION_STACK
for m in ["method_19239", "method_60500", "method_19241"]:
    RULES[("net/minecraft/world/food/FoodProperties$Builder", m)] = FOOD_BUILDER
for o in ["AbstractFurnaceMenu", "CraftingMenu", "InventoryMenu"]:
    RULES[("net/minecraft/world/inventory/" + o, "method_7652")] = MENU_ACCESS
    RULES[("net/minecraft/world/inventory/" + o, "method_7657")] = MENU_ACCESS
RULES[(P + "SwordItem", "method_57394")] = TOOL_ATTRIBUTES
RULES[(P + "DiggerItem", "method_57346")] = TOOL_ATTRIBUTES
RULES[("net/minecraft/world/inventory/RecipeBookMenu", "method_17697")] = (
    "1.21.2's RecipeBookMenu.handlePlacement is abstract (each menu places itself: AbstractCraftingMenu, "
    "AbstractFurnaceMenu); RecipeBookMenu holds no placement code to hook")
RULES[("net/minecraft/world/LockCode", "method_5474")] = (
    "1.21.2's LockCode holds an ItemPredicate and addToTag(tag, registries) encodes it through its codec; 1.21.1's "
    "string key code is gone")
RULES[("net/minecraft/world/LockCode", "method_5473")] = (
    "1.21.2's LockCode holds an ItemPredicate and fromTag(tag, registries) decodes it through its codec; 1.21.1's "
    "string key code is gone")
RULES[(P + "alchemy/Potion", "method_8051")] = (
    "1.21.2 moved the potion name to PotionContents.getName / Potion's own name field (another class); Potion keeps no "
    "method holding getName(potion, prefix)")
RULES[(C + "Ingredient", "method_8100")] = (
    "1.21.2 removed Ingredient.getStackingIds: stacked-contents matching uses StackedItemContents over item holders "
    "(another class); Ingredient keeps no method holding it")


def main():
    src, out = sys.argv[1], sys.argv[2]
    led = {}
    with open(src, encoding="utf-8") as f:
        for r in csv.DictReader(f, delimiter="\t"):
            c = r["cause"]
            if not (c.startswith("target-wired") and ("no W method holds its code" in c or "disabled, target gone" in c)):
                continue
            name = r["target"][:r["target"].index("(")] if "(" in r["target"] else r["target"]
            why = RULES.get((r["mojang"], name))
            if why:
                led[r["owner"] + "." + r["target"]] = why
    json.dump({"_s7item_mixinLedger_basis": "gen_s7item_mixinledger.py (families judged from 1.21.1/1.21.2 bytecode); "
                                     "regenerate, never hand-edit", "mixinLedger": dict(sorted(led.items()))},
              open(out, "w", encoding="utf-8", newline="\n"), indent=1, ensure_ascii=False)
    print(len(led), "members ->", out)


if __name__ == "__main__":
    main()
