package top.dext.centurybridge.step.v1_21_6.mixin;

/**
 * 1.21.5's AbstractTexture.blur (field_5205, protected), which 1.21.6 dropped (javap: 1.21.6 keeps only the GpuTexture
 * and its view). 1.21.5 set it in ReloadableTexture.apply from the contents' blur (CbTextureReloadBlurMixin) and read it
 * as the default blur of setFilter(contents, mipmap); left false elsewhere, as 1.21.5's. Mods' textures and the climbed
 * 1.21.4 layer's getDefaultBlur (CbTextureAbstractMixin) read and write it under its own name. Priority 0, below every
 * mixin shadowing it: Mixin applies a target's mixins in priority order, and the climbed CbTextureAbstractMixin restores
 * members at the 1.21.4 layer's CbVanillaReads.PRIORITY (1). At 500 the 1.21.5 -> 1.21.6 step strips that restorer (its
 * shadow would fail at apply) and with it every texture's CbTexture, which 1.21.4's TextureManager.register casts to.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(value = net.minecraft.class_1044.class, priority = 0)
public abstract class CbTextureBlurMixin implements top.dext.centurybridge.step.v1_21_6.rt.CbTextureBlur {

    /** blur */
    protected boolean field_5205;

    @Override
    public void cb$setBlur(boolean blur) {
        this.field_5205 = blur;
    }
}
