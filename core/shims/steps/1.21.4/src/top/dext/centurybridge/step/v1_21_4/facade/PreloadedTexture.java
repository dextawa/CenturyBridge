package top.dext.centurybridge.step.v1_21_4.facade;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.rt.CbTextures;

/**
 * 1.21.3 PreloadedTexture (class_4005, gone in 1.21.4): a SimpleTexture whose image starts loading on an executor when it is
 * made (and again when it is reset), the load taking the loaded image. Body for body; its contents for 1.21.4's apply are its
 * getTextureImage, as for any 1.21.3 SimpleTexture.
 */
@Environment(EnvType.CLIENT)
public class PreloadedTexture extends net.minecraft.class_1049 {
    /** future */
    public CompletableFuture<LegacyTextureImage> field_17894;

    public PreloadedTexture(net.minecraft.class_3300 resourceManager, net.minecraft.class_2960 id, Executor executor) {
        super(id);
        this.field_17894 = CompletableFuture.supplyAsync(() -> LegacyTextureImage.method_18156(resourceManager, id), executor);
    }

    /** getTextureImage(resourceManager): the preloaded image once, then a fresh load */
    public LegacyTextureImage method_18153(net.minecraft.class_3300 resourceManager) {
        if (this.field_17894 != null) {
            LegacyTextureImage image = this.field_17894.join();
            this.field_17894 = null;
            return image;
        }
        return LegacyTextureImage.method_18156(resourceManager, this.method_65859());
    }

    @Override
    public net.minecraft.class_10539 method_65809(net.minecraft.class_3300 resourceManager) {
        return CbTextures.contents(this.method_18153(resourceManager));
    }

    /** getFuture() */
    public CompletableFuture<Void> method_18148() {
        return this.field_17894 == null ? CompletableFuture.completedFuture(null) : this.field_17894.thenApply(image -> null);
    }

    /** reset(manager, resourceManager, id, executor): load again in the background, register (and so load) on the render thread */
    public void method_18169(net.minecraft.class_1060 manager, net.minecraft.class_3300 resourceManager, net.minecraft.class_2960 id, Executor executor) {
        this.field_17894 = CompletableFuture.supplyAsync(() -> LegacyTextureImage.method_18156(resourceManager, this.method_65859()),
            net.minecraft.class_156.method_18349());
        this.field_17894.thenRunAsync(() -> CbTextures.register(manager, this.method_65859(), this),
            method_22808(executor));
    }

    /** the executor that runs on the given one, then on the render thread */
    public static Executor method_22808(Executor executor) {
        return runnable -> executor.execute(() -> com.mojang.blaze3d.systems.RenderSystem.recordRenderCall(runnable::run));
    }
}
