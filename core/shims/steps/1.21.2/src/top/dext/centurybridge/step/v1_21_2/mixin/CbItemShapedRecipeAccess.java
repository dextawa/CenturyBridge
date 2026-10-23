package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1869;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ShapedRecipe's result (CbItemRecipes) */
@Mixin(class_1869.class)
public interface CbItemShapedRecipeAccess {

    @Accessor("field_9053")
    net.minecraft.class_1799 cb$result();
}
