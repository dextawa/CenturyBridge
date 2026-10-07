package top.dext.centurybridge.facade.v26_2;

import java.util.List;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Facade for 1.20.1 net/minecraft/class_5597 (SinglePartEntityModel), the base
 * of most mod entity models. 26.2 reshaped entity rendering around a
 * render-state: EntityModel is now EntityModel&lt;EntityRenderState&gt; and the
 * animation hook moved to Model.setupAnim(S) (one arg), while the mod overrides
 * the OLD setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw,
 * headPitch) (six args).
 *
 * Rather than leave the mod's animation an orphan, this facade bridges the two:
 * it extends the real EntityModel and overrides the new setupAnim(S), pulling
 * the five animation values the mod needs out of LivingEntityRenderState
 * (walkAnimationPos = limbSwing, walkAnimationSpeed, ageInTicks, yRot = headYaw,
 * xRot = headPitch) and forwarding to the mod's old six-arg setupAnim. So the
 * mod's animation logic actually RUNS and renderToBuffer (still present on 26.2)
 * draws it. The entity argument is null -- almost all models read only the five
 * floats; one that dereferences the entity degrades on that path only.
 *
 * Member names are intermediary tokens; the EMPTY_ROOT placeholder satisfies the
 * 26.2 EntityModel(ModelPart) constructor (the old no-arg form is gone), the
 * mod's real root is its own via method_32008.
 */
public abstract class class_5597<T extends EntityRenderState> extends EntityModel<T> {

    private static final ModelPart EMPTY_ROOT = new ModelPart(List.of(), Map.of());

    protected class_5597() {
        super(EMPTY_ROOT);
    }

    protected class_5597(java.util.function.Function<net.minecraft.resources.Identifier,
            net.minecraft.client.renderer.rendertype.RenderType> renderType) {
        super(EMPTY_ROOT, renderType);
    }

    /** getRoot/getPart (method_32008) -- the mod supplies its real model root. */
    public abstract ModelPart method_32008();

    /** old setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw,
     *  headPitch) (method_2819): the mod overrides this. Default no-op so a model
     *  that leaves it to a parent still links. */
    public void method_2819(Entity entity, float limbSwing, float limbSwingAmount,
            float ageInTicks, float netHeadYaw, float headPitch) {
    }

    /** 26.2 setupAnim(renderState): pull the animation values vanilla extracted
     *  and forward to the mod's old six-arg hook, so its animation runs. */
    @Override
    public void setupAnim(T state) {
        super.setupAnim(state);
        float limbSwing = 0.0F, limbSwingAmount = 0.0F, headYaw = 0.0F, headPitch = 0.0F;
        float ageInTicks = state.ageInTicks;
        if (state instanceof LivingEntityRenderState living) {
            limbSwing = living.walkAnimationPos;
            limbSwingAmount = living.walkAnimationSpeed;
            headYaw = living.yRot;
            headPitch = living.xRot;
        }
        this.method_2819(null, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
    }

    /** old render(PoseStack, VertexConsumer, light, overlay, r, g, b, a)
     *  (method_2828) -> renderToBuffer with the colour packed to ARGB. */
    public void method_2828(PoseStack pose, VertexConsumer vc, int light, int overlay,
            float r, float g, float b, float a) {
        int argb = ((int) (a * 255.0F) << 24) | ((int) (r * 255.0F) << 16)
                 | ((int) (g * 255.0F) << 8) | (int) (b * 255.0F);
        this.renderToBuffer(pose, vc, light, overlay, argb);
    }
}
