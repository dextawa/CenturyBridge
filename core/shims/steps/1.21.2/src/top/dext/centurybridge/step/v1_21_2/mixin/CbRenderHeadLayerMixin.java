package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderHeads;

/**
 * 1.21.1 CustomHeadLayer's three scales on 1.21.2's layer (see CbRenderHeads), and 1.21.1 Fabric API's head-item
 * check: the layer draws nothing when the head item's 1.21.1 ArmorRenderer says not to (CbFapiRender.hidesHeadItem).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_976.class)
public abstract class CbRenderHeadLayerMixin implements CbRenderHeads.Scaled {

    @Unique
    private float[] cb$scale;

    @Override
    public void cb$setScale(float x, float y, float z) {
        this.cb$scale = new float[] {x, y, z};
    }

    @Redirect(method = "method_17159", require = 1, at = @At(value = "INVOKE", ordinal = 0,
        target = "Lnet/minecraft/class_4587;method_22905(FFF)V"))
    private void cb$scale(net.minecraft.class_4587 poseStack, float x, float y, float z) {
        float[] legacy = this.cb$scale;
        if (legacy != null) {
            poseStack.method_22905(legacy[0], legacy[1], legacy[2]);
        } else {
            poseStack.method_22905(x, y, z);
        }
    }

    @Inject(method = "method_17159", at = @At("HEAD"), cancellable = true)
    private void cb$legacyArmorHead(net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light,
                                    net.minecraft.class_10042 state, float yaw, float pitch, CallbackInfo ci) {
        if (top.dext.centurybridge.step.v1_21_2.rt.CbFapiRender.hidesHeadItem(state)) {
            ci.cancel();
        }
    }
}
