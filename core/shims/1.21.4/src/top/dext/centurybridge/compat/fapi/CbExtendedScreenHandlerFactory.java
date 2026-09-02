package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.class_2540;
import net.minecraft.class_3222;

/**
 * The 1.20.1 ExtendedScreenHandlerFactory contract: implementors wrote their screen-opening data
 * into a PacketByteBuf ({@code writeScreenOpeningData}). Since 1.20.5 the real interface asks for a
 * typed value ({@code D getScreenOpeningData(player)}), which a 1.20.1 class never implements
 * (AbstractMethodError at container open). The converter renames the interface here: the old
 * abstract method stays abstract, the new one is derived from it, and the data type is the raw
 * buffer that {@link CbExtendedScreenHandlerType} transports byte-for-byte.
 */
public interface CbExtendedScreenHandlerFactory extends ExtendedScreenHandlerFactory<class_2540> {

    void writeScreenOpeningData(class_3222 player, class_2540 buf);

    @Override
    default class_2540 getScreenOpeningData(class_3222 player) {
        class_2540 buf = PacketByteBufs.create();
        writeScreenOpeningData(player, buf);
        return buf;
    }
}
