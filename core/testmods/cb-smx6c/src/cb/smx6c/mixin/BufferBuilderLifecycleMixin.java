package cb.smx6c.mixin;

import cb.smx6c.Counters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.6 mod's injectors on BufferBuilder's batch lifecycle: begin(mode, format) HEAD, end() RETURN and
 * endOrDiscardIfEmpty() HEAD, counting (1.21: the layer's restored members, reached through CbRenderBufferBuilderRouteMixin /
 * CbRenderSectionCompileRouteMixin when mixed).
 */
@Mixin(net.minecraft.class_287.class)
public abstract class BufferBuilderLifecycleMixin {

    @Inject(method = "method_1328(Lnet/minecraft/class_293$class_5596;Lnet/minecraft/class_293;)V", at = @At("HEAD"))
    private void cb$begin(net.minecraft.class_293.class_5596 mode, net.minecraft.class_293 format, CallbackInfo ci) {
        Counters.BEGIN.incrementAndGet();
    }

    @Inject(method = "method_1326()Lnet/minecraft/class_287$class_7433;", at = @At("RETURN"))
    private void cb$end(CallbackInfoReturnable<?> cir) {
        Counters.END.incrementAndGet();
    }

    @Inject(method = "method_43575()Lnet/minecraft/class_287$class_7433;", at = @At("HEAD"))
    private void cb$endOrDiscard(CallbackInfoReturnable<?> cir) {
        Counters.END_OR_DISCARD.incrementAndGet();
    }
}
