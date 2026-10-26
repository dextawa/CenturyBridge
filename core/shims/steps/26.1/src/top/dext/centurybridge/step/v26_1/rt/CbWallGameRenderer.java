package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.platform.WindowEventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.resources.model.ModelManager;
import top.dext.centurybridge.step.v26_1.facade.BlockRenderDispatcher;

/**
 * 1.21.11's GameRenderer and Minecraft surface that is not an instance member 26.1 can carry (those are on
 * the classes themselves: mixin/CbWallGameRendererMixin, mixin/CbWallMinecraftMixin).
 */
@Environment(EnvType.CLIENT)
public final class CbWallGameRenderer {

    private CbWallGameRenderer() {
    }

    /**
     * new GameRenderer(minecraft, itemInHandRenderer, renderBuffers, blockRenderDispatcher): 26.1 takes the
     * model manager the dispatcher's models come from (its block model shaper's).
     */
    public static ModelManager modelManager(BlockRenderDispatcher dispatcher) {
        return dispatcher.method_3351().method_3333();
    }

    /** GameRenderer.PROJECTION_Z_NEAR: 26.1's Camera.PROJECTION_Z_NEAR (both 0.05) */
    public static float field_32686() {
        return Camera.PROJECTION_Z_NEAR;
    }

    /**
     * WindowEventHandler.setWindowActive(active), which 26.1's interface no longer has (its Window keeps the
     * focus flag itself): the handler's own setWindowActive -- Minecraft's is given back by
     * CbWallMinecraftMixin, a mod's implementation gets {@link CbWallMinecraftWindow} (adapterHooks) -- as
     * the interface call reached it in 1.21.11.
     */
    public static void setWindowActive(WindowEventHandler handler, boolean active) {
        if (!(handler instanceof CbWallMinecraftWindow w)) {
            throw new UnsupportedOperationException("1.21.11 WindowEventHandler.setWindowActive on " + handler.getClass().getName()
                    + ", which declares no setWindowActive: 26.1's WindowEventHandler has no such method to fall back to");
        }
        w.setWindowActive(active);
    }

    /** 1.21.11's WindowEventHandler.setWindowActive, on Minecraft (CbWallMinecraftMixin) and a mod's handlers (adapterHooks) */
    public interface CbWallMinecraftWindow {
        void setWindowActive(boolean active);
    }
}
