package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbItemRecipes;

/**
 * ResultSlot.onTake asks the recipe for its leftovers with the positioned input of the grid; through
 * CbItemRecipes.cbPositioned the input remembers the grid and its offset (see CbItemRecipes).
 */
@Mixin(net.minecraft.class_1734.class)
public abstract class CbItemRecipeResultSlotMixin {

    @Redirect(method = "method_7667", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_8566;method_60501()Lnet/minecraft/class_9694$class_9765;"))
    private net.minecraft.class_9694.class_9765 cbItem$takeInput(net.minecraft.class_8566 grid) {
        return CbItemRecipes.cbPositioned(grid);
    }
}
