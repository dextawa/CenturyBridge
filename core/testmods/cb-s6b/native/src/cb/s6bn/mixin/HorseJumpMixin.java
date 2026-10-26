package cb.s6bn.mixin;

import cb.s6bn.Counter;
import net.minecraft.class_1496;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * native 1.21.6: the AbstractHorse methods the layer's jump-flag route sits in -- tickRidden HEAD / TAIL and its
 * LivingEntity.isJumping() call, executeRidersJump HEAD and its setDeltaMovement call
 */
@Mixin(class_1496.class)
public class HorseJumpMixin {
    @Inject(method = "method_49481(Lnet/minecraft/class_1657;Lnet/minecraft/class_243;)V", at = @At("HEAD"))
    private void cbS6bn$tickRiddenHead(class_1657 rider, class_243 input, CallbackInfo ci) {
        Counter.bump("cb.s6bn.tickRidden.head");
    }

    @Inject(method = "method_49481(Lnet/minecraft/class_1657;Lnet/minecraft/class_243;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1496;method_70673()Z"))
    private void cbS6bn$tickRiddenIsJumping(class_1657 rider, class_243 input, CallbackInfo ci) {
        Counter.bump("cb.s6bn.tickRidden.isJumping");
    }

    @Inject(method = "method_49481(Lnet/minecraft/class_1657;Lnet/minecraft/class_243;)V", at = @At("TAIL"))
    private void cbS6bn$tickRiddenTail(class_1657 rider, class_243 input, CallbackInfo ci) {
        Counter.bump("cb.s6bn.tickRidden.tail");
    }

    @Inject(method = "method_45343(FLnet/minecraft/class_243;)V", at = @At("HEAD"))
    private void cbS6bn$jumpHead(float strength, class_243 input, CallbackInfo ci) {
        Counter.bump("cb.s6bn.riderJump.head");
    }

    @Inject(method = "method_45343(FLnet/minecraft/class_243;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1496;method_18800(DDD)V", shift = At.Shift.AFTER))
    private void cbS6bn$jumpVelocity(float strength, class_243 input, CallbackInfo ci) {
        Counter.bump("cb.s6bn.riderJump.velocity");
    }
}
