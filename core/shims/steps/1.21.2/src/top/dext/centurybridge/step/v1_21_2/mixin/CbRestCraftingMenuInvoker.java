package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1657;
import net.minecraft.class_9884;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** AbstractCraftingMenu.owner() (protected): the level a 1.21.1 recipeMatches on a crafting grid asks in (CbRestRecipeMenus) */
@Mixin(class_9884.class)
public interface CbRestCraftingMenuInvoker {

    @Invoker("method_61631")
    class_1657 cb$owner();
}
