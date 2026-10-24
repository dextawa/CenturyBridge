package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1528;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityDragon;

/**
 * 1.21.1's wither model asked the wither's getHeadYRot(i) / getHeadXRot(i) for each side head; 1.21.2's renderer
 * copies getHeadYRots() / getHeadXRots() (no other caller): a mod wither overriding 1.21.1's answers them, one
 * entry per head of 1.21.2's arrays.
 */
@Mixin(class_1528.class)
public abstract class CbEntityWitherMixin implements CbEntityDragon.WitherHeads {

    @Shadow
    @Final
    private float[] field_7083;

    @Shadow
    @Final
    private float[] field_7084;

    @Override
    public float[] cb$yRotHeads() {
        return this.field_7083;
    }

    @Override
    public float[] cb$xRotHeads() {
        return this.field_7084;
    }

    @Inject(method = "method_61488", at = @At("HEAD"), cancellable = true)
    private void cb$headYRots(CallbackInfoReturnable<float[]> cir) {
        if (this instanceof CbEntityDragon.LegacyHeadYRot legacy) {
            float[] out = new float[this.field_7083.length];
            for (int i = 0; i < out.length; i++) {
                out[i] = legacy.method_6879(i);
            }
            cir.setReturnValue(out);
        }
    }

    @Inject(method = "method_61487", at = @At("HEAD"), cancellable = true)
    private void cb$headXRots(CallbackInfoReturnable<float[]> cir) {
        if (this instanceof CbEntityDragon.LegacyHeadXRot legacy) {
            float[] out = new float[this.field_7084.length];
            for (int i = 0; i < out.length; i++) {
                out[i] = legacy.method_6887(i);
            }
            cir.setReturnValue(out);
        }
    }
}
