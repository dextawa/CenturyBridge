package top.dext.centurybridge.step.v26_1.facade;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.level.block.PlainSignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

/**
 * 1.21.11's SignRenderer (26.1's StandingSignRenderer renders the same signs from precomputed
 * transformations): 1.21.11's members and bodies on facade/AbstractSignRenderer, the models and sheets
 * 26.1's -- the standing and wall models of every wood type (StandingSignRenderer.createSignModel, the same
 * layers), the wood type's sign sprite. Members keep their intermediary names.
 */
@Environment(EnvType.CLIENT)
public class SignRenderer extends AbstractSignRenderer {

    /** RENDER_SCALE */
    public static final float field_44577 = 0.6666667F;
    /** TEXT_OFFSET */
    private static final Vec3 TEXT_OFFSET = new Vec3(0.0, 0.3333333432674408, 0.046666666865348816);

    /** the standing and wall model of a wood type (1.21.11's SignRenderer.Models) */
    private record Models(Model.Simple standing, Model.Simple wall) {
    }

    private final Map<WoodType, Models> signModels;

    public SignRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.signModels = WoodType.values().collect(ImmutableMap.toImmutableMap(w -> w,
            w -> new Models(method_32157(context.entityModelSet(), w, true), method_32157(context.entityModelSet(), w, false))));
    }

    @Override
    public Model.Simple method_65823(BlockState state, WoodType woodType) {
        Models models = this.signModels.get(woodType);
        return state.getBlock() instanceof StandingSignBlock ? models.standing() : models.wall();
    }

    @Override
    public SpriteId method_45792(WoodType woodType) {
        return Sheets.getSignSprite(woodType);
    }

    @Override
    public float method_51272() {
        return 0.6666667F;
    }

    @Override
    public float method_51273() {
        return 0.6666667F;
    }

    /** translateBase(pose, yRotation) */
    private static void translateBase(PoseStack pose, float yRotation) {
        pose.translate(0.5F, 0.5F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(yRotation));
    }

    @Override
    public void method_65824(PoseStack pose, float yRotation, BlockState state) {
        translateBase(pose, yRotation);
        if (!(state.getBlock() instanceof StandingSignBlock)) {
            pose.translate(0.0F, -0.3125F, -0.4375F);
        }
    }

    @Override
    public Vec3 method_45790() {
        return TEXT_OFFSET;
    }

    /** submitSpecial(sprites, pose, collector, light, overlay, model, sprite): the sign item, posed in hand as 1.21.11 posed it */
    public static void method_45800(SpriteGetter sprites, PoseStack pose, SubmitNodeCollector collector, int light, int overlay, Model.Simple model,
        SpriteId sprite) {
        pose.pushPose();
        method_72164(pose);
        StandingSignRenderer.submitSpecial(sprites, pose, collector, light, overlay, model, sprite);
        pose.popPose();
    }

    /** applyInHandTransforms(pose) */
    public static void method_72164(PoseStack pose) {
        translateBase(pose, 0.0F);
        pose.scale(0.6666667F, -0.6666667F, -0.6666667F);
    }

    /** createSignModel(models, woodType, standing) */
    public static Model.Simple method_32157(EntityModelSet models, WoodType woodType, boolean standing) {
        return StandingSignRenderer.createSignModel(models, woodType, standing ? PlainSignBlock.Attachment.GROUND : PlainSignBlock.Attachment.WALL);
    }

    /** createSignLayer(standing) */
    public static LayerDefinition method_32154(boolean standing) {
        return StandingSignRenderer.createSignLayer(standing);
    }
}
