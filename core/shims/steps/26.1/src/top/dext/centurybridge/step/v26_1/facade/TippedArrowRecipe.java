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
import top.dext.centurybridge.step.v26_1.rt.CbWallRegistries;

/**
 * 1.21.11's TippedArrowRecipe (net/minecraft/class_1876, "crafting_special_tippedarrow"): a lingering potion amid
 * eight arrows in a full 3x3 grid, eight tipped arrows of the potion's contents. 26.1 made it a data
 * "crafting_imbue" recipe; this is 1.21.11's recipe with 1.21.11's bodies, its serializer under 1.21.11's id.
 */
public class TippedArrowRecipe extends CustomRecipe {

    /** 1.21.11 TippedArrowRecipe(category) */
    public TippedArrowRecipe(CraftingBookCategory category) {
        super();
        CbWallRecipes.customCategory(this, category);
    }

    /** 1.21.11 TippedArrowRecipe.matches(input, level) (method_17741): 26.1's matches, which a 1.21.11 subclass's override (renamed by the wall table) overrides */
    @Override
    public boolean matches(CraftingInput input, Level level) {
        return cb$matches(input, level);
    }

    /** 26.1's assemble(input): 1.21.11's assemble(input, registries) with the level's registries */
    @Override
    public ItemStack assemble(CraftingInput input) {
        return assemble(input, CbWallRegistries.access());
    }

    /** 1.21.11 TippedArrowRecipe.assemble(input, registries) (class_1876.method_17740) under the wall table's name, which a 1.21.11 subclass's override takes */
    public ItemStack assemble(CraftingInput input, net.minecraft.core.HolderLookup.Provider registries) {
        return cb$assemble(input);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public net.minecraft.world.item.crafting.RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return (net.minecraft.world.item.crafting.RecipeSerializer) CbWallRecipes.record(CbWallRecipeKinds.tippedArrow());
    }

    /** 1.21.11 TippedArrowRecipe.matches */
    public boolean cb$matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3 || input.ingredientCount() != 9) {
            return false;
        }
        for (int y = 0; y < input.height(); y++) {
            for (int x = 0; x < input.width(); x++) {
                ItemStack stack = input.getItem(x, y);
                if (stack.isEmpty()) {
                    return false;
                }
                if (x == 1 && y == 1) {
                    if (!stack.is(Items.LINGERING_POTION)) {
                        return false;
                    }
                } else if (!stack.is(Items.ARROW)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** 1.21.11 TippedArrowRecipe.assemble */
    public ItemStack cb$assemble(CraftingInput input) {
        ItemStack potion = input.getItem(1, 1);
        if (!potion.is(Items.LINGERING_POTION)) {
            return ItemStack.EMPTY;
        }
        ItemStack arrows = new ItemStack(Items.TIPPED_ARROW, 8);
        arrows.set(DataComponents.POTION_CONTENTS, potion.get(DataComponents.POTION_CONTENTS));
        return arrows;
    }
}
