package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_5.rt.CbItemTick;

/**
 * ItemStack.inventoryTick on the client: 1.21.5 asks the item only on the server, 1.21.4 asked it on both sides. A
 * 1.21.4 override of a player's stack is asked here with 1.21.4's index and selected flag (CbItemTick.clientTick);
 * 1.21.5's body runs unchanged.
 */
@Mixin(class_1799.class)
public abstract class CbItemStackTickMixin {

    @Inject(method = "method_7917(Lnet/minecraft/class_1937;Lnet/minecraft/class_1297;Lnet/minecraft/class_1304;)V", at = @At("TAIL"))
    private void cb$clientInventoryTick(class_1937 level, class_1297 entity, class_1304 slot, CallbackInfo ci) {
        CbItemTick.clientTick((class_1799) (Object) this, level, entity, slot);
    }
}
