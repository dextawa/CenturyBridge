package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_8060;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** SmithingTransformRecipe's result (CbItemRecipes) */
@Mixin(class_8060.class)
public interface CbItemSmithingTransformAccess {

    @Accessor("field_42033")
    net.minecraft.class_1799 cb$result();
}
