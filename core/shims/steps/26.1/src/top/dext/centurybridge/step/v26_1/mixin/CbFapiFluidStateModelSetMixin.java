package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbFapiFluids;

/** the model of a fluid 1.21.11's handler or layer map decides (rt/CbFapiFluids.model) */
@Environment(EnvType.CLIENT)
@Mixin(FluidStateModelSet.class)
public abstract class CbFapiFluidStateModelSetMixin {

    @Inject(method = "get", at = @At("RETURN"), cancellable = true)
    private void cb$legacyFluidModel(FluidState state, CallbackInfoReturnable<FluidModel> cir) {
        FluidModel model = cir.getReturnValue();
        FluidModel legacy = CbFapiFluids.model(state, model);
        if (legacy != model) {
            cir.setReturnValue(legacy);
        }
    }
}
