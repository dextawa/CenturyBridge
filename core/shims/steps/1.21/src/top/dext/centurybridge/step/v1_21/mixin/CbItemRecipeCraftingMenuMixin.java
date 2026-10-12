package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbItemRecipes;

/**
 * CraftingMenu builds the CraftingInput a recipe is asked with from its grid; going through
 * CbItemRecipes.cbCraftingInput (the same input 1.21's asCraftInput makes) lets a 1.20.6 recipe that
 * 1.21 asks see the whole grid again (see CbItemRecipes).
 */
@Mixin(net.minecraft.class_1714.class)
public abstract class CbItemRecipeCraftingMenuMixin {

    /** slotChangedCraftingGrid: the grid the result is looked up for */
    @Redirect(method = "method_17399", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_8566;method_59961()Lnet/minecraft/class_9694;"))
    private static net.minecraft.class_9694 cbItem$gridInput(net.minecraft.class_8566 grid) {
        return CbItemRecipes.cbCraftingInput(grid);
    }

    /** recipeMatches: the recipe book's check against the grid */
    @Redirect(method = "method_7652", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_8566;method_59961()Lnet/minecraft/class_9694;"))
    private net.minecraft.class_9694 cbItem$matchInput(net.minecraft.class_8566 grid) {
        return CbItemRecipes.cbCraftingInput(grid);
    }
}
