package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallBlockModelSource;

/**
 * 26.1's BlockModelResolver.update(model, blockState, context) clears the model and fills it from the block state;
 * at its return the model records what it now shows (rt/CbWallBlockModelSource). Observes only.
 */
@Environment(EnvType.CLIENT)
@Mixin(BlockModelResolver.class)
public abstract class CbWallBlockModelResolverMixin {

    @Inject(method = "update(Lnet/minecraft/client/renderer/block/BlockModelRenderState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/block/model/BlockDisplayContext;)V",
            at = @At("RETURN"))
    private void cb$resolved(BlockModelRenderState model, BlockState state, BlockDisplayContext context, CallbackInfo ci) {
        ((CbWallBlockModelSource.Source) model).cb$resolved(state, (BlockModelResolver) (Object) this, context);
    }
}
