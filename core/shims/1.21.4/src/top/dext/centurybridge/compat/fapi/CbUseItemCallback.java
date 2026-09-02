package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import top.dext.centurybridge.facade.v1_21_4.class_1271;

/**
 * fabric-events-interaction-v0's UseItemCallback as 1.20.x mods implement it: the listener answered a
 * TypedActionResult&lt;ItemStack&gt; (facade class_1271); since 1.21.2 it answers an ActionResult. A 1.20.1
 * lambda's SAM type can never satisfy the real interface (AbstractMethodError at first use), so the converter
 * renames the interface here; registrations land on our own array-backed event, and one real listener forwards
 * into it, translating the answer (Success carries the transformed held item).
 * classRename: net/fabricmc/fabric/api/event/player/UseItemCallback -> this interface.
 */
@FunctionalInterface
public interface CbUseItemCallback {

    Event<CbUseItemCallback> EVENT = Bridge.create();

    class_1271<class_1799> interact(class_1657 player, class_1937 world, class_1268 hand);

    final class Bridge {
        private Bridge() {
        }

        static Event<CbUseItemCallback> create() {
            Event<CbUseItemCallback> ev = EventFactory.createArrayBacked(CbUseItemCallback.class,
                listeners -> (player, world, hand) -> {
                    for (CbUseItemCallback l : listeners) {
                        class_1271<class_1799> r = l.interact(player, world, hand);
                        if (r != null && r.method_5467() != class_1269.field_5811) {
                            return r;
                        }
                    }
                    return class_1271.method_22430(class_1799.field_8037);
                });
            UseItemCallback.EVENT.register((player, world, hand) -> {
                class_1271<class_1799> r = ev.invoker().interact(player, world, hand);
                return r == null ? class_1269.field_5811 : r.cbToVanilla();
            });
            return ev;
        }
    }
}
