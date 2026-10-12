package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbItemRecipes;

/** InventoryMenu.recipeMatches asks the recipe about the 2x2 grid through CbItemRecipes (see there) */
@Mixin(net.minecraft.class_1723.class)
public abstract class CbItemRecipeInventoryMenuMixin {

    @Redirect(method = "method_7652", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_8566;method_59961()Lnet/minecraft/class_9694;"))
    private net.minecraft.class_9694 cbItem$matchInput(net.minecraft.class_8566 grid) {
        return CbItemRecipes.cbCraftingInput(grid);
    }
}
