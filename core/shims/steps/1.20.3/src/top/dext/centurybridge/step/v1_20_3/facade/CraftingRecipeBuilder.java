package top.dext.centurybridge.step.v1_20_3.facade;

import com.google.gson.JsonObject;

/**
 * Facade for 1.20.2 CraftingRecipeBuilder (class_7798), gone in 1.20.3 (the crafting builders
 * implement RecipeBuilder directly there, which took over determineBookCategory unchanged: the same
 * BUILDING_BLOCKS / TOOLS+COMBAT / REDSTONE / other mapping). A 1.20.2 mod's builder extending it
 * keeps its superclass; its results extend {@link CraftingResult}, 1.20.2's
 * CraftingRecipeBuilder.CraftingResult (class_7798$class_7799). Members public, as a facade's must be.
 */
public abstract class CraftingRecipeBuilder {

    public CraftingRecipeBuilder() {
    }

    /** determineBookCategory(category) */
    public static net.minecraft.class_7710 method_46202(net.minecraft.class_7800 category) {
        return net.minecraft.class_5797.method_55308(category);
    }

    /** 1.20.2 CraftingRecipeBuilder.CraftingResult: a crafting FinishedRecipe writing its book category */
    public abstract static class CraftingResult implements FinishedRecipe {

        /** category */
        public final net.minecraft.class_7710 field_40633;

        public CraftingResult(net.minecraft.class_7710 category) {
            this.field_40633 = category;
        }

        /** serializeRecipeData(json): the book category */
        @Override
        public void method_10416(JsonObject json) {
            json.addProperty("category", field_40633.method_15434());
        }
    }
}
