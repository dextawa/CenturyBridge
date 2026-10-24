package top.dext.centurybridge.step.v26_1.facade;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Arrays;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.level.block.HangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

/**
 * 1.21.11's HangingSignRenderer (26.1's renders the same signs from precomputed transformations): 1.21.11's
 * members and bodies on facade/AbstractSignRenderer, the models and sheets 26.1's -- a model per wood type
 * and attachment (26.1's HangingSignRenderer.createSignModel, the same layers), the wood type's hanging sign
 * sprite. Members keep their intermediary names.
 */
@Environment(EnvType.CLIENT)
public class HangingSignRenderer extends AbstractSignRenderer {

    /** MODEL_RENDER_SCALE */
    public static final float field_44574 = 1.0F;
    private static final float TEXT_RENDER_SCALE = 0.9F;
    private static final Vec3 TEXT_OFFSET = new Vec3(0.0, -0.3199999928474426, 0.0729999989271164);

    /**
     * 1.21.11's HangingSignRenderer.ModelKey record: a wood type and an attachment (components, accessors and
     * record equality under the intermediary names 1.21.11's record had at runtime; fields public, as access
     * wideners never reach a CenturyBridge class)
     */
    public static final class ModelKey {
        public final WoodType comp_3342;
        public final HangingSignBlock.Attachment comp_3343;

        public ModelKey(WoodType woodType, HangingSignBlock.Attachment attachmentType) {
            this.comp_3342 = woodType;
            this.comp_3343 = attachmentType;
        }

        /** woodType */
        public WoodType comp_3342() {
            return this.comp_3342;
        }

        /** attachmentType */
        public HangingSignBlock.Attachment comp_3343() {
            return this.comp_3343;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ModelKey k && java.util.Objects.equals(this.comp_3342, k.comp_3342) && this.comp_3343 == k.comp_3343;
        }

        @Override
        public int hashCode() {
            return 31 * java.util.Objects.hashCode(this.comp_3342) + java.util.Objects.hashCode(this.comp_3343);
        }

        @Override
        public String toString() {
            return "class_10382[comp_3342=" + this.comp_3342 + ", comp_3343=" + this.comp_3343 + "]";
        }
    }

    /**
     * 1.21.11's HangingSignRenderer.AttachmentType.byBlockState(state) (the type is 26.1's HangingSignBlock.Attachment,
     * same constants): a ceiling hanging sign hangs from the middle when attached, else from the ceiling; any other
     * block from a wall -- 1.21.11's body
     */
    public static HangingSignBlock.Attachment attachmentByBlockState(BlockState state) {
        if (state.getBlock() instanceof net.minecraft.world.level.block.CeilingHangingSignBlock) {
            return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.ATTACHED)
                ? HangingSignBlock.Attachment.CEILING_MIDDLE : HangingSignBlock.Attachment.CEILING;
        }
        return HangingSignBlock.Attachment.WALL;
    }

    private final Map<ModelKey, Model.Simple> hangingSignModels;

    public HangingSignRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.hangingSignModels = WoodType.values()
            .flatMap(w -> Arrays.stream(HangingSignBlock.Attachment.values()).map(a -> new ModelKey(w, a)))
            .collect(ImmutableMap.toImmutableMap(k -> k, k -> method_65830(context.entityModelSet(), k.comp_3342(), k.comp_3343())));
    }

    /** createSignModel(models, woodType, attachment) */
    public static Model.Simple method_65830(EntityModelSet models, WoodType woodType, HangingSignBlock.Attachment attachment) {
        return net.minecraft.client.renderer.blockentity.HangingSignRenderer.createSignModel(models, woodType, attachment);
    }

    @Override
    public float method_51272() {
        return 1.0F;
    }

    @Override
    public float method_51273() {
        return TEXT_RENDER_SCALE;
    }

    /** translateBase(pose, yRotation) */
    public static void method_49918(PoseStack pose, float yRotation) {
        pose.translate(0.5, 0.9375, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees(yRotation));
        pose.translate(0.0F, -0.3125F, 0.0F);
    }

    @Override
    public void method_65824(PoseStack pose, float yRotation, BlockState state) {
        method_49918(pose, yRotation);
    }

    @Override
    public Model.Simple method_65823(BlockState state, WoodType woodType) {
        return this.hangingSignModels.get(new ModelKey(woodType, attachmentByBlockState(state)));
    }

    @Override
    public SpriteId method_45792(WoodType woodType) {
        return Sheets.getHangingSignSprite(woodType);
    }

    @Override
    public Vec3 method_45790() {
        return TEXT_OFFSET;
    }

    /** submitSpecial(sprites, pose, collector, light, overlay, model, sprite): the hanging sign item, posed as 1.21.11 posed it */
    public static void method_65829(SpriteGetter sprites, PoseStack pose, SubmitNodeCollector collector, int light, int overlay, Model.Simple model,
        SpriteId sprite) {
        pose.pushPose();
        method_49918(pose, 0.0F);
        pose.scale(1.0F, -1.0F, -1.0F);
        net.minecraft.client.renderer.blockentity.HangingSignRenderer.submitSpecial(sprites, pose, collector, light,
            net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, model, sprite); // 1.21.11 drew it without overlay whatever it was given
        pose.popPose();
    }

    /** createHangingSignLayer(attachment) */
    public static LayerDefinition method_45795(HangingSignBlock.Attachment attachment) {
        return net.minecraft.client.renderer.blockentity.HangingSignRenderer.createHangingSignLayer(attachment);
    }
}
