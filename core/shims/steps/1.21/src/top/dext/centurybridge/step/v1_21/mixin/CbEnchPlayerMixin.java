package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbEnchEffects;

/**
 * Player.attack's sweep: 1.21 runs the weapon's enchantment damage and post-attack effects on every
 * sweep target, 1.20.6 did neither; while the sweep targets are hurt, 1.20.6 mods' enchantments stay
 * out (CbEnchEffects.enterSweep). 1.21's own enchantments keep their 1.21 behaviour.
 */
@Mixin(net.minecraft.class_1657.class)
public abstract class CbEnchPlayerMixin {

    @Shadow
    protected abstract float method_59903(net.minecraft.class_1297 target, float base, net.minecraft.class_1282 source);

    /** the sweep target's damage (the attack's second getEnchantedDamage) */
    @Redirect(method = "method_7324", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1657;method_59903(Lnet/minecraft/class_1297;FLnet/minecraft/class_1282;)F", ordinal = 1))
    private float cbEnch$sweepDamage(net.minecraft.class_1657 self, net.minecraft.class_1297 target, float base, net.minecraft.class_1282 source) {
        CbEnchEffects.enterSweep();
        try {
            return this.method_59903(target, base, source);
        } finally {
            CbEnchEffects.exitSweep();
        }
    }

    /** the sweep target's post-attack effects (the attack's first doPostAttackEffects) */
    @Redirect(method = "method_7324", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1890;method_60107(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;Lnet/minecraft/class_1282;)V", ordinal = 0))
    private void cbEnch$sweepPostAttack(net.minecraft.class_3218 level, net.minecraft.class_1297 target, net.minecraft.class_1282 source) {
        CbEnchEffects.enterSweep();
        try {
            net.minecraft.class_1890.method_60107(level, target, source);
        } finally {
            CbEnchEffects.exitSweep();
        }
    }
}
