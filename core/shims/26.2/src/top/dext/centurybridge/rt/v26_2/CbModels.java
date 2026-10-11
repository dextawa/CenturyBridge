package top.dext.centurybridge.rt.v26_2;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;

/**
 * 1.20.1 entity models over 26.x's state-driven models.
 *
 * 1.20.1 LivingEntityRenderer.render called model.animateModel(entity, limbAngle,
 * limbDistance, tickDelta) and then model.setAngles(entity, limbAngle, limbDistance,
 * animationProgress, headYaw, headPitch), and drew with model.render(matrices, vertices,
 * light, overlay, r, g, b, a). 26.x calls model.setupAnim(state) and renderToBuffer at draw
 * time. The state carries the same numbers, computed the same way (walk animation position
 * and speed, age in ticks, net head yaw, head pitch), and its entity (CbRenderStateEntity).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbModels {

    /** setupAnim(state), synthesized on a model that overrides setAngles (guarded) */
    public static void setupAnim(CbLegacyEntityModel self, Object state) {
        // what Model.setupAnim does: back to the layer definition's pose, as every frame starts
        ((Model<?>) self).resetPose();
        if (!(state instanceof EntityRenderState s)) {
            return;
        }
        Entity entity = ((CbRenderStateEntity) s).centurybridge$entity();
        if (entity == null) {
            return;
        }
        float tickDelta = ((CbRenderStateEntity) s).centurybridge$partialTick();
        if (s instanceof LivingEntityRenderState l) {
            self.method_2816(entity, l.walkAnimationPos, l.walkAnimationSpeed, tickDelta);
            self.setupAnim(entity, l.walkAnimationPos, l.walkAnimationSpeed, l.ageInTicks, l.yRot, l.xRot);
        } else {
            self.method_2816(entity, 0.0F, 0.0F, tickDelta);
            self.setupAnim(entity, 0.0F, 0.0F, s.ageInTicks, 0.0F, 0.0F);
        }
    }

    /**
     * Model.render(matrices, vertices, light, overlay, r, g, b, a) called on a model (an
     * instance redirect): a 1.20.1 model's own render, or a vanilla one drawn the 26.x way
     * with the color packed as 26.x takes it.
     */
    public static void method_2828(Model<?> self, PoseStack matrices, VertexConsumer vertices, int light, int overlay,
                                   float red, float green, float blue, float alpha) {
        if (self instanceof CbLegacyModelRender legacy) {
            legacy.method_2828(matrices, vertices, light, overlay, red, green, blue, alpha);
        } else {
            self.renderToBuffer(matrices, vertices, light, overlay, argb(red, green, blue, alpha));
        }
    }

    /** 26.x's packed color as 1.20.1's four floats */
    public static float[] rgba(int argb) {
        return new float[] {(argb >> 16 & 0xFF) / 255.0F, (argb >> 8 & 0xFF) / 255.0F,
            (argb & 0xFF) / 255.0F, (argb >>> 24) / 255.0F};
    }

    public static int argb(float r, float g, float b, float a) {
        return (Math.round(a * 255.0F) & 0xFF) << 24 | (Math.round(r * 255.0F) & 0xFF) << 16
            | (Math.round(g * 255.0F) & 0xFF) << 8 | Math.round(b * 255.0F) & 0xFF;
    }

    /**
     * The root a 1.20.1 model's super() is given when its constructor was handed none: 26.x
     * draws a model by its root, and this one has no parts -- the model's own render still
     * draws its own parts (ModelRenderBridge), nothing is drawn twice.
     */
    public static ModelPart emptyRoot() {
        return new ModelPart(List.of(), Map.of());
    }

    private CbModels() {
    }
}
