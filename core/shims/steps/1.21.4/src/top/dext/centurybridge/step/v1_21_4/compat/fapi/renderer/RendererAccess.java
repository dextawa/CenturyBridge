package top.dext.centurybridge.step.v1_21_4.compat.fapi.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;

/**
 * Fabric API 1.21.3's RendererAccess on 1.21.4, where the renderer is registered and asked through Renderer.register / Renderer.get.
 * 1.21.3's answered null (and hasRenderer false) before a renderer registered; 1.21.4's get() throws then.
 */
@Environment(EnvType.CLIENT)
public interface RendererAccess {
    RendererAccess INSTANCE = new RendererAccess() {
        @Override
        public void registerRenderer(Renderer renderer) {
            if (renderer == null) {
                throw new NullPointerException("Attempt to register a NULL rendering plug-in.");
            }
            Renderer.register(renderer);
        }

        @Override
        public Renderer getRenderer() {
            try {
                return Renderer.get();
            } catch (UnsupportedOperationException none) {
                return null;
            }
        }

        @Override
        public boolean hasRenderer() {
            return this.getRenderer() != null;
        }
    };

    void registerRenderer(Renderer renderer);

    Renderer getRenderer();

    boolean hasRenderer();
}
