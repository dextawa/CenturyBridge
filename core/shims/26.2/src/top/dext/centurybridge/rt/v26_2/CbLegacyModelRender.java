package top.dext.centurybridge.rt.v26_2;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * A 1.20.1 model's own render(matrices, vertices, light, overlay, r, g, b, a) -- abstract in
 * 1.20.1, so every concrete mod model has one. 26.x renders a model by its root part and
 * made renderToBuffer final; ModelRenderBridge hands the call to this instead, as 1.20.1 did.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbLegacyModelRender {

    void method_2828(PoseStack matrices, VertexConsumer vertices, int light, int overlay,
                     float red, float green, float blue, float alpha);
}
