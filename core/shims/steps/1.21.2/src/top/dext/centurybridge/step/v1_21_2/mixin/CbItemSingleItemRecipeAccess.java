package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_3972;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** SingleItemRecipe's result (CbItemRecipes) */
@Mixin(class_3972.class)
public interface CbItemSingleItemRecipeAccess {

    @Accessor("field_17643")
    net.minecraft.class_1799 cb$result();
}
