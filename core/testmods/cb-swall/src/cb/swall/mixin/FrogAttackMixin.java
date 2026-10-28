package cb.swall.mixin;

import cb.swall.SwallTestMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21.11's private FrogAi.canAttack(frog) (class_7107.method_41375), as 1.21.11-era mods mixed it: counts the frog brain's
 * attack checks. On 26.1 the selector follows canAttack(Mob) (callReshapes + mixinTargets narrow: the handler still takes
 * the frog every 26.1 caller passes).
 */
@Mixin(net.minecraft.class_7107.class)
public abstract class FrogAttackMixin {
    @Inject(method = "method_41375(Lnet/minecraft/class_7102;)Z", at = @At("HEAD"), remap = false)
    private static void cbswall$countAttackCheck(net.minecraft.class_7102 frog, CallbackInfoReturnable<Boolean> cir) {
        if (frog != null) {
            SwallTestMod.FROG_ATTACK_CALLS.incrementAndGet();
        }
    }
}
