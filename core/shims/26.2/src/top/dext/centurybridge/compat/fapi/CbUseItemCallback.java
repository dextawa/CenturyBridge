package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.dext.centurybridge.facade.v26_2.class_1271;

/**
 * fabric-events-interaction-v0's UseItemCallback as 1.20.x mods implement it:
 * the listener answered a TypedActionResult&lt;ItemStack&gt;; since 1.21.2
 * it answers an InteractionResult. A 1.20.1 lambda's SAM type (facade
 * return) can never satisfy the real interface, so the converter renames
 * the interface here. Registrations land on our own array-backed event,
 * and one real listener forwards into it, translating the answer.
 */
@FunctionalInterface
public interface CbUseItemCallback {

    Event<CbUseItemCallback> EVENT = Bridge.create();

    class_1271<ItemStack> interact(Player player, Level level, InteractionHand hand);

    final class Bridge {
        private Bridge() {
        }

        static Event<CbUseItemCallback> create() {
            Event<CbUseItemCallback> ev = EventFactory.createArrayBacked(CbUseItemCallback.class,
                listeners -> (player, level, hand) -> {
                    for (CbUseItemCallback l : listeners) {
                        class_1271<ItemStack> r = l.interact(player, level, hand);
                        if (r != null && r.method_5467() != InteractionResult.PASS) {
                            return r;
                        }
                    }
                    return class_1271.method_22430(ItemStack.EMPTY);
                });
            UseItemCallback.EVENT.register((player, level, hand) -> {
                class_1271<ItemStack> r = ev.invoker().interact(player, level, hand);
                return r == null ? InteractionResult.PASS : r.cbToVanilla();
            });
            return ev;
        }
    }
}
