package top.dext.centurybridge.shims.v26_2;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v26_2.CbRenderStateBlockEntity;

/** The dispatcher builds every block entity's render state here: it leaves knowing its block entity. */
@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherCaptureBridge {

    @Inject(method = "tryExtractRenderState(Lnet/minecraft/world/level/block/entity/BlockEntity;FLnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;Z)Lnet/minecraft/client/renderer/blockentity/state/BlockEntityRenderState;",
            at = @At("RETURN"))
    private void centurybridge$capture(BlockEntity blockEntity, float partialTick, ModelFeatureRenderer.CrumblingOverlay crumbling,
                                       boolean flag, CallbackInfoReturnable<BlockEntityRenderState> cir) {
        BlockEntityRenderState state = cir.getReturnValue();
        if (state != null) {
            ((CbRenderStateBlockEntity) state).centurybridge$capture(blockEntity, partialTick);
        }
    }
}
