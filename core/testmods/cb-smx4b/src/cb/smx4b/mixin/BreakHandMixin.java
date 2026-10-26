package cb.smx4b.mixin;

import cb.smx4b.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.20.4 LivingEntity.broadcastBreakEvent(hand): counted, the hand noted */
@Mixin(net.minecraft.class_1309.class)
public abstract class BreakHandMixin {
    @Inject(method = "method_20236(Lnet/minecraft/class_1268;)V", at = @At("HEAD"), remap = false)
    private void cbsmx4b$broken(net.minecraft.class_1268 hand, CallbackInfo ci) {
        Driver.breakAsked++;
        Driver.breakHand = hand;
    }
}
