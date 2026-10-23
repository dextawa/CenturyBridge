package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1685;
import net.minecraft.class_1799;
import net.minecraft.class_3218;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_9304;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEntityEnchanting;

/**
 * ThrownTrident and channeling (CbEntityEnchanting): for a 1.20.6 subclass that overrides isChanneling(), its
 * answer decides the strike as it did in 1.20.6 -- around 1.21's weapon effects of an entity hit and of a block
 * hit, and with 1.20.6's own strike afterwards where 1.21 has none to make. Any other trident is 1.21's alone.
 */
@Mixin(class_1685.class)
public abstract class CbEntityTridentMixin implements CbEntityEnchanting.TridentHit {

    @Unique
    private class_9304 cb$heldEnchantments;

    @Override
    public class_9304 cb$heldEnchantments() {
        return this.cb$heldEnchantments;
    }

    @Override
    public void cb$holdEnchantments(class_9304 enchantments) {
        this.cb$heldEnchantments = enchantments;
    }

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
        CbEntityEnchanting.beforeWeaponEffects((class_1685) (Object) this);
    }

    @Inject(method = "method_59956(Lnet/minecraft/class_3218;Lnet/minecraft/class_3965;Lnet/minecraft/class_1799;)V", at = @At("TAIL"))
    private void cb$afterBlockEffects(class_3218 level, class_3965 hit, class_1799 weapon, CallbackInfo ci) {
        CbEntityEnchanting.afterWeaponEffects((class_1685) (Object) this);
        CbEntityEnchanting.afterBlockHit((class_1685) (Object) this, level, hit.method_17777());
    }
}
