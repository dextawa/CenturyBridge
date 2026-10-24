package top.dext.centurybridge.step.v1_21_4.compat.fapi.rendering.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

/**
 * Fabric API 1.21.3's WorldRenderContext members on 1.21.4's: lightmapTextureManager() is gone (1.21.4's level rendering takes
 * no LightTexture); translucentBlockOutline() is new (1.21.4 draws the block outline in a solid and a translucent pass).
 */
@Environment(EnvType.CLIENT)
public final class CbFapiWorldRenderContext {
    private CbFapiWorldRenderContext() {
    }

    /** was lightmapTextureManager(): the game renderer's LightTexture, which 1.21.3's context carried */
    public static net.minecraft.class_765 lightmapTextureManager(WorldRenderContext context) {
        return context.gameRenderer().method_22974();
    }

    /** a 1.21.3 implementation's translucentBlockOutline(): 1.21.3 drew the outline once, in no translucent pass */
    public static boolean translucentBlockOutline(WorldRenderContext context) {
        return false;
    }
}
