"""Wiring for 1.21.1's RecipeBookMenu members on 1.21.2 (s7item, step 1.21.1 -> 1.21.2; semantics in rt/CbItemRecipeMenus).

  instanceRedirects  every call of a 1.21.1 RecipeBookMenu member (keyed on class_1729; calls through CraftingMenu,
                     InventoryMenu, AbstractFurnaceMenu and mod menus resolve there): a menu declaring the 1.21.1 method
                     answers itself, a vanilla menu with its 1.21.1 body
  superRedirects     a mod CraftingMenu / InventoryMenu / AbstractFurnaceMenu subclass's super.<member>(..): 1.21.1's body
  staticRedirects    CraftingMenu.slotChangedCraftingGrid(.., Level, ..)
  adapterHooks       a mod class under RecipeBookMenu declaring a 1.21.1 shape implements its Legacy_* interface
  overrideHooks      a mod vanilla-menu subclass's 1.21.1 override answers the 1.21.2 method vanilla calls instead:
                     handlePlacement, finishPlacingRecipe, fillCraftSlotsStackedContents, getGridWidth/Height
                     (AbstractCraftingMenu's); its super call goes through a super bridge where the 1.21.2 method
                     would land back in the mod's override
  superCtorReshapes  AbstractFurnaceMenu(.., recipeType, bookType, ..): the recipe property set 1.21.2 adds, by recipe type
  ItemCombinerMenu   createInputSlotDefinitions / getSlotToQuickMoveTo calls, supers and overrides; its old constructor
                     (CbItemCombiner, CbItemCombinerMenuMixin)
  classRenames       1.21.1 PlaceRecipe / ServerPlaceRecipe -> the layer's facades (1.21.1 bodies; 1.21.2 removed the one and
                     rebuilt the other as a private placer)
  Direct mod subclasses of RecipeBookMenu are s7rest's facade (it implements CbItemRecipeMenus.Legacy).

  python gen_s7item_recipemenus.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemRecipeMenus"
RBM = "net/minecraft/class_1729"
CRAFTING = "net/minecraft/class_1714"
INVENTORY = "net/minecraft/class_1723"
FURNACE = "net/minecraft/class_1720"
VANILLA = [CRAFTING, INVENTORY, FURNACE]
GUARD = "top/dext/centurybridge/step/v1_21_2/rt/Guard"
SC = "Lnet/minecraft/class_1662;"  # 1.21.1 StackedContents (s7entity's facade)
HOLDER = "Lnet/minecraft/class_8786;"

# (1.21.1 shape, static name, has a per-vanilla-owner super static)
MEMBERS = [
    ("method_17697(Z" + HOLDER + "Lnet/minecraft/class_3222;)V", "handlePlacement", False),
    ("method_59963()V", "beginPlacingRecipe", False),
    ("method_59964(" + HOLDER + ")V", "finishPlacingRecipe", False),
    ("method_7654(" + SC + ")V", "fillCraftSlotsStackedContents", True),
    ("method_7657()V", "clearCraftingContent", True),
    ("method_7652(" + HOLDER + ")Z", "recipeMatches", True),
    ("method_7655()I", "getResultSlotIndex", True),
    ("method_7653()I", "getGridWidth", True),
    ("method_7656()I", "getGridHeight", True),
    ("method_7658()I", "getSize", True),
    ("method_32339(I)Z", "shouldMoveToInventory", True),
]

PLACE_NEW = "method_17697(ZZ" + HOLDER + "Lnet/minecraft/class_3218;Lnet/minecraft/class_1661;)Lnet/minecraft/class_1729$class_9885;"
FINISH_NEW = "method_59964(Lnet/minecraft/class_3218;" + HOLDER + ")V"


def sup(name):
    return "super" + name[0].upper() + name[1:]


def main():
    out = {"instanceRedirects": {}, "superRedirects": {}, "staticRedirects": {}, "adapterHooks": [],
           "overrideHooks": []}
    for shape, name, per_owner in MEMBERS:
        out["instanceRedirects"][RBM + "." + shape] = RT + "#" + name
        out["adapterHooks"].append({
            "under": RBM, "adapter": RT + "$Legacy_" + name, "requires": [shape], "synth": [],
            "basis": "a mod menu declaring 1.21.1's RecipeBookMenu." + name + " answers its calls (CbItemRecipeMenus)"})
        if per_owner:
            for owner in VANILLA:
                out["superRedirects"][owner + "." + shape] = RT + "#" + sup(name)
    # RecipeBookMenu's empty default, reached from a mod furnace menu (CraftingMenu and InventoryMenu go through
    # the finishPlacingRecipe hook's super bridge below)
    out["superRedirects"][FURNACE + ".method_59964(" + HOLDER + ")V"] = RT + "#superFinishPlacingRecipe"
    out["staticRedirects"][CRAFTING + ".method_17399(Lnet/minecraft/class_1703;Lnet/minecraft/class_1937;"
                           "Lnet/minecraft/class_1657;Lnet/minecraft/class_8566;Lnet/minecraft/class_1731;"
                           + HOLDER + ")V"] = RT + "#slotChangedCraftingGrid"
    for owner in VANILLA:
        out["overrideHooks"].append({
            "old": MEMBERS[0][0], "new": PLACE_NEW, "onlyUnder": owner,
            "adapter": RT + "$Legacy_handlePlacement",
            "bridge": RT + ".handleBridge(L" + RT + "$Legacy_handlePlacement;ZZ" + HOLDER
                      + "Lnet/minecraft/class_3218;Lnet/minecraft/class_1661;)Lnet/minecraft/class_1729$class_9885;",
            "superBridge": RT + ".superHandleBridge(L" + RT + "$SuperPlace;Z" + HOLDER + "Lnet/minecraft/class_3222;)V",
            "superAccess": RT + "$SuperPlace",
            "superAccessors": [PLACE_NEW],
            "basis": "1.21.2's packet handler calls handlePlacement(placeAll, creative, recipe, level, inventory): a "
                     "mod menu's 1.21.1 handlePlacement(placeAll, recipe, player) does the placement (ghost included); "
                     "its super call is 1.21.1's placement on 1.21.2's super (CbItemRecipeMenus)"})
    for owner in [CRAFTING, INVENTORY]:
        out["overrideHooks"].append({
            "old": MEMBERS[2][0], "new": FINISH_NEW, "onlyUnder": owner,
            "adapter": RT + "$Legacy_finishPlacingRecipe",
            "bridge": RT + ".finishBridge(L" + RT + "$Legacy_finishPlacingRecipe;Lnet/minecraft/class_3218;"
                      + HOLDER + ")V",
            "superBridge": RT + ".superFinishBridge(L" + RT + "$SuperFinish;" + HOLDER + ")V",
            "superAccess": RT + "$SuperFinish",
            "superAccessors": [FINISH_NEW],
            "basis": "AbstractCraftingMenu.handlePlacement calls finishPlacingRecipe(level, recipe): a mod menu's 1.21.1 "
                     "finishPlacingRecipe(recipe) answers; its super call is 1.21.1's body (the level from the menu's "
                     "ContainerLevelAccess, nothing on a client) -- not the frames hook, whose direct entry would hand "
                     "1.21.2's body a null level (CbItemRecipeMenus)"})
    for owner in VANILLA:
        out["overrideHooks"].append({
            "old": "method_7654(" + SC + ")V", "new": "method_7654(Lnet/minecraft/class_9875;)V", "onlyUnder": owner,
            "adapter": RT + "$Legacy_fillCraftSlotsStackedContents",
            "bridge": RT + ".fillBridge(L" + RT + "$Legacy_fillCraftSlotsStackedContents;Lnet/minecraft/class_9875;)V",
            "basis": "1.21.2's recipe book fills StackedItemContents: a mod menu's 1.21.1 override fills a 1.21.1 "
                     "StackedContents, poured into it (CbItemRecipeMenus.fillBridge)"})
    for owner in [CRAFTING, INVENTORY]:
        for old, new, what in [("method_7653()I", "method_61629()I", "getGridWidth"),
                               ("method_7656()I", "method_61630()I", "getGridHeight")]:
            out["overrideHooks"].append({
                "old": old, "new": new, "onlyUnder": owner, "guard": GUARD,
                "basis": "1.21.1 RecipeBookMenu." + what + " of a crafting menu is 1.21.2's AbstractCraftingMenu."
                         + what + " (same use: the recipe placement's grid): the mod's override answers it"})
    out["superCtorReshapes"] = {}
    for tail in ["", "Lnet/minecraft/class_1263;Lnet/minecraft/class_3913;"]:
        old = ("(Lnet/minecraft/class_3917;Lnet/minecraft/class_3956;Lnet/minecraft/class_5421;ILnet/minecraft/class_1661;"
               + tail + ")V")
        new = ("(Lnet/minecraft/class_3917;Lnet/minecraft/class_3956;Lnet/minecraft/class_5321;Lnet/minecraft/class_5421;"
               "ILnet/minecraft/class_1661;" + tail + ")V")
        n = 5 + (2 if tail else 0)
        ops = ["keep:0", "keep:1",
               "conv:1 " + RT + "#furnaceInputs(Lnet/minecraft/class_3956;)Lnet/minecraft/class_5321;"]
        ops += ["keep:%d" % i for i in range(2, n)]
        out["superCtorReshapes"][FURNACE + ".<init>" + old] = {
            "desc": new, "ops": ops,
            "basis": "1.21.2's AbstractFurnaceMenu takes the recipe property set of its inputs: 1.21.2's own for the "
                     "vanilla cooking types, else canSmelt is 1.21.1's lookup (CbItemRecipeMenus.furnaceInputs, "
                     "CbItemFurnaceMenuMixin)"}
    out["classRenames"] = {
        "net/minecraft/class_2952": "top/dext/centurybridge/step/v1_21_2/facade/PlaceRecipe",
        "net/minecraft/class_2955": "top/dext/centurybridge/step/v1_21_2/facade/ServerPlaceRecipe",
    }
    combiner(out)
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


COMBINER_RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemCombiner"
COMBINER = "net/minecraft/class_4861"
SMITHING = "net/minecraft/class_4862"
ANVIL = "net/minecraft/class_1706"
DEFS = "method_48352()Lnet/minecraft/class_8047;"
QUICK = "method_48357(Lnet/minecraft/class_1799;)I"


def combiner(out):
    """ItemCombinerMenu: createInputSlotDefinitions / getSlotToQuickMoveTo (CbItemCombiner, CbItemCombinerMenuMixin)."""
    out["instanceRedirects"][COMBINER + "." + DEFS] = COMBINER_RT + "#createInputSlotDefinitions"
    out["instanceRedirects"][COMBINER + "." + QUICK] = COMBINER_RT + "#getSlotToQuickMoveTo"
    for owner in [ANVIL, SMITHING]:
        out["superRedirects"][owner + "." + DEFS] = COMBINER_RT + "#superCreateInputSlotDefinitions"
    for owner in [COMBINER, SMITHING]:
        out["superRedirects"][owner + "." + QUICK] = COMBINER_RT + "#superGetSlotToQuickMoveTo"
    for shape, name in [(DEFS, "createInputSlotDefinitions"), (QUICK, "getSlotToQuickMoveTo")]:
        out["adapterHooks"].append({
            "under": COMBINER, "adapter": COMBINER_RT + "$Legacy_" + name, "requires": [shape], "synth": [],
            "basis": "a mod ItemCombinerMenu declaring 1.21.1's " + name + " answers its calls, the definition "
                     "1.21.2's constructor takes / the quick-move start (CbItemCombiner, CbItemCombinerMenuMixin)"})
    out["superCtorReshapes"][COMBINER + ".<init>(Lnet/minecraft/class_3917;ILnet/minecraft/class_1661;"
                             "Lnet/minecraft/class_3914;)V"] = {
        "desc": "(Lnet/minecraft/class_3917;ILnet/minecraft/class_1661;Lnet/minecraft/class_3914;Lnet/minecraft/class_8047;)V",
        "ops": ["keep", "keep", "keep", "keep", "push " + COMBINER_RT + "#deferred()Lnet/minecraft/class_8047;"],
        "basis": "1.21.1's constructor asked createInputSlotDefinitions() itself; the definition passed is a placeholder "
                 "CbItemCombinerMenuMixin replaces with the menu's own answer at the same point of the constructor"}


if __name__ == "__main__":
    main()
