package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_5134;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Player's spin attack: 1.21 attacks during a spin with the damage the spin was started with, 1.20.6 with the
 * ATTACK_DAMAGE attribute as at any other time. A spin started through 1.20.6's startAutoSpinAttack(ticks)
 * carries CbEntityLiving.LEGACY_SPIN_DAMAGE (NaN); for such a spin each attack takes the attribute's value as
 * it stands, as 1.20.6's attack read it. A spin 1.21 starts (the trident's) is untouched.
 */
@Mixin(class_1657.class)
public abstract class CbEntitySpinAttackMixin extends class_1309 {

    @Unique
    private boolean cb$legacySpin;

    protected CbEntitySpinAttackMixin(class_1299<? extends class_1309> type, class_1937 level) {
        super(type, level);
    }

    @Inject(method = "method_40126(IFLnet/minecraft/class_1799;)V", at = @At("HEAD"))
    private void cb$spinStarted(int ticks, float damage, class_1799 stack, CallbackInfo ci) {
        this.cb$legacySpin = Float.isNaN(damage);
    }

    @Inject(method = "method_7324(Lnet/minecraft/class_1297;)V", at = @At("HEAD"))
    private void cb$legacySpinDamage(class_1297 target, CallbackInfo ci) {
        if (this.cb$legacySpin && this.method_6123()) {
            this.field_51569 = (float) this.method_45325(class_5134.field_23721);
        }
    }
}
