package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.Optional;
import net.minecraft.class_10591;
import net.minecraft.class_1799;
import net.minecraft.class_1856;

/**
 * 1.21.4's SmithingRecipe.baseIngredient() answered an Optional&lt;Ingredient&gt; (empty: the base slot must be
 * empty, Ingredient.testOptionalIngredient); 1.21.5's answers the Ingredient, and SmithingTransformRecipe takes it
 * so, with its result as a TransmuteResult instead of an ItemStack. A 1.21.5 Ingredient is never empty (its
 * constructor refuses an empty item set), so the base a 1.21.5 recipe answers is always present, and an empty
 * 1.21.4 base has no 1.21.5 value: the recipe 1.21.4 described (one matching only an empty base slot) cannot be
 * expressed at 1.21.5, and the conversion says so instead of inventing a base.
 */
public final class CbItemSmithing {

    private CbItemSmithing() {
    }

    /** a 1.21.4 caller's baseIngredient(): 1.21.5's base, present */
    public static Optional<class_1856> base(class_1856 base) {
        return Optional.ofNullable(base);
    }

    /** a 1.21.4 override's (or constructor argument's) base as 1.21.5 takes it */
    public static class_1856 baseOf(Optional<class_1856> base) {
        if (base == null || base.isEmpty()) {
            throw new IllegalArgumentException("CenturyBridge: a 1.21.4 smithing recipe with an empty base ingredient"
                + " (matching only an empty base slot) has no 1.21.5 form: 1.21.5 smithing recipes always take a base");
        }
        return base.get();
    }

    /**
     * 1.21.4's SmithingTransformRecipe result stack as 1.21.5's TransmuteResult: the same item, count and component
     * patch. 1.21.4's assemble gave base.transmuteCopy(result item, result count) with the result's component patch
     * applied; TransmuteResult.apply does exactly that.
     */
    public static class_10591 result(class_1799 result) {
        return new class_10591(result.method_41409(), result.method_7947(), result.method_57380());
    }
}
