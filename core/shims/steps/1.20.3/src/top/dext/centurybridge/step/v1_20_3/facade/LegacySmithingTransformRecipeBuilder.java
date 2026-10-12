package top.dext.centurybridge.step.v1_20_3.facade;

import top.dext.centurybridge.step.v1_20_3.rt.CbDataRecipes;

/**
 * SmithingTransformRecipeBuilder with its 1.20.2 constructor, which took the recipe serializer
 * (1.20.3's builder always saves a vanilla SmithingTransformRecipe). A 1.20.2 {@code new
 * SmithingTransformRecipeBuilder(serializer, ..)} builds one of these (ctorRedirects), a 1.20.2
 * subclass extends it (superRenames). It saves as 1.20.2's did: vanilla's save does the validation
 * and builds the advancement exactly as 1.20.2's (the same steps), and what it saves is 1.20.2's
 * Result -- naming the given serializer, its JSON the template, base, addition and result -- handed
 * to the output the way 1.20.2 handed a FinishedRecipe ({@link CbDataRecipes#finishing}).
 */
public class LegacySmithingTransformRecipeBuilder extends net.minecraft.class_8074 {

    /** type: the serializer the 1.20.2 builder was given */
    public final net.minecraft.class_1865<?> field_42096;
    private final net.minecraft.class_1856 cb$template;
    private final net.minecraft.class_1856 cb$base;
    private final net.minecraft.class_1856 cb$addition;
    private final net.minecraft.class_1792 cb$result;

    public LegacySmithingTransformRecipeBuilder(net.minecraft.class_1865<?> serializer, net.minecraft.class_1856 template,
            net.minecraft.class_1856 base, net.minecraft.class_1856 addition, net.minecraft.class_7800 category,
            net.minecraft.class_1792 result) {
        super(template, base, addition, category, result);
        this.field_42096 = serializer;
        this.cb$template = template;
        this.cb$base = base;
        this.cb$addition = addition;
        this.cb$result = result;
    }

    /** save(output, id) */
    @Override
    public void method_48537(net.minecraft.class_8790 output, net.minecraft.class_2960 id) {
        super.method_48537(CbDataRecipes.finishing(output, (recipeId, advancement) -> new SmithingTransformRecipeBuilderResult(
            recipeId, field_42096, cb$template, cb$base, cb$addition, cb$result, advancement)), id);
    }
}
