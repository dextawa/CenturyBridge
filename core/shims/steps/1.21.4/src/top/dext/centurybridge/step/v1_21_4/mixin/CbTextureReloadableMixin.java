package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_4.rt.CbTextures;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * A ReloadableTexture's 1.21.3 load(resourceManager): its contents, applied -- 1.21.3's SimpleTexture.load read the image and
 * its texture metadata and uploaded it with that blur / clamp, which 1.21.4's loadContents and apply do (see {@link CbTextures}).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_10537.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbTextureReloadableMixin extends net.minecraft.class_1044 {

    @Shadow
    public abstract net.minecraft.class_10539 method_65809(net.minecraft.class_3300 resourceManager) throws java.io.IOException;

    @Shadow
    public abstract void method_65857(net.minecraft.class_10539 contents);

    /** was load(resourceManager): as 1.21.4's registerAndLoad loads one (apply uploads the image and releases it) */
    public void method_4625(net.minecraft.class_3300 resourceManager) throws java.io.IOException {
        this.method_65857(this.method_65809(resourceManager));
    }
}
