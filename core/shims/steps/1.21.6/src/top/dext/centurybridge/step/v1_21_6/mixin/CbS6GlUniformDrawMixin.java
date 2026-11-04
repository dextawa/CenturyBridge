package top.dext.centurybridge.step.v1_21_6.mixin;

/**
 * 1.21.5 read a dirty uniform's current array contents during draw setup. Flush in-place edits at the same boundary,
 * before 1.21.6 binds the corresponding native slices.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(net.minecraft.class_10860.class)
public abstract class CbS6GlUniformDrawMixin {

    @org.spongepowered.asm.mixin.injection.Inject(
        method = "method_68351(Lnet/minecraft/class_10866;Ljava/util/Collection;)Z",
        at = @org.spongepowered.asm.mixin.injection.At("HEAD"))
    private void cb$flushLegacyArrays(net.minecraft.class_10866 pass, java.util.Collection<String> dirty,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        top.dext.centurybridge.step.v1_21_6.rt.CbS6GlUniforms.flush(pass);
    }
}
