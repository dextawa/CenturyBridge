package top.dext.centurybridge.step.v1_21_6.mixin;

/** Closes only buffers allocated by the legacy uniform view; caller-owned native slices are left alone. */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(net.minecraft.class_10866.class)
public abstract class CbS6GlUniformCloseMixin {

    @org.spongepowered.asm.mixin.injection.Inject(method = "close()V", at = @org.spongepowered.asm.mixin.injection.At("RETURN"))
    private void cb$closeLegacyBuffers(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        top.dext.centurybridge.step.v1_21_6.rt.CbS6GlUniforms.closed((net.minecraft.class_10866) (Object) this);
    }
}
