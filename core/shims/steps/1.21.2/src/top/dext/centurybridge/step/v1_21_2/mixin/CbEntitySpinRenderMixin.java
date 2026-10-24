package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10039;
import net.minecraft.class_1542;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_916;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntitySmall;

/**
 * 1.21.1's ItemEntityRenderer asked the item entity's getSpin(partialTick); 1.21.2's computes the static getSpin from
 * the render state. For a mod class overriding 1.21.1's, extraction keeps the override's answer on the state
 * (CbEntitySpinStateMixin) and rendering uses it.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_916.class)
public abstract class CbEntitySpinRenderMixin {

    @Inject(method = "method_62470", at = @At("RETURN"))
    private void cb$extract(class_1542 item, class_10039 state, float partialTick, CallbackInfo ci) {
        ((CbEntitySmall.SpinState) state).cb$spin(item instanceof CbEntitySmall.LegacySpin legacy
            ? legacy.method_27314(partialTick) : Float.NaN);
    }

    @Redirect(method = "method_3996", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1542;method_27314(FF)F"))
    private float cb$spin(float ageInTicks, float bobOffset, class_10039 state, class_4587 poseStack,
                         class_4597 buffers, int light) {
        float legacy = ((CbEntitySmall.SpinState) state).cb$spin();
        return Float.isNaN(legacy) ? class_1542.method_27314(ageInTicks, bobOffset) : legacy;
    }
}
