package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;

/** fabric-events-interaction-v0's ClientPickBlockGatherCallback (1 corpus mod): gone in 26.x; the event never fires (documented loss). */
@FunctionalInterface
public interface CbClientPickBlockGatherCallback {

    Event<CbClientPickBlockGatherCallback> EVENT = EventFactory.createArrayBacked(CbClientPickBlockGatherCallback.class,
        listeners -> (player, result) -> ItemStack.EMPTY);

    ItemStack pick(Player player, HitResult result);
}
