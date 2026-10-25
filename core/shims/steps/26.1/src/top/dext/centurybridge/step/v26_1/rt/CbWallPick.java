package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The 1.21.11 GameRenderer.pick(partialTicks) the layer restores on GameRenderer (mixin/CbWallGameRendererMixin), as
 * an interface GameRenderer implements, so Minecraft's own pick calls can go through it (mixin/CbWallMinecraftPickMixin).
 */
@Environment(EnvType.CLIENT)
public interface CbWallPick {

    void pick(float partialTicks);
}
