package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * fabric-screen-handler-api-v1's ExtendedScreenHandlerType as 1.20.1 mods construct it
 * ({@code new ExtendedScreenHandlerType<>(factory)}, 12 corpus mods). 1.20.5 added a mandatory
 * stream codec for a typed data value and 26.x renamed the class to ExtendedMenuType; here the
 * data value IS the 1.20.1 buffer, carried as an opaque byte array (CbScreenHandlerRegistry.RAW),
 * so the old factory and the old ExtendedScreenHandlerFactory implementors keep their exact wire
 * bytes. Being a subclass, the real API's instanceof checks (client open-screen path) still hold.
 */
public class CbExtendedScreenHandlerType<T extends AbstractContainerMenu> extends ExtendedMenuType<T, FriendlyByteBuf> {

    public CbExtendedScreenHandlerType(CbExtendedFactory<T> factory) {
        super((syncId, inventory, data) -> factory.create(syncId, inventory, data), CbScreenHandlerRegistry.RAW);
    }
}
