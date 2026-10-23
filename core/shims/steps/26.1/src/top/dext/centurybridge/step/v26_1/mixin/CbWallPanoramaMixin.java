package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Panorama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallPanorama;

/** the panorama state a 1.21.11 mod's PanoramaRenderer just extracted, with its cube map (rt/CbWallPanorama) */
@Environment(EnvType.CLIENT)
@Mixin(Panorama.class)
public abstract class CbWallPanoramaMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void cb$ownCubeMap(GuiGraphicsExtractor graphics, int width, int height, boolean spin, CallbackInfo ci) {
        CbWallPanorama.extracted((Panorama) (Object) this,
                Minecraft.getInstance().gameRenderer.getGameRenderState().guiRenderState.panoramaRenderState);
    }
}
