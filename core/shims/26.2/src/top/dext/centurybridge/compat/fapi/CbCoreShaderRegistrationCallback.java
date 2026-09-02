package top.dext.centurybridge.compat.fapi;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * fabric-rendering-v1's CoreShaderRegistrationCallback (6 corpus mods). Core
 * shader programs died with the 1.21.5 render-pipeline rework (RenderPipeline
 * objects, no ShaderProgram loading callback), so the event can never fire: the
 * registration is accepted, a warning is printed once, and the mod's shader
 * fields stay null -- the immediate-mode code that would use them is ledgered
 * anyway.
 */
@FunctionalInterface
public interface CbCoreShaderRegistrationCallback {

    Event<CbCoreShaderRegistrationCallback> EVENT = Bridge.create();

    void registerShaders(CbCoreShaderRegistrationContext context) throws IOException;

    final class Bridge {
        private static final AtomicBoolean WARNED = new AtomicBoolean();

        private Bridge() {
        }

        static Event<CbCoreShaderRegistrationCallback> create() {
            Event<CbCoreShaderRegistrationCallback> ev = EventFactory.createArrayBacked(CbCoreShaderRegistrationCallback.class,
                listeners -> context -> {
                });
            if (WARNED.compareAndSet(false, true)) {
                System.err.println("[CenturyBridge] CoreShaderRegistrationCallback never fires on 26.x: core shader programs were replaced by RenderPipelines in 1.21.5");
            }
            return ev;
        }
    }
}
