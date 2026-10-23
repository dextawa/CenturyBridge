package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_1.rt.CbWallPanorama;

/**
 * The frame's panorama drawn with the cube map of the panorama that extracted it: a 1.21.11 mod's
 * PanoramaRenderer over its own cube map (rt/CbWallPanorama); every other panorama with the GUI renderer's.
 */
@Environment(EnvType.CLIENT)
@Mixin(GuiRenderer.class)
public abstract class CbWallGuiRendererMixin {

    @Shadow
    @Final
    private GuiRenderState renderState;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/CubeMap;render(FF)V"))
    private void cb$panoramaCubeMap(CubeMap gui, float xRot, float yRot) {
        CbWallPanorama.drawWith(this.renderState.panoramaRenderState, gui).render(xRot, yRot);
    }
}
