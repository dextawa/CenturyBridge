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
 * Facade for 1.20.1 net/minecraft/class_4592 (AgeableListModel), an entity-model
 * base deleted/reshaped on 26.2. Same bridge as SinglePartEntityModel: extends
 * real EntityModel, forwards the new setupAnim(renderState) to the mod's old
 * six-arg setupAnim pulling the animation values from LivingEntityRenderState.
 * The four old constructors (scale/offset flags) forward to super(EMPTY_ROOT);
 * the mod's real parts come via its getHeadParts/getBodyParts (method_22946/48).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public abstract class class_4592<T extends EntityRenderState> extends EntityModel<T> {

    private static final ModelPart EMPTY_ROOT = new ModelPart(List.of(), Map.of());

    protected class_4592() { super(EMPTY_ROOT); }
    protected class_4592(boolean scaleHead, float yOff, float zOff) { super(EMPTY_ROOT); }
    protected class_4592(boolean scaleHead, float yOff, float zOff, float a, float b, float c) { super(EMPTY_ROOT); }
    protected class_4592(java.util.function.Function<net.minecraft.resources.Identifier,
            net.minecraft.client.renderer.rendertype.RenderType> rt,
            boolean scaleHead, float yOff, float zOff, float a, float b, float c) { super(EMPTY_ROOT, rt); }

    /** getHeadParts (method_22946) */
    protected abstract Iterable<ModelPart> method_22946();
    /** getBodyParts (method_22948) */
    protected abstract Iterable<ModelPart> method_22948();

    /** old setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch) */
    public void method_2819(Entity e, float limbSwing, float limbSwingAmount,
            float ageInTicks, float headYaw, float headPitch) {
    }

    @Override
    public void setupAnim(T state) {
        super.setupAnim(state);
        float ls = 0, lsa = 0, hy = 0, hp = 0, age = state.ageInTicks;
        if (state instanceof LivingEntityRenderState l) {
            ls = l.walkAnimationPos; lsa = l.walkAnimationSpeed; hy = l.yRot; hp = l.xRot;
        }
        this.method_2819(null, ls, lsa, age, hy, hp);
    }

    /** old render (method_2828) -> renderToBuffer with ARGB-packed color */
    public void method_2828(PoseStack pose, VertexConsumer vc, int light, int overlay,
            float r, float g, float b, float a) {
        int argb = ((int)(a*255)<<24)|((int)(r*255)<<16)|((int)(g*255)<<8)|(int)(b*255);
        this.renderToBuffer(pose, vc, light, overlay, argb);
    }
}
