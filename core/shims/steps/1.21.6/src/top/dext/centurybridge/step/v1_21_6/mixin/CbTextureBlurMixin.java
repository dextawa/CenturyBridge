package top.dext.centurybridge.step.v1_21_6.mixin;

/**
 * 1.21.5's AbstractTexture.blur (field_5205, protected), which 1.21.6 dropped (javap: 1.21.6 keeps only the GpuTexture
 * and its view). 1.21.5 set it in ReloadableTexture.apply from the contents' blur (CbTextureReloadBlurMixin) and read it
 * as the default blur of setFilter(contents, mipmap); left false elsewhere, as 1.21.5's. Mods' textures and the climbed
 * 1.21.4 layer's getDefaultBlur (CbTextureAbstractMixin) read and write it under its own name. Priority below the
 * default so the field is there before any mixin shadowing it (the climbed CbTextureAbstractMixin) is applied.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(value = net.minecraft.class_1044.class, priority = 500)
public abstract class CbTextureBlurMixin implements top.dext.centurybridge.step.v1_21_6.rt.CbTextureBlur {

    /** blur */
    protected boolean field_5205;

    @Override
    public void cb$setBlur(boolean blur) {
        this.field_5205 = blur;
    }
}
