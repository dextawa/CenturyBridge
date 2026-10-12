package top.dext.centurybridge.step.v1_20_3.facade;

import com.google.gson.JsonObject;
import java.util.Objects;
import top.dext.centurybridge.step.v1_20_3.rt.CbDataRecipes;

/**
 * Facade for 1.20.2 SmithingTransformRecipeBuilder.Result (class_8074$class_8075), gone in 1.20.3:
 * the 1.20.2 record whole -- its JSON (template, base and addition, each allowed empty, and the
 * result item), components, record equality, hash and text -- as a plain class (a facade's members
 * must be public; the 1.20.1 class was not final).
 */
public class SmithingTransformRecipeBuilderResult implements FinishedRecipe {

    public final net.minecraft.class_2960 comp_1230;
    public final net.minecraft.class_1865<?> comp_1231;
    public final net.minecraft.class_1856 comp_1224;
    public final net.minecraft.class_1856 comp_1225;
    public final net.minecraft.class_1856 comp_1226;
    public final net.minecraft.class_1792 comp_1227;
    public final net.minecraft.class_8779 comp_1235;

    public SmithingTransformRecipeBuilderResult(net.minecraft.class_2960 id, net.minecraft.class_1865<?> type,
            net.minecraft.class_1856 template, net.minecraft.class_1856 base, net.minecraft.class_1856 addition,
            net.minecraft.class_1792 result, net.minecraft.class_8779 advancement) {
        this.comp_1230 = id;
        this.comp_1231 = type;
        this.comp_1224 = template;
        this.comp_1225 = base;
        this.comp_1226 = addition;
        this.comp_1227 = result;
        this.comp_1235 = advancement;
    }

    /** serializeRecipeData(json) */
    @Override
    public void method_10416(JsonObject json) {
        json.add("template", CbDataRecipes.ingredientJson(comp_1224, true));
        json.add("base", CbDataRecipes.ingredientJson(comp_1225, true));
        json.add("addition", CbDataRecipes.ingredientJson(comp_1226, true));
        JsonObject result = new JsonObject();
        result.addProperty("item", net.minecraft.class_7923.field_41178.method_10221(comp_1227).toString());
        json.add("result", result);
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
    public net.minecraft.class_1856 comp_1224() {
        return comp_1224;
    }

    /** base() */
    public net.minecraft.class_1856 comp_1225() {
        return comp_1225;
    }

    /** addition() */
    public net.minecraft.class_1856 comp_1226() {
        return comp_1226;
    }

    /** result() */
    public net.minecraft.class_1792 comp_1227() {
        return comp_1227;
    }

    /** advancement() */
    @Override
    public net.minecraft.class_8779 comp_1235() {
        return comp_1235;
    }

    /** the record's equality: an instance of this class with every component equal */
    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof SmithingTransformRecipeBuilderResult r
            && Objects.equals(comp_1230, r.comp_1230) && Objects.equals(comp_1231, r.comp_1231)
            && Objects.equals(comp_1224, r.comp_1224) && Objects.equals(comp_1225, r.comp_1225)
            && Objects.equals(comp_1226, r.comp_1226) && Objects.equals(comp_1227, r.comp_1227)
            && Objects.equals(comp_1235, r.comp_1235);
    }

    /** the record's hash: 31-fold over the components in order */
    @Override
    public int hashCode() {
        int h = Objects.hashCode(comp_1230);
        h = 31 * h + Objects.hashCode(comp_1231);
        h = 31 * h + Objects.hashCode(comp_1224);
        h = 31 * h + Objects.hashCode(comp_1225);
        h = 31 * h + Objects.hashCode(comp_1226);
        h = 31 * h + Objects.hashCode(comp_1227);
        return 31 * h + Objects.hashCode(comp_1235);
    }

    /**
     * the record's text as 1.20.2's game printed it: the class's runtime simple name (class_8075),
     * then the component names the record kept (Mojang's) with their values
     */
    @Override
    public String toString() {
        return "class_8075[id=" + comp_1230 + ", type=" + comp_1231 + ", template=" + comp_1224 + ", base=" + comp_1225
            + ", addition=" + comp_1226 + ", result=" + comp_1227 + ", advancement=" + comp_1235 + "]";
    }
}
