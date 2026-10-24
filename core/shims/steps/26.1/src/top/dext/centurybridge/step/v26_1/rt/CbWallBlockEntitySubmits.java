package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

/** Block entity renderers' static submit helpers whose 1.21.11 shape 26.1 narrowed. */
@Environment(EnvType.CLIENT)
public final class CbWallBlockEntitySubmits {

    private CbWallBlockEntitySubmits() {
    }

    /**
     * 1.21.11 BannerRenderer.submitPatterns(sprites, pose, collector, light, overlay, model, state, baseMaterial, banner,
     * baseColor, patterns, foil, crumbling, outline): the base model in its material (solid entity type, with the
     * outline), the glint over it when foil, then the patterns. 26.1's submitPatterns draws only the patterns; its
     * callers submit the base themselves.
     */
    public static <S> void submitPatterns(SpriteGetter sprites, PoseStack pose, SubmitNodeCollector collector, int light, int overlay, Model<S> model,
                                          S state, SpriteId baseMaterial, boolean banner, DyeColor baseColor, BannerPatternLayers patterns, boolean foil,
                                          ModelFeatureRenderer.CrumblingOverlay crumbling, int outline) {
        collector.submitModel(model, state, pose, baseMaterial.renderType(RenderTypes::entitySolid), light, overlay, -1, sprites.get(baseMaterial),
            outline, crumbling);
        if (foil) {
            collector.submitModel(model, state, pose, RenderTypes.entityGlint(), light, overlay, -1, sprites.get(baseMaterial), 0, crumbling);
        }
        BannerRenderer.submitPatterns(sprites, pose, collector, light, overlay, model, state, banner, baseColor, patterns, crumbling);
    }

    /**
     * 1.21.11 SkullBlockRenderer.submitSkull(direction, yRot, mouthAnimation, pose, collector, light, model, renderType,
     * outline, crumbling): placed on the wall it faces (centred when null), flipped, the model posed with the mouth
     * animation and rotation. 26.1's submitSkull takes the placement and rotation from its caller's transformation.
     */
    public static void submitSkull(Direction direction, float yRot, float mouthAnimation, PoseStack pose, SubmitNodeCollector collector, int light,
                                   SkullModelBase model, RenderType renderType, int outline, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        pose.pushPose();
        if (direction == null) {
            pose.translate(0.5F, 0.0F, 0.5F);
        } else {
            pose.translate(0.5F - direction.getStepX() * 0.25F, 0.25F, 0.5F - direction.getStepZ() * 0.25F);
        }
        pose.scale(-1.0F, -1.0F, 1.0F);
        SkullModelBase.State state = new SkullModelBase.State();
        state.animationPos = mouthAnimation;
        state.yRot = yRot;
        collector.submitModel(model, state, pose, renderType, light, OverlayTexture.NO_OVERLAY, outline, crumbling);
        pose.popPose();
    }
}
