package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1703;
import net.minecraft.class_1714;
import net.minecraft.class_1720;
import net.minecraft.class_1723;
import net.minecraft.class_1729;
import net.minecraft.class_1731;
import net.minecraft.class_1737;
import net.minecraft.class_1799;
import net.minecraft.class_1860;
import net.minecraft.class_1863;
import net.minecraft.class_1874;
import net.minecraft.class_1937;
import net.minecraft.class_2695;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3955;
import net.minecraft.class_3956;
import net.minecraft.class_5321;
import net.minecraft.class_8566;
import net.minecraft.class_8786;
import net.minecraft.class_9696;
import net.minecraft.class_9875;
import net.minecraft.class_9884;
import net.minecraft.class_10290;
import net.minecraft.class_10297;
import top.dext.centurybridge.step.v1_21_2.facade.StackedContents;

/**
 * Calls to 1.21.1's RecipeBookMenu members (class_1729). 1.21.2 cut RecipeBookMenu down to handlePlacement (new
 * shape, answering a PostPlaceAction), fillCraftSlotsStackedContents(StackedItemContents) and getRecipeBookType; the
 * grid members moved to the new AbstractCraftingMenu (class_9884, CraftingMenu and InventoryMenu) or into the
 * placement code. Each static answers the 1.21.1 call:
 * <ul>
 * <li>a menu that declares the 1.21.1 method itself -- a mod subclass, which implements the shape's {@code Legacy_*}
 *     interface (the engine adds it to a mod class under RecipeBookMenu declaring the shape; s7rest's facade for
 *     direct mod subclasses implements {@link Legacy}) -- answers with its own method;</li>
 * <li>a vanilla 1.21.2 menu answers with the body its class had in 1.21.1 (CraftingMenu, InventoryMenu,
 *     AbstractFurnaceMenu; read off 1.21.1's bytecode), built on 1.21.2's members.</li>
 * </ul>
 * The {@code super*} statics are a mod subclass's super call into a vanilla menu: 1.21.1's vanilla body, never the
 * mod's own override.
 */
public final class CbItemRecipeMenus {
    private CbItemRecipeMenus() {
    }

    // ---- the 1.21.1 shapes a mod menu declares (StackedContents is s7entity's facade for class_1662) ----

    /** handlePlacement(placeAll, recipe, player) */
    public interface Legacy_handlePlacement {
        void method_17697(boolean placeAll, class_8786<?> recipe, class_3222 player);
    }

    /** beginPlacingRecipe() (protected in 1.21.1; the engine makes the mod's implementation public) */
    public interface Legacy_beginPlacingRecipe {
        void method_59963();
    }

    /** finishPlacingRecipe(recipe) (protected in 1.21.1) */
    public interface Legacy_finishPlacingRecipe {
        void method_59964(class_8786<?> recipe);
    }

    /** fillCraftSlotsStackedContents(contents) */
    public interface Legacy_fillCraftSlotsStackedContents {
        void method_7654(StackedContents contents);
    }

    /** clearCraftingContent() */
    public interface Legacy_clearCraftingContent {
        void method_7657();
    }

    /** recipeMatches(recipe) */
    public interface Legacy_recipeMatches {
        boolean method_7652(class_8786<?> recipe);
    }

    /** getResultSlotIndex() */
    public interface Legacy_getResultSlotIndex {
        int method_7655();
    }

    /** getGridWidth() */
    public interface Legacy_getGridWidth {
        int method_7653();
    }

    /** getGridHeight() */
    public interface Legacy_getGridHeight {
        int method_7656();
    }

    /** getSize() */
    public interface Legacy_getSize {
        int method_7658();
    }

    /** shouldMoveToInventory(slot) */
    public interface Legacy_shouldMoveToInventory {
        boolean method_32339(int slot);
    }

    /** every 1.21.1 shape of RecipeBookMenu: what a facade standing for 1.21.1's RecipeBookMenu implements */
    public interface Legacy extends Legacy_handlePlacement, Legacy_beginPlacingRecipe, Legacy_finishPlacingRecipe,
            Legacy_fillCraftSlotsStackedContents, Legacy_clearCraftingContent, Legacy_recipeMatches,
            Legacy_getResultSlotIndex, Legacy_getGridWidth, Legacy_getGridHeight, Legacy_getSize,
            Legacy_shouldMoveToInventory {
    }

    // ---- recipeMatches ----

    public static boolean recipeMatches(class_1729 menu, class_8786<?> recipe) {
        if (menu instanceof Legacy_recipeMatches legacy) {
            return legacy.method_7652(recipe);
        }
        return superRecipeMatches(menu, recipe);
    }

    /**
     * 1.21.1: CraftingMenu / InventoryMenu: ((CraftingRecipe) recipe.value()).matches(craftSlots.asCraftInput(),
     * player.level()); AbstractFurnaceMenu: ((AbstractCookingRecipe) recipe.value()).matches(new
     * SingleRecipeInput(container.getItem(0)), level). The casts are 1.21.1's.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean superRecipeMatches(class_1729 menu, class_8786<?> recipe) {
        if (menu instanceof class_1714 crafting) {
            class_1860 value = (class_3955) recipe.comp_1933();
            return value.method_8115(crafting.field_52559.method_59961(), crafting.field_7797.method_37908());
        }
        if (menu instanceof class_1723 inventory) {
            class_1860 value = (class_3955) recipe.comp_1933();
            return value.method_8115(inventory.field_52559.method_59961(), inventory.field_7828.method_37908());
        }
        if (menu instanceof class_1720 furnace) {
            class_1860 value = (class_1874) recipe.comp_1933();
            return value.method_8115(new class_9696(furnace.field_7824.method_5438(0)), furnace.field_7822);
        }
        throw unknown(menu, "recipeMatches");
    }

    public static boolean superRecipeMatches(class_1714 menu, class_8786<?> recipe) {
        return superRecipeMatches((class_1729) menu, recipe);
    }

    public static boolean superRecipeMatches(class_1723 menu, class_8786<?> recipe) {
        return superRecipeMatches((class_1729) menu, recipe);
    }

    public static boolean superRecipeMatches(class_1720 menu, class_8786<?> recipe) {
        return superRecipeMatches((class_1729) menu, recipe);
    }

    // ---- getResultSlotIndex ----

    public static int getResultSlotIndex(class_1729 menu) {
        if (menu instanceof Legacy_getResultSlotIndex legacy) {
            return legacy.method_7655();
        }
        return superGetResultSlotIndex(menu);
    }

    /** 1.21.1: CraftingMenu / InventoryMenu 0, AbstractFurnaceMenu 2 */
    public static int superGetResultSlotIndex(class_1729 menu) {
        if (menu instanceof class_9884) {
            return 0;
        }
        if (menu instanceof class_1720) {
            return 2;
        }
        throw unknown(menu, "getResultSlotIndex");
    }

    public static int superGetResultSlotIndex(class_1714 menu) {
        return superGetResultSlotIndex((class_1729) menu);
    }

    public static int superGetResultSlotIndex(class_1723 menu) {
        return superGetResultSlotIndex((class_1729) menu);
    }

    public static int superGetResultSlotIndex(class_1720 menu) {
        return superGetResultSlotIndex((class_1729) menu);
    }

    // ---- getGridWidth ----

    public static int getGridWidth(class_1729 menu) {
        if (menu instanceof Legacy_getGridWidth legacy) {
            return legacy.method_7653();
        }
        return superGetGridWidth(menu);
    }

    /** 1.21.1: CraftingMenu / InventoryMenu craftSlots.getWidth(), AbstractFurnaceMenu 1 */
    public static int superGetGridWidth(class_1729 menu) {
        if (menu instanceof class_9884 crafting) {
            return crafting.field_52559.method_17398();
        }
        if (menu instanceof class_1720) {
            return 1;
        }
        throw unknown(menu, "getGridWidth");
    }

    public static int superGetGridWidth(class_1714 menu) {
        return superGetGridWidth((class_1729) menu);
    }

    public static int superGetGridWidth(class_1723 menu) {
        return superGetGridWidth((class_1729) menu);
    }

    public static int superGetGridWidth(class_1720 menu) {
        return superGetGridWidth((class_1729) menu);
    }

    // ---- getGridHeight ----

    public static int getGridHeight(class_1729 menu) {
        if (menu instanceof Legacy_getGridHeight legacy) {
            return legacy.method_7656();
        }
        return superGetGridHeight(menu);
    }

    /** 1.21.1: CraftingMenu / InventoryMenu craftSlots.getHeight(), AbstractFurnaceMenu 1 */
    public static int superGetGridHeight(class_1729 menu) {
        if (menu instanceof class_9884 crafting) {
            return crafting.field_52559.method_17397();
        }
        if (menu instanceof class_1720) {
            return 1;
        }
        throw unknown(menu, "getGridHeight");
    }

    public static int superGetGridHeight(class_1714 menu) {
        return superGetGridHeight((class_1729) menu);
    }

    public static int superGetGridHeight(class_1723 menu) {
        return superGetGridHeight((class_1729) menu);
    }

    public static int superGetGridHeight(class_1720 menu) {
        return superGetGridHeight((class_1729) menu);
    }

    // ---- getSize ----

    public static int getSize(class_1729 menu) {
        if (menu instanceof Legacy_getSize legacy) {
            return legacy.method_7658();
        }
        return superGetSize(menu);
    }

    /** 1.21.1: CraftingMenu 10, InventoryMenu 5, AbstractFurnaceMenu 3 */
    public static int superGetSize(class_1729 menu) {
        if (menu instanceof class_1714) {
            return 10;
        }
        if (menu instanceof class_1723) {
            return 5;
        }
        if (menu instanceof class_1720) {
            return 3;
        }
        throw unknown(menu, "getSize");
    }

    public static int superGetSize(class_1714 menu) {
        return superGetSize((class_1729) menu);
    }

    public static int superGetSize(class_1723 menu) {
        return superGetSize((class_1729) menu);
    }

    public static int superGetSize(class_1720 menu) {
        return superGetSize((class_1729) menu);
    }

    // ---- shouldMoveToInventory ----

    public static boolean shouldMoveToInventory(class_1729 menu, int slot) {
        if (menu instanceof Legacy_shouldMoveToInventory legacy) {
            return legacy.method_32339(slot);
        }
        return superShouldMoveToInventory(menu, slot);
    }

    /**
     * 1.21.1: CraftingMenu / InventoryMenu slot != this.getResultSlotIndex() (a virtual call: a mod subclass's own
     * answer counts); AbstractFurnaceMenu slot != 1 (a literal, the fuel slot -- 1.21.1's code, kept).
     */
    public static boolean superShouldMoveToInventory(class_1729 menu, int slot) {
        if (menu instanceof class_9884) {
            return slot != getResultSlotIndex(menu);
        }
        if (menu instanceof class_1720) {
            return slot != 1;
        }
        throw unknown(menu, "shouldMoveToInventory");
    }

    public static boolean superShouldMoveToInventory(class_1714 menu, int slot) {
        return superShouldMoveToInventory((class_1729) menu, slot);
    }

    public static boolean superShouldMoveToInventory(class_1723 menu, int slot) {
        return superShouldMoveToInventory((class_1729) menu, slot);
    }

    public static boolean superShouldMoveToInventory(class_1720 menu, int slot) {
        return superShouldMoveToInventory((class_1729) menu, slot);
    }

    // ---- clearCraftingContent ----

    public static void clearCraftingContent(class_1729 menu) {
        if (menu instanceof Legacy_clearCraftingContent legacy) {
            legacy.method_7657();
            return;
        }
        superClearCraftingContent(menu);
    }

    /**
     * 1.21.1: CraftingMenu craftSlots.clearContent() then resultSlots.clearContent(); InventoryMenu the result
     * first; AbstractFurnaceMenu getSlot(0) and getSlot(2) set to the empty stack.
     */
    public static void superClearCraftingContent(class_1729 menu) {
        if (menu instanceof class_1714 crafting) {
            crafting.field_52559.method_5448();
            crafting.field_52560.method_5448();
        } else if (menu instanceof class_1723 inventory) {
            inventory.field_52560.method_5448();
            inventory.field_52559.method_5448();
        } else if (menu instanceof class_1720 furnace) {
            furnace.method_7611(0).method_7673(class_1799.field_8037);
            furnace.method_7611(2).method_7673(class_1799.field_8037);
        } else {
            throw unknown(menu, "clearCraftingContent");
        }
    }

    public static void superClearCraftingContent(class_1714 menu) {
        superClearCraftingContent((class_1729) menu);
    }

    public static void superClearCraftingContent(class_1723 menu) {
        superClearCraftingContent((class_1729) menu);
    }

    public static void superClearCraftingContent(class_1720 menu) {
        superClearCraftingContent((class_1729) menu);
    }

    // ---- fillCraftSlotsStackedContents ----

    public static void fillCraftSlotsStackedContents(class_1729 menu, StackedContents contents) {
        if (menu instanceof Legacy_fillCraftSlotsStackedContents legacy) {
            legacy.method_7654(contents);
            return;
        }
        superFillCraftSlotsStackedContents(menu, contents);
    }

    /**
     * 1.21.1: CraftingMenu / InventoryMenu craftSlots.fillStackedContents(contents); AbstractFurnaceMenu the
     * container's, when it is StackedContentsCompatible. The 1.21.1-shaped fill is s7level's CbLevelStacked (a
     * container's own 1.21.1 override, else 1.21.2's fill read back by raw id).
     */
    public static void superFillCraftSlotsStackedContents(class_1729 menu, StackedContents contents) {
        if (menu instanceof class_9884 crafting) {
            CbLevelStacked.method_7683((class_1737) crafting.field_52559, contents);
        } else if (menu instanceof class_1720 furnace) {
            if (furnace.field_7824 instanceof class_1737 compatible) {
                CbLevelStacked.method_7683(compatible, contents);
            }
        } else {
            throw unknown(menu, "fillCraftSlotsStackedContents");
        }
    }

    public static void superFillCraftSlotsStackedContents(class_1714 menu, StackedContents contents) {
        superFillCraftSlotsStackedContents((class_1729) menu, contents);
    }

    /**
     * 1.21.2's fillCraftSlotsStackedContents(StackedItemContents) of a mod vanilla-menu subclass overriding 1.21.1's:
     * the override fills a 1.21.1 StackedContents, poured into 1.21.2's (CbEntityStacked.pour).
     */
    public static void fillBridge(Legacy_fillCraftSlotsStackedContents self, class_9875 contents) {
        StackedContents legacy = new StackedContents();
        self.method_7654(legacy);
        CbEntityStacked.pour(legacy, contents);
    }

    public static void superFillCraftSlotsStackedContents(class_1723 menu, StackedContents contents) {
        superFillCraftSlotsStackedContents((class_1729) menu, contents);
    }

    public static void superFillCraftSlotsStackedContents(class_1720 menu, StackedContents contents) {
        superFillCraftSlotsStackedContents((class_1729) menu, contents);
    }

    // ---- beginPlacingRecipe / finishPlacingRecipe ----

    public static void beginPlacingRecipe(class_1729 menu) {
        if (menu instanceof Legacy_beginPlacingRecipe legacy) {
            legacy.method_59963();
            return;
        }
        superBeginPlacingRecipe(menu);
    }

    /** 1.21.1: CraftingMenu sets placingRecipe (1.21.2's own beginPlacingRecipe); RecipeBookMenu's default is empty */
    public static void superBeginPlacingRecipe(class_1729 menu) {
        if (menu instanceof class_1714 crafting) {
            crafting.method_59963();
        }
    }

    public static void finishPlacingRecipe(class_1729 menu, class_8786<?> recipe) {
        if (menu instanceof Legacy_finishPlacingRecipe legacy) {
            legacy.method_59964(recipe);
            return;
        }
        superFinishPlacingRecipe(menu, recipe);
    }

    /**
     * 1.21.1: CraftingMenu clears placingRecipe and, through its ContainerLevelAccess, runs slotChangedCraftingGrid
     * on the access's level -- which did nothing on a client level; 1.21.2's finishPlacingRecipe(ServerLevel, recipe)
     * is that same body for a server level. RecipeBookMenu's default is empty (InventoryMenu and the furnaces kept
     * it).
     */
    @SuppressWarnings("unchecked")
    public static void superFinishPlacingRecipe(class_1729 menu, class_8786<?> recipe) {
        if (menu instanceof class_1714 crafting) {
            crafting.field_51625 = false;
            crafting.field_7799.method_17393((level, pos) -> {
                if (level instanceof class_3218 server) {
                    crafting.method_59964(server, (class_8786<class_3955>) recipe);
                }
            });
        }
    }

    /** a mod furnace menu's super.finishPlacingRecipe(recipe): RecipeBookMenu's empty default */
    public static void superFinishPlacingRecipe(class_1720 menu, class_8786<?> recipe) {
    }

    /** the 1.21.2 shape, reached past a mod CraftingMenu / InventoryMenu subclass's 1.21.1 override */
    public interface SuperFinish {
        void cb$super$method_59964(class_3218 level, class_8786<?> recipe);
    }

    /**
     * A mod CraftingMenu / InventoryMenu subclass's super.finishPlacingRecipe(recipe) (the override hook's super
     * bridge): 1.21.1's body as {@link #superFinishPlacingRecipe}, with 1.21.2's finishPlacingRecipe reached as super
     * -- the mod class's own 1.21.2 shape forwards to its 1.21.1 override.
     */
    public static void superFinishBridge(SuperFinish self, class_8786<?> recipe) {
        if (self instanceof class_1714 crafting) {
            crafting.field_51625 = false;
            crafting.field_7799.method_17393((level, pos) -> {
                if (level instanceof class_3218 server) {
                    self.cb$super$method_59964(server, recipe);
                }
            });
        }
    }

    /**
     * 1.21.2's finishPlacingRecipe(level, recipe) of a mod CraftingMenu / InventoryMenu subclass: its 1.21.1
     * override (1.21.1 handed it only the recipe; the level is the one its ContainerLevelAccess holds).
     */
    public static void finishBridge(Legacy_finishPlacingRecipe self, class_3218 level, class_8786<?> recipe) {
        self.method_59964(recipe);
    }

    // ---- handlePlacement ----

    public static void handlePlacement(class_1729 menu, boolean placeAll, class_8786<?> recipe, class_3222 player) {
        if (menu instanceof Legacy_handlePlacement legacy) {
            legacy.method_17697(placeAll, recipe, player);
            return;
        }
        superHandlePlacement(menu, placeAll, recipe, player);
    }

    /** 1.21.2's handlePlacement of the menu: its own, or (past a mod override) super's */
    private interface Placement {
        class_1729.class_9885 place(boolean placeAll, boolean creative, class_8786<?> recipe, class_3218 level,
            class_1661 inventory);
    }

    /**
     * 1.21.1's RecipeBookMenu.handlePlacement on a vanilla menu: beginPlacingRecipe, ServerPlaceRecipe.recipeClicked,
     * finishPlacingRecipe (always). recipeClicked placed nothing for a missing recipe or one not in the player's
     * recipe book, nor (the player not creative) when the grid could not be cleared into the inventory; otherwise it
     * placed the recipe, or -- when the inventory could not craft it -- cleared the grid and sent the client a ghost
     * recipe, marking the inventory changed either way. 1.21.2's handlePlacement(placeAll, creative, recipe, level,
     * inventory) is that placement (the clear test and the inventory change included) with begin/finish around it,
     * answering PLACE_GHOST_RECIPE where 1.21.1 sent the ghost; the ghost packet now carries a recipe display: the
     * first one the server's recipe manager lists for the recipe (1.21.2's packet handler sends the display the
     * client clicked; a recipe without a display has nothing a client could draw).
     */
    public static void superHandlePlacement(class_1729 menu, boolean placeAll, class_8786<?> recipe,
            class_3222 player) {
        place(menu, menu::method_17697, placeAll, recipe, player);
    }

    private static void place(class_1729 menu, Placement placement, boolean placeAll, class_8786<?> recipe,
            class_3222 player) {
        if (recipe == null || !player.method_14253().method_64593(recipe.comp_1932())) {
            beginPlacingRecipe(menu);
            finishPlacingRecipe(menu, recipe);
            return;
        }
        class_1729.class_9885 action = placement.place(placeAll, player.method_7337(), recipe, player.method_51469(),
            player.method_31548());
        if (action == class_1729.class_9885.field_52573) {
            sendGhostRecipe(player, recipe);
        }
    }

    /**
     * 1.21.1's ClientboundPlaceGhostRecipePacket(player.containerMenu.containerId, recipe): 1.21.2's packet carries a
     * recipe display instead of the recipe -- the first one the server's recipe manager lists for it (1.21.2's packet
     * handler sends the display the client clicked; a recipe without a display has nothing a client could draw).
     */
    public static void sendGhostRecipe(class_3222 player, class_8786<?> recipe) {
        class_1863 manager = player.field_13995.method_3772();
        class_10297[] first = new class_10297[1];
        manager.method_64679(recipe.comp_1932(), entry -> {
            if (first[0] == null) {
                first[0] = entry;
            }
        });
        if (first[0] != null) {
            player.field_13987.method_14364(new class_2695(player.field_7512.field_7763, first[0].comp_3263()));
        }
    }

    /** the 1.21.2 shape, reached past a mod menu's 1.21.1 handlePlacement override */
    public interface SuperPlace {
        class_1729.class_9885 cb$super$method_17697(boolean placeAll, boolean creative, class_8786<?> recipe,
            class_3218 level, class_1661 inventory);
    }

    /** a mod vanilla-menu subclass's super.handlePlacement(placeAll, recipe, player) (the override hook's super bridge) */
    public static void superHandleBridge(SuperPlace self, boolean placeAll, class_8786<?> recipe, class_3222 player) {
        place((class_1729) self, self::cb$super$method_17697, placeAll, recipe, player);
    }

    /**
     * 1.21.2's handlePlacement of a mod vanilla-menu subclass overriding 1.21.1's: the override, with the player the
     * inventory belongs to (1.21.2's packet handler passes the server player's own inventory). The 1.21.1 override
     * did the whole placement, the ghost recipe included (its super call sends it), so 1.21.2 is told NOTHING.
     */
    public static class_1729.class_9885 handleBridge(Legacy_handlePlacement self, boolean placeAll, boolean creative,
            class_8786<?> recipe, class_3218 level, class_1661 inventory) {
        self.method_17697(placeAll, recipe, (class_3222) inventory.field_7546);
        return class_1729.class_9885.field_52572;
    }

    // ---- AbstractFurnaceMenu's constructor and canSmelt ----

    /**
     * The recipe property set 1.21.2's AbstractFurnaceMenu constructor takes, for a 1.21.1 constructor that took only
     * the recipe type: 1.21.2's own pairing for smelting / blasting / smoking / campfire cooking (FurnaceMenu,
     * BlastFurnaceMenu, SmokerMenu pass these). Another recipe type has no set; it gets the furnace's, which its menu
     * never reads: canSmelt is 1.21.1's lookup for it (CbItemFurnaceMenuMixin).
     */
    public static class_5321<class_10290> furnaceInputs(class_3956<?> type) {
        if (type == class_3956.field_17547) {
            return class_10290.field_54651;
        }
        if (type == class_3956.field_17548) {
            return class_10290.field_54652;
        }
        if (type == class_3956.field_17549) {
            return class_10290.field_54653;
        }
        return class_10290.field_54650;
    }

    /** whether the recipe type has 1.21.2's input set ({@link #furnaceInputs}) */
    public static boolean hasInputSet(class_3956<?> type) {
        return type == class_3956.field_17546 || type == class_3956.field_17547 || type == class_3956.field_17548
            || type == class_3956.field_17549;
    }

    /**
     * 1.21.1 AbstractFurnaceMenu.canSmelt(stack): level.getRecipeManager().getRecipeFor(recipeType, new
     * SingleRecipeInput(stack), level).isPresent() -- the level's 1.21.1 recipe manager as the layer gives it
     * (CbLevelLevels).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean legacyCanSmelt(class_3956<?> type, class_1799 stack, class_1937 level) {
        return CbLevelLevels.method_8433(level).method_8132((class_3956) type, new class_9696(stack), level).isPresent();
    }

    // ---- CraftingMenu.slotChangedCraftingGrid ----

    /**
     * 1.21.1 CraftingMenu.slotChangedCraftingGrid(menu, level, player, craftSlots, resultSlots, recipe): did nothing
     * on a client level, otherwise (casting the player to ServerPlayer) 1.21.2's body, which takes the ServerLevel.
     */
    @SuppressWarnings("unchecked")
    public static void slotChangedCraftingGrid(class_1703 menu, class_1937 level, class_1657 player,
            class_8566 craftSlots, class_1731 resultSlots, class_8786<class_3955> recipe) {
        if (!level.method_8608()) {
            class_1714.method_17399(menu, (class_3218) level, player, craftSlots, resultSlots, recipe);
        }
    }

    private static AbstractMethodError unknown(class_1729 menu, String member) {
        // 1.21.1's RecipeBookMenu members were abstract; every 1.21.2 vanilla subclass is CraftingMenu, InventoryMenu
        // (AbstractCraftingMenu) or an AbstractFurnaceMenu. Anything else is a mod menu that did not implement the
        // 1.21.1 method -- an AbstractMethodError in 1.21.1.
        return new AbstractMethodError(menu.getClass().getName() + " does not implement 1.21.1 RecipeBookMenu." + member);
    }
}
