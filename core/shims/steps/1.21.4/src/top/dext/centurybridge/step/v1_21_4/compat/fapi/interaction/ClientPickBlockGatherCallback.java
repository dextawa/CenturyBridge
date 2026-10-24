package top.dext.centurybridge.step.v1_21_4.compat.fapi.interaction;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;

/**
 * Fabric API 1.21.3's ClientPickBlockGatherCallback (gone in 1.21.4, which picks on the server): the stack a pick should give,
 * asked first; the first non-empty answer is picked as 1.21.3's client picked it (CbFapiPick).
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ClientPickBlockGatherCallback {
    Event<ClientPickBlockGatherCallback> EVENT = CbFapiPick.event(ClientPickBlockGatherCallback.class, listeners -> (player, result) -> {
        for (ClientPickBlockGatherCallback listener : listeners) {
            net.minecraft.class_1799 stack = listener.pick(player, result);
            if (stack != net.minecraft.class_1799.field_8037 && !stack.method_7960()) {
                return stack;
            }
        }
        return net.minecraft.class_1799.field_8037;
    });

    net.minecraft.class_1799 pick(net.minecraft.class_1657 player, net.minecraft.class_239 result);
}
