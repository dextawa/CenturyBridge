package top.dext.centurybridge.step.v1_21_6.mixin;

/**
 * 1.21.5's ReloadableTexture.apply(contents) (method_65857) first kept the contents' blur in AbstractTexture.blur
 * (javap 1.21.5: method_65872, then putfield field_5205, before the upload); 1.21.6's uploads without keeping it. Kept
 * again in the field CbTextureBlurMixin gives back.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(net.minecraft.class_10537.class)
public abstract class CbTextureReloadBlurMixin {

    @org.spongepowered.asm.mixin.injection.Inject(method = "method_65857", at = @org.spongepowered.asm.mixin.injection.At("HEAD"))
    private void cb$keepBlur(net.minecraft.class_10539 contents, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ((top.dext.centurybridge.step.v1_21_6.rt.CbTextureBlur) (Object) this).cb$setBlur(contents.method_65872());
    }
}
