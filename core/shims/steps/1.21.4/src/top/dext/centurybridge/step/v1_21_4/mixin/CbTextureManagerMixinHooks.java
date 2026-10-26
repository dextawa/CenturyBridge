package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.Map;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbTextures;

/**
 * The injectors into 1.21.4's own TextureManager methods that go with {@link CbTextureManagerMixin} (its restored 1.21.3
 * members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.3 injectors can anchor inside them): the
 * load on register, and the reset on reload of the textures of mods' own classes.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1060.class)
public abstract class CbTextureManagerMixinHooks {

    @Shadow
    @Final
    private Map<net.minecraft.class_2960, net.minecraft.class_1044> field_5286;

    /**
     * 1.21.3's register(id, texture) loaded the texture first (loadTexture: texture.load(resourceManager)); 1.21.4's
     * register of a texture that does not reload from contents does so again, through the 1.21.3 load the layer restores
     * on DynamicTexture, TextureAtlas and FontTexture (CbTextureLoadMixin; 1.21.3's bodies, empty). With no mod mixin
     * the load does nothing and register goes on unchanged: vanilla is unchanged. A ReloadableTexture is loaded from its
     * contents by 1.21.4 (registerAndLoad / reload); a texture CbTextures.register already loaded is not loaded again.
     */
    @Inject(method = "method_4616", at = @At("HEAD"), cancellable = true)
    private void cb$loadOnRegister(net.minecraft.class_2960 id, net.minecraft.class_1044 texture, CallbackInfo ci) {
        if (texture instanceof net.minecraft.class_10537 || CbTextures.loaded()) {
            return;
        }
        net.minecraft.class_1060 self = (net.minecraft.class_1060) (Object) this;
        net.minecraft.class_1044 loaded = CbTextures.loadOnRegister(self, id, texture);
        if (loaded != texture) {
            ci.cancel();
            CbTextures.registerLoaded(self, id, loaded);
        }
    }

    /** a texture of a mod's own class reloads as 1.21.3 reloaded every texture (reset), not from contents */
    @Inject(method = "method_65879", at = @At("HEAD"), cancellable = true)
    private static void cb$skipLegacy(java.util.List<?> pending, net.minecraft.class_3300 resourceManager, Executor executor, net.minecraft.class_2960 id,
            net.minecraft.class_1044 texture, CallbackInfo ci) {
        if (CbTextures.legacy(texture)) {
            ci.cancel();
        }
    }

    @Inject(method = "method_65880", at = @At("TAIL"))
    private void cb$resetLegacy(java.util.List<?> pending, Void unused, CallbackInfo ci) {
        CbTextures.resetLegacy((net.minecraft.class_1060) (Object) this, this.field_5286);
    }
}
