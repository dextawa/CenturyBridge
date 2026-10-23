package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_1860;
import net.minecraft.class_1867;
import net.minecraft.class_1869;
import net.minecraft.class_2371;
import net.minecraft.class_310;
import net.minecraft.class_3972;
import net.minecraft.class_6880;
import net.minecraft.class_7225;
import net.minecraft.class_9875;

/**
 * 1.21.1's view of a recipe, which its client recipe book worked from, on 1.21.2's recipes: the result item
 * (getResultItem), whether it fits a crafting grid (canCraftInDimensions), its ingredients by grid position
 * (getIngredients, a gap where nothing goes), whether it is incomplete (isIncomplete), the items an ingredient
 * accepts (Ingredient.getItems). A 1.21.1 recipe class answers each through its own 1.21.1 method (the
 * adapters, added to such classes by adapterHooks); any other recipe as 1.21.1's vanilla recipes did, read off
 * 1.21.2's: the result its first display shows, the shaped pattern's size and positions, a shapeless recipe's
 * ingredient count, a single-item recipe's input, a smithing recipe's three slots.
 */
@Environment(EnvType.CLIENT)
public final class CbRestRecipeView {

    private CbRestRecipeView() {
    }

    /** getResultItem(registries) of a 1.21.1 recipe class */
    public interface LegacyResult {
        class_1799 method_8110(class_7225.class_7874 registries);
    }

    /** canCraftInDimensions(width, height) of a 1.21.1 recipe class */
    public interface LegacyDimensions {
        boolean method_8113(int width, int height);
    }

    /** getIngredients() of a 1.21.1 recipe class */
    public interface LegacyIngredients {
        class_2371<class_1856> method_8117();
    }

    /** isIncomplete() of a 1.21.1 recipe class */
    public interface LegacyIncomplete {
        boolean method_31584();
    }

    /** getToastSymbol() of a 1.21.1 recipe class */
    public interface LegacyToastSymbol {
        class_1799 method_17447();
    }

    /** getToastSymbol(): a 1.21.1 recipe class's own; else the station its first display shows, else 1.21.1's default (a crafting table) */
    public static class_1799 toastSymbol(class_1860<?> recipe) {
        if (recipe instanceof LegacyToastSymbol legacy) {
            return legacy.method_17447();
        }
        List<net.minecraft.class_10295> displays = recipe.method_64664();
        net.minecraft.class_638 level = class_310.method_1551().field_1687;
        if (!displays.isEmpty() && level != null) {
            return displays.get(0).comp_3259().method_64742(net.minecraft.class_10363.method_65008(level));
        }
        return new class_1799(net.minecraft.class_2246.field_9980);
    }

    /** the items an ingredient accepts, one stack each (1.21.1's Ingredient.getItems) */
    public static class_1799[] items(class_1856 ingredient) {
        if (ingredient == null) {
            return new class_1799[0];
        }
        List<class_6880<net.minecraft.class_1792>> holders = ingredient.method_8105();
        class_1799[] out = new class_1799[holders.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = new class_1799(holders.get(i));
        }
        return out;
    }

    /** whether an ingredient slot is a gap (1.21.1's Ingredient.EMPTY: nothing goes there) */
    public static boolean isGap(class_1856 ingredient) {
        return ingredient == null || ingredient.method_8105().isEmpty();
    }

    /** getResultItem(registries) */
    public static class_1799 resultItem(class_1860<?> recipe, class_7225.class_7874 registries) {
        if (recipe instanceof LegacyResult legacy) {
            return legacy.method_8110(registries);
        }
        List<net.minecraft.class_10295> displays = recipe.method_64664();
        net.minecraft.class_638 level = class_310.method_1551().field_1687;
        if (displays.isEmpty() || level == null) {
            return class_1799.field_8037;
        }
        return displays.get(0).comp_3258().method_64742(net.minecraft.class_10363.method_65008(level));
    }

    /** canCraftInDimensions(width, height) */
    public static boolean fits(class_1860<?> recipe, int width, int height) {
        if (recipe instanceof LegacyDimensions legacy) {
            return legacy.method_8113(width, height);
        }
        if (recipe instanceof class_1869 shaped) {
            return width >= shaped.method_8150() && height >= shaped.method_8158();
        }
        if (recipe instanceof class_1867 shapeless) {
            return width * height >= shapeless.method_61671().method_64675().size();
        }
        if (recipe instanceof net.minecraft.class_8059) {
            return width >= 3 && height >= 1;
        }
        return true;
    }

    /** getIngredients(): by grid position, null for a gap */
    public static List<class_1856> ingredients(class_1860<?> recipe) {
        List<class_1856> out = new ArrayList<>();
        if (recipe instanceof LegacyIngredients legacy) {
            for (class_1856 i : legacy.method_8117()) {
                out.add(isGap(i) ? null : i);
            }
            return out;
        }
        if (recipe instanceof class_1869 shaped) {
            for (Optional<class_1856> i : shaped.method_61693()) {
                out.add(i.orElse(null));
            }
            return out;
        }
        if (recipe instanceof class_3972 single) {
            out.add(single.method_64720());
            return out;
        }
        if (recipe instanceof net.minecraft.class_8059 smithing) {
            out.add((class_1856) smithing.method_64722().orElse(null));
            out.add((class_1856) smithing.method_64723().orElse(null));
            out.add((class_1856) smithing.method_64724().orElse(null));
            return out;
        }
        out.addAll(recipe.method_61671().method_64675());
        return out;
    }

    /** isIncomplete(): no ingredients, or an ingredient (not a gap) that accepts no item */
    public static boolean incomplete(class_1860<?> recipe) {
        if (recipe instanceof LegacyIncomplete legacy) {
            return legacy.method_31584();
        }
        List<class_1856> ingredients = ingredients(recipe);
        if (ingredients.isEmpty() || ingredients.stream().allMatch(i -> i == null)) {
            return true;
        }
        return recipe.method_61671().method_61687();
    }

    /** StackedContents.canCraft(recipe, null): whether the stacked items make the recipe once */
    public static boolean canCraft(class_9875 stacked, class_1860<?> recipe) {
        return stacked.method_61538(recipe, null);
    }
}
