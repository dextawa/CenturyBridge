package top.dext.centurybridge.step.v1_20_3.facade;

import top.dext.centurybridge.step.v1_20_3.rt.CbDataRecipes;

/**
 * SmithingTrimRecipeBuilder with its 1.20.2 constructor, which took the recipe serializer (1.20.3's
 * builder always saves a vanilla SmithingTrimRecipe): see LegacySmithingTransformRecipeBuilder. What
 * it saves is 1.20.2's Result, naming the given serializer, its JSON the template, base and addition.
 */
public class LegacySmithingTrimRecipeBuilder extends net.minecraft.class_8076 {

    /** type: the serializer the 1.20.2 builder was given */
    public final net.minecraft.class_1865<?> field_42102;
    private final net.minecraft.class_1856 cb$template;
    private final net.minecraft.class_1856 cb$base;
    private final net.minecraft.class_1856 cb$addition;

    public LegacySmithingTrimRecipeBuilder(net.minecraft.class_1865<?> serializer, net.minecraft.class_7800 category,
            net.minecraft.class_1856 template, net.minecraft.class_1856 base, net.minecraft.class_1856 addition) {
        super(category, template, base, addition);
        this.field_42102 = serializer;
        this.cb$template = template;
        this.cb$base = base;
        this.cb$addition = addition;
    }

    /** save(output, id) */
    @Override
    public void method_48542(net.minecraft.class_8790 output, net.minecraft.class_2960 id) {
        super.method_48542(CbDataRecipes.finishing(output, (recipeId, advancement) -> new SmithingTrimRecipeBuilderResult(
            recipeId, field_42102, cb$template, cb$base, cb$addition, advancement)), id);
    }
}
