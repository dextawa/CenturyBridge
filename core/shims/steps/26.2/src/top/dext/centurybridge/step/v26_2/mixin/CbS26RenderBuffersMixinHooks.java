package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbPrioritySplitClient;

/**
 * The injector into 26.2's own RenderBuffers constructor that goes with {@link CbS26RenderBuffersMixin} (its restored
 * source getters stay there, at {@code CbVanillaReads.PRIORITY}): builds 26.1.2's three sources at the constructor's
 * end, as 26.1.2's constructor did. Default priority.
 */
@Mixin(RenderBuffers.class)
public abstract class CbS26RenderBuffersMixinHooks {

    @Inject(method = "<init>(I)V", at = @At("TAIL"))
    private void cbS26$sources(int threads, CallbackInfo ci) {
        ((CbPrioritySplitClient.RenderBufferSources) (Object) this).cb$v26_2$buildSources();
    }
}
