package top.dext.centurybridge.shims.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_8113;
import net.minecraft.class_8138;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyDisplayOrientation;

/**
 * 1.20.1's DisplayRenderer drew a FIXED-billboard display rotated by display.orientation(); 1.20.2
 * computes that rotation from the (interpolated) entity rotation itself. A converted display class
 * overriding orientation() (marked CbLegacyDisplayOrientation by an adapter hook) is drawn with its
 * override's answer again, uninterpolated as in 1.20.1; every other display is left to 1.20.2.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_8138.class)
public abstract class CbDisplayRendererOrientation {

    @Inject(method = "method_49053", at = @At("HEAD"), cancellable = true)
    private void cb$legacyOrientation(class_8113.class_8229 renderState, class_8113 display, float partialTick,
                                      Quaternionf quaternion, CallbackInfoReturnable<Quaternionf> cir) {
        if (display instanceof CbLegacyDisplayOrientation legacy
                && renderState.comp_1329() == class_8113.class_8114.field_42406) {
            cir.setReturnValue(quaternion.set(legacy.method_48863()));
        }
    }
}
