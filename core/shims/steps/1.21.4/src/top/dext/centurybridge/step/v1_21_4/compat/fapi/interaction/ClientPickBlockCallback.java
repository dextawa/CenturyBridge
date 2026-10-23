package top.dext.centurybridge.step.v1_21_4.compat.fapi.interaction;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;

/**
 * Fabric API 1.21.3's ClientPickBlockCallback (deprecated there, gone in 1.21.4): fills a container with the stack to pick,
 * asked after the gather callback found nothing; a false answer stops the remaining listeners. Asked where 1.21.3's client asked
 * it (CbFapiPick).
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ClientPickBlockCallback {
    Event<ClientPickBlockCallback> EVENT = CbFapiPick.event(ClientPickBlockCallback.class, listeners -> (player, result, container) -> {
        for (ClientPickBlockCallback listener : listeners) {
            if (!listener.pick(player, result, container)) {
                return false;
            }
        }
        return true;
    });

    boolean pick(net.minecraft.class_1657 player, net.minecraft.class_239 result, Container container);

    /** ClientPickBlockCallback.Container: the stack being picked */
    final class Container {
        private net.minecraft.class_1799 stack;

        public Container(net.minecraft.class_1799 stack) {
            this.stack = stack;
        }

        public net.minecraft.class_1799 getStack() {
            return this.stack;
        }

        public void setStack(net.minecraft.class_1799 stack) {
            this.stack = stack;
        }
    }
}
