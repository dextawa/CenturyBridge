package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_1496;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.5's uses of the restored AbstractHorse jump flag (CbHorseJumpRestoredMixin) on 1.21.6's paths (javap of both):
 * executeRidersJump set it right after the jump velocity; tickRidden, on landing, cleared it and jumped again only when
 * isJumping() answered false. 1.21.6's code runs as it is (its own isJumping check, a native 1.21.6 mod's mixins);
 * the flag is set and cleared at 1.21.5's points, and only when the restored isJumping() answers true right after it
 * was cleared -- a converted mod overriding or mixing it -- does tickRidden take 1.21.5's no-jump path (the pending
 * jump scale reset to 0, the rest of the method being exactly that).
 */
@Mixin(class_1496.class)
public abstract class CbHorseJumpRouteMixin {

    @Shadow
    protected float field_6976;

    @Inject(method = "method_45343(FLnet/minecraft/class_243;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1496;method_18800(DDD)V", shift = At.Shift.AFTER))
    private void cb$legacyJumpFlagSet(float strength, net.minecraft.class_243 input, CallbackInfo ci) {
        ((top.dext.centurybridge.step.v1_21_6.rt.CbHorses.LegacyJump) (Object) this).method_6758(true);
    }

    @Inject(method = "method_49481(Lnet/minecraft/class_1657;Lnet/minecraft/class_243;)V",
        at = @At(value = "FIELD", target = "Lnet/minecraft/class_1496;field_6976:F", opcode = Opcodes.GETFIELD, ordinal = 0),
        cancellable = true)
    private void cb$legacyJumpFlagLanding(net.minecraft.class_1657 rider, net.minecraft.class_243 input, CallbackInfo ci) {
        top.dext.centurybridge.step.v1_21_6.rt.CbHorses.LegacyJump self = (top.dext.centurybridge.step.v1_21_6.rt.CbHorses.LegacyJump) (Object) this;
        self.method_6758(false);
        if (self.method_6763()) {
            this.field_6976 = 0.0F;
            ci.cancel();
        }
    }
}
