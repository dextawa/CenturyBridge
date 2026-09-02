package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.class_1703;
import net.minecraft.class_2540;

/**
 * fabric-screen-handler-api-v1's ExtendedScreenHandlerType as 1.20.1 mods construct it:
 * {@code new ExtendedScreenHandlerType<>(factory)} with a PacketByteBuf-reading factory. 1.20.5
 * added a mandatory PacketCodec for a typed data value; here the data value IS the 1.20.1 buffer,
 * carried as an opaque byte array (see CbScreenHandlerRegistry.RAW), so the old factory and the
 * old ExtendedScreenHandlerFactory implementors keep their exact wire bytes. Being a subclass, the
 * real API's instanceof checks (client open-screen path) still hold.
 * classRename: net/fabricmc/fabric/api/screenhandler/v1/ExtendedScreenHandlerType -> this class.
 */
public class CbExtendedScreenHandlerType<T extends class_1703> extends ExtendedScreenHandlerType<T, class_2540> {

    public CbExtendedScreenHandlerType(CbExtendedFactory<T> factory) {
        super((syncId, inventory, data) -> factory.create(syncId, inventory, data), CbScreenHandlerRegistry.RAW);
    }
}
