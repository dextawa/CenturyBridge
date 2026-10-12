package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyEffectInstanceOverride;

/**
 * 1.20.1's MobEffectInstance.applyEffect(entity) back (s1misc), 1.20.1 body: while the instance
 * has duration left, its effect's applyEffectTick(entity, amplifier). 1.20.1's tick called it;
 * 1.20.2's tick calls applyEffectTick inline -- for an instance of a converted class overriding
 * applyEffect (CbLegacyEffectInstanceOverride) the tick calls the override again.
 */
@Mixin(class_1293.class)
public abstract class CbEffectInstanceLegacy {

    @Shadow
    @Final
    private class_1291 field_5896;

    @Shadow
    private int field_5893;

    @Shadow
    private boolean method_48562() {
        throw new AssertionError();
    }

    public void method_5589(class_1309 entity) {
        if (this.method_48562()) {
            this.field_5896.method_5572(entity, this.field_5893);
        }
    }

    @Redirect(method = "method_5585", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1291;method_5572(Lnet/minecraft/class_1309;I)V"))
    private void cb$applyEffect(class_1291 effect, class_1309 entity, int amplifier) {
        if ((Object) this instanceof CbLegacyEffectInstanceOverride legacy) {
            legacy.method_5589(entity);
        } else {
            effect.method_5572(entity, amplifier);
        }
    }
}
