package top.dext.centurybridge.step.v26_2.mixin;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.state.HangingSignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.HangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
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
 * 26.1.2's HangingSignRenderer: a model per attachment (CEILING, CEILING_MIDDLE, WALL) for every WoodType.values()
 * (Models.create), chosen in extractRenderState by the block's attachment, with the body transformation of the
 * block's yaw (WallHangingSignBlock FACING.toYRot(), CeilingHangingSignBlock RotationSegment.convertToDegrees(ROTATION)).
 * As for standing signs (mixin/CbS26StandingSignMixin), only for sign blocks 26.2 draws no body for.
 */
@Environment(EnvType.CLIENT)
@Mixin(HangingSignRenderer.class)
public abstract class CbS26HangingSignMixin {
    @Unique
    private final Map<WoodType, Map<HangingSignBlock.Attachment, Model.Simple>> cbS26$models = new HashMap<>();
    @Unique
    private SpriteGetter cbS26$sprites;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cbS26$legacyModels(BlockEntityRendererProvider.Context context, CallbackInfo ci) {
        this.cbS26$sprites = context.sprites();
        WoodType.values().forEach(wood -> {
            Map<HangingSignBlock.Attachment, Model.Simple> m = new EnumMap<>(HangingSignBlock.Attachment.class);
            for (HangingSignBlock.Attachment a : HangingSignBlock.Attachment.values()) {
                m.put(a, CbS26SignsClient.createHangingSignModel(context.entityModelSet(), wood, a));
            }
            this.cbS26$models.put(wood, m);
        });
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/level/block/entity/SignBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/HangingSignRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
        at = @At("TAIL"))
    private void cbS26$legacyBody(SignBlockEntity sign, HangingSignRenderState state, float partialTick, Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress, CallbackInfo ci) {
        BlockState blockState = sign.getBlockState();
        Block block = blockState.getBlock();
        Map<HangingSignBlock.Attachment, Model.Simple> models = this.cbS26$models.get(SignBlock.getWoodType(block));
        if (!CbS26SignsClient.legacyBody(block) || models == null) {
            ((CbS26LegacySign) state).cbS26$setLegacyBody(null, null, null, null);
            return;
        }
        float yRot = block instanceof WallHangingSignBlock
            ? blockState.getValue(WallHangingSignBlock.FACING).toYRot()
            : RotationSegment.convertToDegrees(blockState.getValue(CeilingHangingSignBlock.ROTATION));
        ((CbS26LegacySign) state).cbS26$setLegacyBody(models.get(state.attachmentType),
            CbS26SignsClient.getHangingSignSprite(SignBlock.getWoodType(block)),
            CbS26SignsClient.hangingBody(yRot), this.cbS26$sprites);
    }
}
