package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbRenderTypes;

/**
 * MultiBufferSource.BufferSource ending a batch of a 1.20.6 render type that declares its own end(builder,
 * sorting): 1.20.6's BufferSource called type.end(builder, RenderSystem.getVertexSorting()) for every batch it
 * ended, so such a type gets its end with the batch's builder still building (its super.end draws it the 1.20.6
 * way, CbRenderTypes.cbEndBody), in place of 1.21's build-sort-draw; the last shared type is forgotten as
 * 1.21 forgets it.
 */
@Mixin(net.minecraft.class_4597.class_4598.class)
public abstract class CbRenderBufferSourceMixin {

    @Shadow
    protected net.minecraft.class_1921 field_52158;

    @Inject(method = "method_60893", at = @At("HEAD"), cancellable = true)
    private void cb$endLegacyBatch(net.minecraft.class_1921 type, net.minecraft.class_287 builder, CallbackInfo ci) {
        if (type instanceof CbRenderTypes.CbLegacyEnd own) {
            own.method_23012(builder, CbRenderTypes.cbSorting());
            if (type.equals(this.field_52158)) {
                this.field_52158 = null;
            }
            ci.cancel();
        }
    }
}
