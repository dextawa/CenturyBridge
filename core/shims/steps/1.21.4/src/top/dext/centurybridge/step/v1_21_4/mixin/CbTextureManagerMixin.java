package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.facade.PreloadedTexture;
import top.dext.centurybridge.step.v1_21_4.rt.CbTextures;

/**
 * TextureManager's 1.21.3 members (see {@link CbTextures}): getTexture(id, default), register(name, dynamicTexture), preload,
 * and the reset on reload of the textures of mods' own classes, which 1.21.4's reload of reloadable textures leaves to it.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1060.class)
public abstract class CbTextureManagerMixin implements CbTextures.CbTextureManager {

    @Shadow
    @Final
    private Map<net.minecraft.class_2960, net.minecraft.class_1044> field_5286;

    @Shadow
    @Final
    private net.minecraft.class_3300 field_5287;

    /** 1.21.3 prebakedTextures: register(name, texture)'s counter per name */
    private final Map<String, Integer> cb$dynamicIds = new HashMap<>();

    @Override
    public net.minecraft.class_3300 cb$resourceManager() {
        return this.field_5287;
    }

    private net.minecraft.class_1060 cb$self() {
        return (net.minecraft.class_1060) (Object) this;
    }

    /** was getTexture(id, defaultTexture) */
    public net.minecraft.class_1044 method_34590(net.minecraft.class_2960 id, net.minecraft.class_1044 defaultTexture) {
        return this.field_5286.getOrDefault(id, defaultTexture);
    }

    /** was register(name, texture): minecraft:dynamic/name_n, n counting from 1 per name */
    public net.minecraft.class_2960 method_4617(String name, net.minecraft.class_1043 texture) {
        Integer n = this.cb$dynamicIds.get(name);
        n = n == null ? 1 : n + 1;
        this.cb$dynamicIds.put(name, n);
        net.minecraft.class_2960 id = net.minecraft.class_2960.method_60656(String.format(java.util.Locale.ROOT, "dynamic/%s_%d", name, n));
        CbTextures.register(cb$self(), id, texture);
        return id;
    }

    /** was preload(id, executor) */
    public CompletableFuture<Void> method_18168(net.minecraft.class_2960 id, Executor executor) {
        if (!this.field_5286.containsKey(id)) {
            PreloadedTexture texture = new PreloadedTexture(this.field_5287, id, executor);
            this.field_5286.put(id, texture);
            return texture.method_18148().thenRunAsync(() -> CbTextures.register(cb$self(), id, texture),
                runnable -> com.mojang.blaze3d.systems.RenderSystem.recordRenderCall(runnable::run));
        }
        return CompletableFuture.completedFuture(null);
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
        CbTextures.resetLegacy(cb$self(), this.field_5286);
    }
}
