package top.dext.centurybridge.step.v26_1.facade;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.level.Level;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipeKinds;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipes;

/**
 * 1.21.11's MapCloningRecipe (net/minecraft/class_1855, "crafting_special_mapcloning"): one filled map (one with a
 * map id) and empty maps, the filled map copied once per empty map. 26.1 made map cloning a data "crafting_transmute"
 * recipe; this is 1.21.11's recipe with 1.21.11's bodies, its serializer under 1.21.11's id (CbWallRecipeKinds).
 */
public class MapCloningRecipe extends CustomRecipe {

    /** 1.21.11 MapCloningRecipe(category) */
    public MapCloningRecipe(CraftingBookCategory category) {
        super();
        CbWallRecipes.customCategory(this, category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return cb$matches(input, level);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return cb$assemble(input);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public net.minecraft.world.item.crafting.RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return (net.minecraft.world.item.crafting.RecipeSerializer) CbWallRecipes.record(CbWallRecipeKinds.mapCloning());
    }

    /** 1.21.11 MapCloningRecipe.matches */
    public boolean cb$matches(CraftingInput input, Level level) {
        if (input.ingredientCount() < 2) {
            return false;
        }
        boolean filled = false;
        boolean empty = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.has(DataComponents.MAP_ID)) {
                if (filled) {
                    return false;
                }
                filled = true;
            } else if (stack.is(Items.MAP)) {
                empty = true;
            } else {
                return false;
            }
        }
        return filled && empty;
    }

    /** 1.21.11 MapCloningRecipe.assemble */
    public ItemStack cb$assemble(CraftingInput input) {
        int count = 0;
        ItemStack filled = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.has(DataComponents.MAP_ID)) {
                if (!filled.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                filled = stack;
            } else if (stack.is(Items.MAP)) {
                count++;
            } else {
                return ItemStack.EMPTY;
            }
        }
        return !filled.isEmpty() && count >= 1 ? filled.copyWithCount(count + 1) : ItemStack.EMPTY;
    }
}
