package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1867;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ShapelessRecipe's result and ingredients (CbItemRecipes) */
@Mixin(class_1867.class)
public interface CbItemShapelessRecipeAccess {

    @Accessor("field_9050")
    net.minecraft.class_1799 cb$result();

    @Accessor("field_9047")
    java.util.List<net.minecraft.class_1856> cb$ingredients();
}
