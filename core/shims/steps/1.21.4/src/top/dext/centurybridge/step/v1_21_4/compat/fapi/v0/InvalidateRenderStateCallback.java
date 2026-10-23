package top.dext.centurybridge.step.v1_21_4.compat.fapi.v0;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Fabric API's fabric-rendering-v0 InvalidateRenderStateCallback (the module is gone in 1.21.4's Fabric API): fired
 * when the world renderer reloads its render state. v0 forwarded rendering-v1's event to it; so does this one,
 * from the moment the class is first used (nothing can listen before).
 */
@Environment(EnvType.CLIENT)
public interface InvalidateRenderStateCallback {

    Event<InvalidateRenderStateCallback> EVENT = Forward.bind(EventFactory.createArrayBacked(InvalidateRenderStateCallback.class,
        listeners -> () -> {
            for (InvalidateRenderStateCallback l : listeners) {
                l.onInvalidate();
            }
        }));

    void onInvalidate();

    /** v0's RenderingCallbackInvoker: rendering-v1's event invokes v0's */
    final class Forward {
        private Forward() {
        }

        static Event<InvalidateRenderStateCallback> bind(Event<InvalidateRenderStateCallback> event) {
            net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback.EVENT.register(() -> event.invoker().onInvalidate());
            return event;
        }
    }
}
