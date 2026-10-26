package cb.smx4bn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.20.5 LivingEntity.broadcastBreakEvent(slot), which ItemStack.hurtAndBreak calls when the item breaks */
@Mixin(net.minecraft.class_1309.class)
public abstract class NativeBreakSlotMixin {
    @Inject(method = "method_20235(Lnet/minecraft/class_1304;)V", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$broken(net.minecraft.class_1304 slot, CallbackInfo ci) {
        cb.smx4bn.Driver.breakSlot++;
    }
}
