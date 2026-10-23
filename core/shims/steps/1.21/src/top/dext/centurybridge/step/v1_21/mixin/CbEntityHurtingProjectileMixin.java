package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1668;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2489;
import net.minecraft.class_2499;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEntityProjectiles;

/**
 * AbstractHurtingProjectile and 1.20.6's power vector (CbEntityProjectiles): a projectile that has one flies
 * 1.20.6's way -- each tick its motion plus the vector, times the inertia; a deflection sets the vector from the
 * new motion (x 0.1 when attacked, x 0.05 when it bounced); the save keeps it under 1.20.6's key "power" and
 * reading a save that has it brings it back. A projectile without one is 1.21's alone.
 */
@Mixin(class_1668.class)
public abstract class CbEntityHurtingProjectileMixin implements CbEntityProjectiles.LegacyPower {

    @Unique
    private class_243 cb$power;

    @Shadow
    protected abstract float method_7466();

    @Shadow
    protected abstract float method_55583();

    @Override
    public class_243 cb$power() {
        return this.cb$power;
    }

    @Override
    public void cb$setPower(class_243 power) {
        this.cb$power = power;
    }

    @ModifyArg(method = "method_5773()V",
               at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1668;method_18799(Lnet/minecraft/class_243;)V"))
    private class_243 cb$legacyAcceleration(class_243 motion) {
        if (this.cb$power == null) {
            return motion;
        }
        class_1668 self = (class_1668) (Object) this;
        float inertia = self.method_5799() ? this.method_55583() : this.method_7466();
        return self.method_18798().method_1019(this.cb$power).method_1021(inertia);
    }

    @Inject(method = "method_59525(Lnet/minecraft/class_1297;Z)V", at = @At("TAIL"))
    private void cb$legacyDeflection(class_1297 deflector, boolean byAttack, CallbackInfo ci) {
        if (this.cb$power != null) {
            this.cb$power = ((class_1668) (Object) this).method_18798().method_1021(byAttack ? 0.1 : 0.05);
        }
    }

    @Inject(method = "method_5652(Lnet/minecraft/class_2487;)V", at = @At("TAIL"))
    private void cb$savePower(class_2487 tag, CallbackInfo ci) {
        if (this.cb$power != null) {
            class_2499 list = new class_2499();
            list.add(class_2489.method_23241(this.cb$power.field_1352));
            list.add(class_2489.method_23241(this.cb$power.field_1351));
            list.add(class_2489.method_23241(this.cb$power.field_1350));
            tag.method_10566("power", list);
        }
    }

    @Inject(method = "method_5749(Lnet/minecraft/class_2487;)V", at = @At("TAIL"))
    private void cb$readPower(class_2487 tag, CallbackInfo ci) {
        if (tag.method_10573("power", 9)) {
            class_2499 list = tag.method_10554("power", 6);
            if (list.size() == 3) {
                this.cb$power = new class_243(list.method_10611(0), list.method_10611(1), list.method_10611(2));
            }
        }
    }
}
