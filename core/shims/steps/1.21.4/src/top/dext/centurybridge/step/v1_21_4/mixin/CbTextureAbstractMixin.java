package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_4.rt.CbTextures;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * AbstractTexture's 1.21.3 load / reset / getDefaultBlur (see {@link CbTextures}). 1.21.3's load was abstract; every 1.21.3
 * texture 1.21.4 keeps that does not reload from contents (dynamic, atlas, font textures) loaded nothing; a ReloadableTexture
 * loads its contents (CbTextureReloadableMixin).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_1044.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbTextureAbstractMixin implements CbTextures.CbTexture {

    @Shadow
    protected boolean field_5205;

    /** was load(resourceManager) */
    @Override
    public void method_4625(net.minecraft.class_3300 resourceManager) throws java.io.IOException {
    }

    /** was reset(textureManager, resourceManager, id, executor): registered again, so loaded again */
    @Override
    public void method_18169(net.minecraft.class_1060 textureManager, net.minecraft.class_3300 resourceManager, net.minecraft.class_2960 id,
            java.util.concurrent.Executor executor) {
        CbTextures.reset((net.minecraft.class_1044) (Object) this, textureManager, id);
    }

    /** was getDefaultBlur(): the mipmap flag */
    public boolean method_62619() {
        return this.field_5205;
    }
}
