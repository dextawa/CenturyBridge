package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1685;
import net.minecraft.class_1799;
import net.minecraft.class_3218;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEntityEnchanting;

/**
 * The injectors into 1.21's own ThrownTrident methods that go with {@link CbEntityTridentMixin} (its restored 1.20.6
 * isChanneling stays there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.6 injectors can anchor inside it):
 * around 1.21's weapon effects of an entity hit and of a block hit the restored isChanneling is asked
 * (CbEntityEnchanting), and 1.20.6's own strike follows where it says yes.
 */
@Mixin(class_1685.class)
public abstract class CbEntityTridentMixinHooks {

    @Inject(method = "method_7454(Lnet/minecraft/class_3966;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1890;method_60619(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;Lnet/minecraft/class_1282;Lnet/minecraft/class_1799;)V"))
    private void cb$beforeAttackEffects(class_3966 hit, CallbackInfo ci) {
        CbEntityEnchanting.beforeWeaponEffects((class_1685) (Object) this);
    }

    @Inject(method = "method_7454(Lnet/minecraft/class_3966;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1890;method_60619(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;Lnet/minecraft/class_1282;Lnet/minecraft/class_1799;)V", shift = At.Shift.AFTER))
    private void cb$afterAttackEffects(class_3966 hit, CallbackInfo ci) {
        CbEntityEnchanting.afterWeaponEffects((class_1685) (Object) this);
    }

    @Inject(method = "method_7454(Lnet/minecraft/class_3966;)V", at = @At("TAIL"))
    private void cb$legacyStrike(class_3966 hit, CallbackInfo ci) {
        CbEntityEnchanting.afterEntityHit((class_1685) (Object) this, hit.method_17782());
    }

    @Inject(method = "method_59956(Lnet/minecraft/class_3218;Lnet/minecraft/class_3965;Lnet/minecraft/class_1799;)V", at = @At("HEAD"))
    private void cb$beforeBlockEffects(class_3218 level, class_3965 hit, class_1799 weapon, CallbackInfo ci) {
        CbEntityEnchanting.beforeBlockEffects((class_1685) (Object) this, level, hit.method_17777());
    }

    @Inject(method = "method_59956(Lnet/minecraft/class_3218;Lnet/minecraft/class_3965;Lnet/minecraft/class_1799;)V", at = @At("TAIL"))
    private void cb$afterBlockEffects(class_3218 level, class_3965 hit, class_1799 weapon, CallbackInfo ci) {
        CbEntityEnchanting.afterWeaponEffects((class_1685) (Object) this);
        CbEntityEnchanting.afterBlockHit((class_1685) (Object) this, level, hit.method_17777());
    }
}
