package top.dext.centurybridge.compat.fapi;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;

/** fabric-entity-api's EntityPickInteractionAware (3 corpus mods): gone in 26.x; kept loadable, not consulted by vanilla pick-block (documented loss). */
public interface CbEntityPickInteractionAware {
    ItemStack getPickedStack(Player player, HitResult result);
}
