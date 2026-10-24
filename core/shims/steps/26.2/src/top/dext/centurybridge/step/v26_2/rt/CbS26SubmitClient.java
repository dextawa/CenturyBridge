package top.dext.centurybridge.step.v26_2.rt;

import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * 26.1.2 OrderedSubmitNodeCollector calls whose shape 26.2 changed, each submitting what 26.1.2's storage and feature
 * renderers drew (javap of both):
 * <ul>
 * <li>submitModelPart(.., sprite, sheeted, hasFoil[, tint, crumbling, outline]): 26.2 dropped the two flags; a foil part
 * is a second submission with the glint render type (26.2's TridentSpecialRenderer does the same). 26.1.2's
 * ModelPartFeatureRenderer drew a foil part through ItemFeatureRenderer.getFoilBuffer(buffers, renderType, sheeted,
 * true): the part's vertices once more into getFoilRenderType(renderType, sheeted), the sprite's UVs included, tinted
 * alike, no crumbling or outline on the glint.</li>
 * <li>submitMovingBlock(pose, state): 26.2 adds the outline colour; 0 is no outline (26.2's own non-outlined submission),
 * and 26.1.2's moving blocks had none.</li>
 * <li>submitBreakingBlockModel(pose, model, seed, progress): 26.2 takes the model's parts; 26.1.2's
 * BlockFeatureRenderer collected them at draw time from a SingleThreadedRandomSource seeded with the seed -- collected
 * here the same way.</li>
 * <li>submitNameTag(.., distanceToCameraSq, camera): 26.2 dropped the distance (26.1.2's storage kept it on the
 * submission; 26.2's renderers and its own entity renderers no longer pass or read one).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbS26SubmitClient {
    private CbS26SubmitClient() {}

    /** 26.1.2 ItemFeatureRenderer.getFoilRenderType(renderType, sheeted), with useTransparentGlint as 26.2 computes it */
    static RenderType foilRenderType(RenderType renderType, boolean sheeted) {
        if (Minecraft.getInstance().gameRenderer.gameRenderState().useShaderTransparency()
                && renderType.outputTarget() == OutputTarget.ITEM_ENTITY_TARGET) {
            return RenderTypes.glintTranslucent();
        }
        return sheeted ? RenderTypes.glint() : RenderTypes.entityGlint();
    }

    /** 26.1.2's default: (.., sheeted, hasFoil, -1, null, 0) */
    public static void submitModelPart(OrderedSubmitNodeCollector c, ModelPart part, PoseStack pose, RenderType renderType, int light,
            int overlay, TextureAtlasSprite sprite, boolean sheeted, boolean hasFoil) {
        submitModelPart(c, part, pose, renderType, light, overlay, sprite, sheeted, hasFoil, -1, null, 0);
    }

    public static void submitModelPart(OrderedSubmitNodeCollector c, ModelPart part, PoseStack pose, RenderType renderType, int light,
            int overlay, TextureAtlasSprite sprite, boolean sheeted, boolean hasFoil, int tint,
            ModelFeatureRenderer.CrumblingOverlay crumbling, int outline) {
        c.submitModelPart(part, pose, renderType, light, overlay, sprite, tint, crumbling, outline);
        if (hasFoil) {
            c.submitModelPart(part, pose, foilRenderType(renderType, sheeted), light, overlay, sprite, tint, null, 0);
        }
    }

    public static void submitMovingBlock(OrderedSubmitNodeCollector c, PoseStack pose, MovingBlockRenderState state) {
        c.submitMovingBlock(pose, state, 0);
    }

    public static void submitBreakingBlockModel(OrderedSubmitNodeCollector c, PoseStack pose, BlockStateModel model, long seed, int progress) {
        RandomSource random = RandomSource.createThreadLocalInstance(0L);
        random.setSeed(seed);
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(random, parts);
        c.submitBreakingBlockModel(pose, parts, progress);
    }

    public static void submitNameTag(OrderedSubmitNodeCollector c, PoseStack pose, Vec3 position, int y, Component name, boolean seeThrough,
            int light, double distanceToCameraSq, CameraRenderState camera) {
        c.submitNameTag(pose, position, y, name, seeThrough, light, camera);
    }
}
