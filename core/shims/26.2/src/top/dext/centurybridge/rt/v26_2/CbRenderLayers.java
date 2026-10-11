package top.dext.centurybridge.rt.v26_2;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import top.dext.centurybridge.facade.v26_2.class_4597;

/**
 * 1.20.1 feature renderers (FeatureRenderer, 26.x RenderLayer) over the submit pipeline.
 *
 * 1.20.1 LivingEntityRenderer drew each feature with render(matrices, buffers, light,
 * entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch); 26.x
 * calls submit(poseStack, collector, light, state, yRot, xRot). The state carries the
 * entity (CbRenderStateEntity) and the same numbers the 1.20.1 renderer handed the
 * feature (walk animation position and speed, age in ticks; yRot/xRot are the head yaw and
 * pitch); the buffer source records and is submitted afterwards, as for entity renderers.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbRenderLayers {

    /** submit(...), synthesized on a feature that overrides render (overrideHooks, bridge mode, guarded) */
    public static void submit(CbLegacyRenderLayer self, PoseStack poseStack, SubmitNodeCollector collector, int light,
                              EntityRenderState state, float yRot, float xRot) {
        Entity entity = ((CbRenderStateEntity) state).centurybridge$entity();
        if (entity == null) {
            return; // a state that did not come through the dispatcher: nothing to hand the old method
        }
        float tickDelta = ((CbRenderStateEntity) state).centurybridge$partialTick();
        float limbAngle = 0.0F;
        float limbDistance = 0.0F;
        if (state instanceof LivingEntityRenderState l) {
            limbAngle = l.walkAnimationPos;
            limbDistance = l.walkAnimationSpeed;
        }
        CbRecordingBuffers buffers = new CbRecordingBuffers();
        try {
            self.submit(poseStack, buffers, light, entity, limbAngle, limbDistance, tickDelta, state.ageInTicks, yRot, xRot);
        } finally {
            buffers.submitTo(collector);
        }
    }

    /**
     * FeatureRenderer.renderModel(model, texture, matrices, buffers, light, entity, r, g, b), as
     * 1.20.1 wrote it: the model drawn into the entity-cutout buffer of the texture, with the
     * entity's hurt overlay.
     */
    public static void method_23199(EntityModel<?> model, Identifier texture, PoseStack matrices, class_4597 buffers,
                                    int light, LivingEntity entity, float red, float green, float blue) {
        // 1.20.1 RenderLayer.getEntityCutoutNoCull, as the converter maps it everywhere else
        VertexConsumer vertices = buffers.getBuffer(ClientStatics.method_23578(texture));
        CbModels.method_2828(model, matrices, vertices, light, overlay(entity, 0.0F), red, green, blue, 1.0F);
    }

    /**
     * FeatureRenderer.renderCopy(contextModel, model, texture, matrices, buffers, light, entity,
     * limbAngle, limbDistance, age, headYaw, headPitch, tickDelta, r, g, b): an invisible entity
     * draws nothing; otherwise the model is animated as the context model is and drawn.
     */
    public static void method_23196(EntityModel<?> contextModel, EntityModel<?> model, Identifier texture, PoseStack matrices,
                                    class_4597 buffers, int light, LivingEntity entity, float limbAngle, float limbDistance,
                                    float age, float headYaw, float headPitch, float tickDelta, float red, float green, float blue) {
        if (entity.isInvisible()) {
            return;
        }
        if (model instanceof CbLegacyEntityModel legacy) {
            legacy.method_2816(entity, limbAngle, limbDistance, tickDelta);
            legacy.setupAnim(entity, limbAngle, limbDistance, age, headYaw, headPitch);
        }
        method_23199(model, texture, matrices, buffers, light, entity, red, green, blue);
    }

    /** LivingEntityRenderer.getOverlayCoords(entity, whiteOverlayProgress) as 1.20.1 computed it */
    public static int overlay(LivingEntity entity, float whiteOverlayProgress) {
        return OverlayTexture.pack(OverlayTexture.u(whiteOverlayProgress), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
    }

    private CbRenderLayers() {
    }
}
