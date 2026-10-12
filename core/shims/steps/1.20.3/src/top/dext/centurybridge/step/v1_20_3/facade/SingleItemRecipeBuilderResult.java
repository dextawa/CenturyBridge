package top.dext.centurybridge.step.v1_20_3.facade;

import com.google.gson.JsonObject;
import java.util.Objects;
import top.dext.centurybridge.step.v1_20_3.rt.CbDataRecipes;

/**
 * Facade for 1.20.2 SingleItemRecipeBuilder.Result (class_3981$class_3982), gone in 1.20.3: the
 * FinishedRecipe a single-item (stonecutting-style) recipe was saved as. The 1.20.2 record whole --
 * its JSON (group unless empty, the ingredient, the result item's id, the count), its components and
 * its record equality, hash and text -- as a plain class: a facade's members must be public, and it
 * must not be final since the 1.20.1 class was not.
 */
public class SingleItemRecipeBuilderResult implements FinishedRecipe {

    public final net.minecraft.class_2960 comp_1230;
    public final net.minecraft.class_1865<?> comp_1231;
    public final String comp_1947;
    public final net.minecraft.class_1856 comp_1948;
    public final net.minecraft.class_1792 comp_1949;
    public final int comp_1950;
    public final net.minecraft.class_8779 comp_1235;

    public SingleItemRecipeBuilderResult(net.minecraft.class_2960 id, net.minecraft.class_1865<?> type, String group,
            net.minecraft.class_1856 ingredient, net.minecraft.class_1792 result, int count, net.minecraft.class_8779 advancement) {
        this.comp_1230 = id;
        this.comp_1231 = type;
        this.comp_1947 = group;
        this.comp_1948 = ingredient;
        this.comp_1949 = result;
        this.comp_1950 = count;
        this.comp_1235 = advancement;
    }

    /** serializeRecipeData(json) */
    @Override
    public void method_10416(JsonObject json) {
        if (!comp_1947.isEmpty()) {
            json.addProperty("group", comp_1947);
        }
        json.add("ingredient", CbDataRecipes.ingredientJson(comp_1948, false));
        json.addProperty("result", net.minecraft.class_7923.field_41178.method_10221(comp_1949).toString());
        json.addProperty("count", comp_1950);
    }

    /** id() */
    @Override
    public net.minecraft.class_2960 comp_1230() {
        return comp_1230;
    }

    /** type() */
    @Override
    public net.minecraft.class_1865<?> comp_1231() {
        return comp_1231;
    }

    /** group() */
    public String comp_1947() {
        return comp_1947;
    }

    /** ingredient() */
    public net.minecraft.class_1856 comp_1948() {
        return comp_1948;
    }

    /** result() */
    public net.minecraft.class_1792 comp_1949() {
        return comp_1949;
    }

    /** count() */
    public int comp_1950() {
        return comp_1950;
    }

    /** advancement() */
    @Override
    public net.minecraft.class_8779 comp_1235() {
        return comp_1235;
    }

    /** the record's equality: an instance of this class with every component equal */
    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof SingleItemRecipeBuilderResult r
            && Objects.equals(comp_1230, r.comp_1230) && Objects.equals(comp_1231, r.comp_1231)
            && Objects.equals(comp_1947, r.comp_1947) && Objects.equals(comp_1948, r.comp_1948)
            && Objects.equals(comp_1949, r.comp_1949) && comp_1950 == r.comp_1950 && Objects.equals(comp_1235, r.comp_1235);
    }

    /** the record's hash: 31-fold over the components in order */
    @Override
    public int hashCode() {
        int h = Objects.hashCode(comp_1230);
        h = 31 * h + Objects.hashCode(comp_1231);
        h = 31 * h + Objects.hashCode(comp_1947);
        h = 31 * h + Objects.hashCode(comp_1948);
        h = 31 * h + Objects.hashCode(comp_1949);
        h = 31 * h + Integer.hashCode(comp_1950);
        return 31 * h + Objects.hashCode(comp_1235);
    }

    /**
     * the record's text as 1.20.2's game printed it: the class's runtime simple name (class_3982),
     * then the component names the record kept (Mojang's) with their values
     */
    @Override
    public String toString() {
        return "class_3982[id=" + comp_1230 + ", type=" + comp_1231 + ", group=" + comp_1947 + ", ingredient=" + comp_1948
            + ", result=" + comp_1949 + ", count=" + comp_1950 + ", advancement=" + comp_1235 + "]";
    }
}
