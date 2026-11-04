package top.dext.centurybridge.step.v26_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_2.rt.CbS26RenderSetup;

/**
 * Carries a 26.1.2-era RenderSetupBuilder.bufferSize(int) onto the RenderSetup the builder builds (rt/CbS26RenderSetup),
 * so RenderType.bufferSize() can answer it. Observes the return only; default priority.
 */
@Environment(EnvType.CLIENT)
@Mixin(RenderSetup.RenderSetupBuilder.class)
public abstract class CbS26RenderSetupSizeMixin {

    @Inject(method = "createRenderSetup", at = @At("RETURN"))
    private void cbS26$keepBufferSize(CallbackInfoReturnable<RenderSetup> cir) {
        CbS26RenderSetup.built((RenderSetup.RenderSetupBuilder) (Object) this, cir.getReturnValue());
    }
}
