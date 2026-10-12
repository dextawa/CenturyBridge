package top.dext.centurybridge.step.v1_20_3.facade;

import com.google.gson.JsonObject;
import java.util.Objects;
import top.dext.centurybridge.step.v1_20_3.rt.CbDataRecipes;

/**
 * Facade for 1.20.2 SmithingTrimRecipeBuilder.Result (class_8076$class_8077), gone in 1.20.3: the
 * 1.20.2 record whole -- its JSON (template, base and addition, each allowed empty), components,
 * record equality, hash and text -- as a plain class (a facade's members must be public; the 1.20.1
 * class was not final).
 */
public class SmithingTrimRecipeBuilderResult implements FinishedRecipe {

    public final net.minecraft.class_2960 comp_1230;
    public final net.minecraft.class_1865<?> comp_1231;
    public final net.minecraft.class_1856 comp_1232;
    public final net.minecraft.class_1856 comp_1233;
    public final net.minecraft.class_1856 comp_1234;
    public final net.minecraft.class_8779 comp_1235;

    public SmithingTrimRecipeBuilderResult(net.minecraft.class_2960 id, net.minecraft.class_1865<?> type,
            net.minecraft.class_1856 template, net.minecraft.class_1856 base, net.minecraft.class_1856 addition,
            net.minecraft.class_8779 advancement) {
        this.comp_1230 = id;
        this.comp_1231 = type;
        this.comp_1232 = template;
        this.comp_1233 = base;
        this.comp_1234 = addition;
        this.comp_1235 = advancement;
    }

    /** serializeRecipeData(json) */
    @Override
    public void method_10416(JsonObject json) {
        json.add("template", CbDataRecipes.ingredientJson(comp_1232, true));
        json.add("base", CbDataRecipes.ingredientJson(comp_1233, true));
        json.add("addition", CbDataRecipes.ingredientJson(comp_1234, true));
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

    /** template() */
    public net.minecraft.class_1856 comp_1232() {
        return comp_1232;
    }

    /** base() */
    public net.minecraft.class_1856 comp_1233() {
        return comp_1233;
    }

    /** addition() */
    public net.minecraft.class_1856 comp_1234() {
        return comp_1234;
    }

    /** advancement() */
    @Override
    public net.minecraft.class_8779 comp_1235() {
        return comp_1235;
    }

    /** the record's equality: an instance of this class with every component equal */
    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof SmithingTrimRecipeBuilderResult r
            && Objects.equals(comp_1230, r.comp_1230) && Objects.equals(comp_1231, r.comp_1231)
            && Objects.equals(comp_1232, r.comp_1232) && Objects.equals(comp_1233, r.comp_1233)
            && Objects.equals(comp_1234, r.comp_1234) && Objects.equals(comp_1235, r.comp_1235);
    }

    /** the record's hash: 31-fold over the components in order */
    @Override
    public int hashCode() {
        int h = Objects.hashCode(comp_1230);
        h = 31 * h + Objects.hashCode(comp_1231);
        h = 31 * h + Objects.hashCode(comp_1232);
        h = 31 * h + Objects.hashCode(comp_1233);
        h = 31 * h + Objects.hashCode(comp_1234);
        return 31 * h + Objects.hashCode(comp_1235);
    }

    /**
     * the record's text as 1.20.2's game printed it: the class's runtime simple name (class_8077),
     * then the component names the record kept (Mojang's) with their values
     */
    @Override
    public String toString() {
        return "class_8077[id=" + comp_1230 + ", type=" + comp_1231 + ", template=" + comp_1232 + ", base=" + comp_1233
            + ", addition=" + comp_1234 + ", advancement=" + comp_1235 + "]";
    }
}
