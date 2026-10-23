package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1720;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** AbstractFurnaceMenu.level (protected): the level a 1.21.1 recipeMatches on a furnace asks in (CbRestRecipeMenus) */
@Mixin(class_1720.class)
public interface CbRestFurnaceMenuAccessor {

    @Accessor("field_7822")
    class_1937 cb$level();
}
