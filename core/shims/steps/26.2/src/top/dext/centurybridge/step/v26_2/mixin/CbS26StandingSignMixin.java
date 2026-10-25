package top.dext.centurybridge.step.v26_2.mixin;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.renderer.blockentity.state.StandingSignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PlainSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26LegacySign;
import top.dext.centurybridge.step.v26_2.rt.CbS26SignsClient;

/**
 * 26.1.2's StandingSignRenderer built a standing and a wall model for every WoodType.values() in its constructor
 * (Models.create: createSignModel(context.entityModelSet(), wood, GROUND / WALL)) and, in extractRenderState, chose
 * the wood's model for the block's attachment and the body transformation (createGroundTransformation:
 * RotationSegment.convertToDegrees(ROTATION); createWallTransformation: FACING.toYRot()). 26.2's keeps the text part.
 * For a sign block 26.2 draws no body for (rt/CbS26SignsClient.legacyBody) the 26.1.2 body is put on the render state
 * at the end of 26.2's extraction; mixin/CbS26SignSubmitMixin submits it.
 */
@Environment(EnvType.CLIENT)
@Mixin(StandingSignRenderer.class)
public abstract class CbS26StandingSignMixin {
    @Unique
    private final Map<WoodType, Map<PlainSignBlock.Attachment, Model.Simple>> cbS26$models = new HashMap<>();
    @Unique
    private SpriteGetter cbS26$sprites;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cbS26$legacyModels(BlockEntityRendererProvider.Context context, CallbackInfo ci) {
        this.cbS26$sprites = context.sprites();
        WoodType.values().forEach(wood -> {
            Map<PlainSignBlock.Attachment, Model.Simple> m = new EnumMap<>(PlainSignBlock.Attachment.class);
            for (PlainSignBlock.Attachment a : PlainSignBlock.Attachment.values()) {
                m.put(a, CbS26SignsClient.createSignModel(context.entityModelSet(), wood, a));
            }
            this.cbS26$models.put(wood, m);
        });
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/level/block/entity/SignBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/StandingSignRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
        at = @At("TAIL"))
    private void cbS26$legacyBody(SignBlockEntity sign, StandingSignRenderState state, float partialTick, Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress, CallbackInfo ci) {
        BlockState blockState = sign.getBlockState();
        Block block = blockState.getBlock();
        Map<PlainSignBlock.Attachment, Model.Simple> models = this.cbS26$models.get(SignBlock.getWoodType(block));
        if (!CbS26SignsClient.legacyBody(block) || models == null) {
            ((CbS26LegacySign) state).cbS26$setLegacyBody(null, null, null, null);
            return;
        }
        float yRot = block instanceof WallSignBlock
            ? blockState.getValue(WallSignBlock.FACING).toYRot()
            : RotationSegment.convertToDegrees(blockState.getValue(StandingSignBlock.ROTATION));
        ((CbS26LegacySign) state).cbS26$setLegacyBody(models.get(state.attachmentType),
            CbS26SignsClient.getSignSprite(SignBlock.getWoodType(block)),
            CbS26SignsClient.standingBody(state.attachmentType, yRot), this.cbS26$sprites);
    }
}
