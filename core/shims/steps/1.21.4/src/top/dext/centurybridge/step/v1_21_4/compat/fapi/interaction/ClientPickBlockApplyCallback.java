package top.dext.centurybridge.step.v1_21_4.compat.fapi.interaction;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;

/**
 * Fabric API 1.21.3's ClientPickBlockApplyCallback (gone in 1.21.4): changes the stack a pick gives; an empty answer cancels the
 * pick. Asked where 1.21.3's client asked it (CbFapiPick).
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ClientPickBlockApplyCallback {
    Event<ClientPickBlockApplyCallback> EVENT = CbFapiPick.event(ClientPickBlockApplyCallback.class, listeners -> (player, result, stack) -> {
        for (ClientPickBlockApplyCallback listener : listeners) {
            stack = listener.pick(player, result, stack);
            if (stack.method_7960()) {
                return stack;
            }
        }
        return stack;
    });

    net.minecraft.class_1799 pick(net.minecraft.class_1657 player, net.minecraft.class_239 result, net.minecraft.class_1799 stack);
}
