package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.CubeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** GuiRenderer's panorama cube map (rt/CbWallPanorama) */
@Environment(EnvType.CLIENT)
@Mixin(GuiRenderer.class)
public interface CbWallGuiRendererAccess {

    @Accessor("cubeMap")
    CubeMap cb$cubeMap();
}
