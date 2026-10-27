package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.Optional;
import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_10591;
import net.minecraft.class_8060;

/**
 * 1.21.4's smithing recipe API on 1.21.5 (javap -c -p both releases, class_8059 / class_8060 / class_10591).
 *
 * 1.21.4: SmithingRecipe.baseIngredient() answered Optional<Ingredient> (an empty one matched an empty base slot,
 * Ingredient.testOptionalIngredient); SmithingTransformRecipe held its result as an ItemStack and assembled
 * base.transmuteCopy(result.item, result.count) with result.componentsPatch applied.
 * 1.21.5: baseIngredient() answers an Ingredient (every smithing recipe has a base); the transform result is a
 * TransmuteResult(item holder, count, components patch) whose apply(base) does those same three steps.
 */
public final class CbSmithing {
    private CbSmithing() {
    }

    /** 1.21.5's base ingredient as 1.21.4's Optional */
    public static Optional<class_1856> some(class_1856 ingredient) {
        return Optional.ofNullable(ingredient);
    }

    /**
     * 1.21.4's Optional base ingredient as 1.21.5's: present -> the ingredient. 1.21.5 has no smithing recipe without
     * a base (and no empty Ingredient); a 1.21.4 recipe with an empty base is refused (IllegalArgumentException
     * naming the recipe kind), a documented loss.
     */
    public static class_1856 base(Optional<class_1856> base) {
        return base.orElseThrow(() -> new IllegalArgumentException(
            "1.21.5 smithing recipes need a base ingredient; a 1.21.4 recipe without one has no 1.21.5 form"));
    }

    /** a 1.21.4 transform result stack as 1.21.5's TransmuteResult: its item, count and components patch */
    public static class_10591 result(class_1799 result) {
        return new class_10591(result.method_41409(), result.method_7947(), result.method_57380());
    }

    /** 1.21.4's SmithingTransformRecipe(template, base, addition, result stack) as 1.21.5's */
    public static class_8060 newTransform(Optional<class_1856> template, Optional<class_1856> base,
            Optional<class_1856> addition, class_1799 result) {
        return new class_8060(template, base(base), addition, result(result));
    }
}
