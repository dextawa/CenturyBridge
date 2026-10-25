package top.dext.centurybridge.step.v26_2.facade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.blockentity.state.BedRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import top.dext.centurybridge.step.v26_2.rt.CbS26Beds;
import top.dext.centurybridge.step.v26_2.rt.CbS26BedsClient;

/**
 * 26.1.2's BedRenderer (gone on 26.2, which draws its own beds as block models): reached by classRenames, 26.1.2's
 * body member by member (javap) on the 26.2 members that took the same calls -- the head and foot model parts baked
 * from the 26.1.2 layers (ModelLayers.BED_HEAD / BED_FOOT, given back by rt/CbS26BedsClient and registered with
 * 26.2's layer definitions), the 26.1.2 bed sprite of the block entity's colour (rt/CbS26BedsClient.getBedSprite), the
 * brightness combined with the other half of the bed, and 26.1.2's facing transform (its own map; staticmoves-gen
 * once sent it to 26.2's ShulkerBoxRenderer.modelTransform, a map built by another transform). Registered for 26.1.2's BED
 * block entity type (mixin/CbS26BedRendererMixin), as 26.1.2's BlockEntityRenderers registered it.
 */
@Environment(EnvType.CLIENT)
public class BedRenderer implements BlockEntityRenderer<BedBlockEntity, BedRenderState> {
    private static final Map<Direction, Transformation> TRANSFORMATIONS = Util.makeEnumMap(Direction.class, BedRenderer::createModelTransform);
    private static final Set<Direction> VISIBLE_LEG_FACES = Util.allOfEnumExcept(Direction.DOWN);
    private final SpriteGetter sprites;
    private final Model.Simple headModel;
    private final Model.Simple footModel;

    public BedRenderer(BlockEntityRendererProvider.Context context) {
        this(context.sprites(), context.entityModelSet());
    }

    public BedRenderer(SpecialModelRenderer.BakingContext context) {
        this(context.sprites(), context.entityModelSet());
    }

    public BedRenderer(SpriteGetter sprites, EntityModelSet models) {
        this.sprites = sprites;
        this.headModel = new Model.Simple(models.bakeLayer(CbS26BedsClient.BED_HEAD()), RenderTypes::entitySolid);
        this.footModel = new Model.Simple(models.bakeLayer(CbS26BedsClient.BED_FOOT()), RenderTypes::entitySolid);
    }

    public static LayerDefinition createHeadLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        Set<Direction> mainFaces = Util.allOfEnumExcept(Direction.UP);
        root.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, mainFaces), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(50, 6).addBox(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F, VISIBLE_LEG_FACES),
            PartPose.rotation((float) (Math.PI / 2), 0.0F, (float) (Math.PI / 2)));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 18).addBox(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F, VISIBLE_LEG_FACES),
            PartPose.rotation((float) (Math.PI / 2), 0.0F, (float) Math.PI));
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition createFootLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        Set<Direction> mainFaces = Util.allOfEnumExcept(Direction.DOWN);
        root.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, mainFaces), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(50, 0).addBox(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F, VISIBLE_LEG_FACES),
            PartPose.rotation((float) (Math.PI / 2), 0.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 12).addBox(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F, VISIBLE_LEG_FACES),
            PartPose.rotation((float) (Math.PI / 2), 0.0F, (float) (Math.PI * 3.0 / 2.0)));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public BedRenderState createRenderState() {
        return new BedRenderState();
    }

    @Override
    public void extractRenderState(BedBlockEntity blockEntity, BedRenderState state, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.color = blockEntity.getColor();
        state.facing = blockEntity.getBlockState().getValue(net.minecraft.world.level.block.BedBlock.FACING);
        state.part = blockEntity.getBlockState().getValue(net.minecraft.world.level.block.BedBlock.PART);
        if (blockEntity.getLevel() != null) {
            DoubleBlockCombiner.NeighborCombineResult<? extends BedBlockEntity> combined = DoubleBlockCombiner.combineWithNeigbour(
                CbS26Beds.BED(), net.minecraft.world.level.block.BedBlock::getBlockType, net.minecraft.world.level.block.BedBlock::getConnectedDirection,
                ChestBlock.FACING, blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos(), (level, pos) -> false);
            state.lightCoords = combined.apply(new BrightnessCombiner<>()).get(state.lightCoords);
        }
    }

    @Override
    public void submit(BedRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        SpriteId sprite = CbS26BedsClient.getBedSprite(state.color);
        poseStack.pushPose();
        poseStack.mulPose(modelTransform(state.facing));
        this.submitPiece(state.part, sprite, poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.breakProgress, 0);
        poseStack.popPose();
    }

    public void submitPiece(BedPart part, SpriteId sprite, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords,
                            ModelFeatureRenderer.CrumblingOverlay breakProgress, int outlineColor) {
        collector.submitModel(this.getPieceModel(part), Unit.INSTANCE, poseStack, lightCoords, overlayCoords, -1, sprite, this.sprites, outlineColor,
            breakProgress);
    }

    private Model.Simple getPieceModel(BedPart part) {
        return switch (part) {
            case HEAD -> this.headModel;
            case FOOT -> this.footModel;
        };
    }

    /** 26.1.2 BedRenderer.modelTransform: the transform of its own map (26.2's ShulkerBoxRenderer's is another) */
    public static Transformation modelTransform(Direction facing) {
        return TRANSFORMATIONS.get(facing);
    }

    /** 26.1.2 BedRenderer.createModelTransform, its body */
    private static Transformation createModelTransform(Direction facing) {
        return new Transformation(new Matrix4f().translation(0.0F, 0.5625F, 0.0F).rotate(Axis.XP.rotationDegrees(90.0F))
            .rotateAround(Axis.ZP.rotationDegrees(180.0F + facing.toYRot()), 0.5F, 0.5F, 0.5F));
    }

    public void getExtents(BedPart part, Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        this.getPieceModel(part).root().getExtentsForGui(poseStack, output);
    }
}
