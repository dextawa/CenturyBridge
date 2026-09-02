package top.dext.centurybridge.compat.fapi;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/** was ExtendedScreenHandlerType.ExtendedFactory / ScreenHandlerRegistry.ExtendedClientHandlerFactory (SAM create) */
@FunctionalInterface
public interface CbExtendedFactory<T extends AbstractContainerMenu> {
    T create(int syncId, Inventory inventory, FriendlyByteBuf buf);
}
