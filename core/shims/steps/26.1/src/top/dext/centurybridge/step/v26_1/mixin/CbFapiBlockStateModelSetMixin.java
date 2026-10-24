package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbFapiBlockLayers;

import java.util.Map;

/** a block 1.21.11's BlockRenderLayerMap mapped answers its model in the mapped layer (rt/CbFapiBlockLayers.model) */
@Environment(EnvType.CLIENT)
@Mixin(BlockStateModelSet.class)
public abstract class CbFapiBlockStateModelSetMixin {
    @Unique
    private final Map<BlockStateModel, BlockStateModel[]> cb$relayered = CbFapiBlockLayers.newCache();

    @Inject(method = "get", at = @At("RETURN"), cancellable = true)
    private void cb$mappedLayer(BlockState state, CallbackInfoReturnable<BlockStateModel> cir) {
        BlockStateModel model = cir.getReturnValue();
        BlockStateModel mapped = CbFapiBlockLayers.model(this.cb$relayered, state, model);
        if (mapped != model) {
            cir.setReturnValue(mapped);
        }
    }
}
