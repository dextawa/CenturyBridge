package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBoats;

/** the boat AbstractBoatRenderer.render is drawing, for a 1.21.1 BoatRenderer's per-variant models (CbRenderBoatRendererMixin) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10262.class)
public abstract class CbRenderBoatDrawMixin {

    @Inject(method = "method_64519", at = @At("HEAD"))
    private void cb$drawing(net.minecraft.class_10004 state, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light, CallbackInfo ci) {
        CbRenderBoats.drawing(state);
    }

    @Inject(method = "method_64519", at = @At("RETURN"))
    private void cb$drawn(net.minecraft.class_10004 state, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light, CallbackInfo ci) {
        CbRenderBoats.drawn();
    }
}
