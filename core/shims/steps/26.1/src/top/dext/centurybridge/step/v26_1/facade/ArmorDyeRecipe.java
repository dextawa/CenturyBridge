package top.dext.centurybridge.step.v26_1.facade;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.level.Level;
import top.dext.centurybridge.step.v26_1.rt.CbWallDyes;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipeKinds;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipes;
import top.dext.centurybridge.step.v26_1.rt.CbWallRegistries;
import top.dext.centurybridge.step.v26_1.rt.CbWallTags;

/**
 * 1.21.11's ArmorDyeRecipe (net/minecraft/class_1849, "crafting_special_armordye"): one dyeable item and any dyes,
 * the item dyed with all of them. 26.1 removed it (each armor piece has a data "crafting_dye" recipe now); this is
 * 1.21.11's recipe with 1.21.11's bodies ({@link #cb$matches}, {@link #cb$assemble}), its serializer registered under
 * 1.21.11's id (CbWallRecipeKinds).
 */
public class ArmorDyeRecipe extends CustomRecipe {

    /**
     * 1.21.11's ItemTags.DYEABLE: 26.1 dropped the tag (its dye recipes name each item); the layer ships
     * minecraft:dyeable again with 1.21.11's members (data/minecraft/tags/item/dyeable.json)
     */
    @SuppressWarnings("unchecked")
    private static final TagKey<Item> DYEABLE = (TagKey<Item>) CbWallTags.field_48803();

    /** 1.21.11 ArmorDyeRecipe(category) */
    public ArmorDyeRecipe(CraftingBookCategory category) {
        super();
        CbWallRecipes.customCategory(this, category);
    }

    /** 26.1's matches: 1.21.11's matches(input, level) under its own token, which a 1.21.11 subclass overrides */
    @Override
    public boolean matches(CraftingInput input, Level level) {
        return method_17701(input, level);
    }

    /** 26.1's assemble(input): 1.21.11's assemble(input, registries) under its own token, with the level's registries */
    @Override
    public ItemStack assemble(CraftingInput input) {
        return method_17700(input, CbWallRegistries.access());
    }

    /**
     * 1.21.11 ArmorDyeRecipe.matches(input, level) (class_1849.method_17701): the class is gone in 26.1, so a 1.21.11 subclass's
     * override keeps this name past the wall and overrides it here
     */
    public boolean method_17701(CraftingInput input, Level level) {
        return cb$matches(input, level);
    }

    /** 1.21.11 ArmorDyeRecipe.assemble(input, registries) (class_1849.method_17700), likewise */
    public ItemStack method_17700(CraftingInput input, net.minecraft.core.HolderLookup.Provider registries) {
        return cb$assemble(input);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public net.minecraft.world.item.crafting.RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return (net.minecraft.world.item.crafting.RecipeSerializer) CbWallRecipes.record(CbWallRecipeKinds.armorDye());
    }

    /** 1.21.11 ArmorDyeRecipe.matches */
    public boolean cb$matches(CraftingInput input, Level level) {
        if (input.ingredientCount() < 2) {
            return false;
        }
        boolean dyeable = false;
        boolean dye = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(DYEABLE)) {
                if (dyeable) {
                    return false;
                }
                dyeable = true;
            } else if (stack.getItem() instanceof DyeItem) {
                dye = true;
            } else {
                return false;
            }
        }
        return dye && dyeable;
    }

    /** 1.21.11 ArmorDyeRecipe.assemble */
    public ItemStack cb$assemble(CraftingInput input) {
        List<DyeColor> dyes = new ArrayList<>();
        ItemStack target = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(DYEABLE)) {
                if (!target.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                target = stack.copy();
            } else {
                Item item = stack.getItem();
                if (!(item instanceof DyeItem)) {
                    return ItemStack.EMPTY;
                }
                dyes.add(CbWallDyes.dyeColor(item));
            }
        }
        return !target.isEmpty() && !dyes.isEmpty() ? DyedItemColor.applyDyes(target, dyes) : ItemStack.EMPTY;
    }
}
