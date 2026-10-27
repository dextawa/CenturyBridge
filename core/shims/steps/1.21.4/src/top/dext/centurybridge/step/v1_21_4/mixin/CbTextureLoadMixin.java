package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * 1.21.3's load(resourceManager) of DynamicTexture, TextureAtlas and FontTexture, each its own (a 1.21.3 mod's injector
 * selects the class's own method): 1.21.3's bodies, empty. 1.21.4's TextureManager.register enters it, as 1.21.3's did
 * (CbTextureManagerMixinHooks.cb$loadOnRegister).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = {net.minecraft.class_1043.class, net.minecraft.class_1059.class, net.minecraft.class_380.class}, priority = CbVanillaReads.PRIORITY)
public abstract class CbTextureLoadMixin {

    /** was load(resourceManager): 1.21.3's body (nothing) */
    @CbVanillaReads("CbTextureManagerMixinHooks: 1.21.4's TextureManager.register loads the texture through it first (cb$loadOnRegister), as 1.21.3's register did")
    public void method_4625(net.minecraft.class_3300 resourceManager) {
    }
}
