package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1734;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2371;
import net.minecraft.class_3218;
import net.minecraft.class_9694;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipeManager;

/**
 * ResultSlot.getRemainingItems(input, level) on a server level: 1.21.1's ResultSlot.onTake asked
 * RecipeManager.getRemainingItemsFor(CRAFTING, input, level), restored by CbItemRecipeManagerRestoredMixin; where that
 * answers otherwise than its body -- a mod's mixin on it -- its answer, else 1.21.2's own (CbItemRecipeManager.remainingRead).
 */
@Mixin(class_1734.class)
public abstract class CbItemResultSlotRemainingMixin {

    @Inject(method = "method_64650", at = @At("HEAD"), cancellable = true)
    private void cb$remainingFor(class_9694 input, class_1937 level, CallbackInfoReturnable<class_2371<class_1799>> cir) {
        if (level instanceof class_3218 server) {
            class_2371<class_1799> read = CbItemRecipeManager.remainingRead(server.method_64577(), input, level);
            if (read != null) {
                cir.setReturnValue(read);
            }
        }
    }
}
