package top.dext.centurybridge.shims.v26_2;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v26_2.CbLegacyModelRender;
import top.dext.centurybridge.rt.v26_2.CbModels;

/**
 * A 1.20.1 model drew itself: render(matrices, vertices, light, overlay, r, g, b, a) was
 * abstract, every model wrote one (which parts, in which order, at which scale). 26.x made
 * renderToBuffer final and draws the root; for a 1.20.1 model the model's own render runs
 * instead, as it did -- the parts it poses in setAngles are the parts it draws.
 */
@Mixin(Model.class)
public abstract class ModelRenderBridge {

    @Inject(method = "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
            at = @At("HEAD"), cancellable = true)
    private void centurybridge$legacyRender(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color,
                                           CallbackInfo ci) {
        if ((Object) this instanceof CbLegacyModelRender legacy) {
            float[] c = CbModels.rgba(color);
            legacy.method_2828(matrices, vertices, light, overlay, c[0], c[1], c[2], c[3]);
            ci.cancel();
        }
    }
}
