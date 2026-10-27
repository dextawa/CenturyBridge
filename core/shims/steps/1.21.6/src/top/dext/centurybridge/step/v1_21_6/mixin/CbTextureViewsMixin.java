package top.dext.centurybridge.step.v1_21_6.mixin;

/**
 * A GlTexture's close also closes the full view CbTextures made for 1.21.5 callers (render passes and presenting take
 * views in 1.21.6). 1.21.6's GlTexture deletes its GL texture once it is closed and no view is open (javap:
 * close / removeView), so the texture still goes away. Every return of close (it returns early when already closed).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(net.minecraft.class_10868.class)
public abstract class CbTextureViewsMixin {

    @org.spongepowered.asm.mixin.injection.Inject(method = "close()V", at = @org.spongepowered.asm.mixin.injection.At("RETURN"))
    private void cb$closeLegacyView(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        top.dext.centurybridge.step.v1_21_6.rt.CbTextures.closed((com.mojang.blaze3d.textures.GpuTexture) (Object) this);
    }
}
