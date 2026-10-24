package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** GameRenderer's GUI renderer (rt/CbWallPanorama: the panorama cube map 1.21.11's GameRenderer held) */
@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public interface CbWallGameRendererGuiAccess {

    @Accessor("guiRenderer")
    GuiRenderer cb$guiRenderer();
}
