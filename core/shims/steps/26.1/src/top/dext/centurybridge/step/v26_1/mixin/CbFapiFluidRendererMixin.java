package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbFapiFluids;

/** 1.21.11's LiquidBlockRendererMixin.onHeadRender: a fluid with a 1.21.11 handler renders through it (rt/CbFapiFluids) */
@Environment(EnvType.CLIENT)
@Mixin(FluidRenderer.class)
public abstract class CbFapiFluidRendererMixin {

    @Inject(method = "tesselate", at = @At("HEAD"), cancellable = true)
    private void cb$legacyFluidHandler(BlockAndTintGetter level, BlockPos pos, FluidRenderer.Output output, BlockState blockState,
                                       FluidState fluidState, CallbackInfo ci) {
        if (CbFapiFluids.tesselate(level, pos, output, blockState, fluidState)) {
            ci.cancel();
        }
    }
}
