package top.dext.centurybridge.compat.fapi;

import net.minecraft.class_1703;
import net.minecraft.class_2378;
import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_3917;
import net.minecraft.class_7701;
import net.minecraft.class_7923;
import net.minecraft.class_9129;
import net.minecraft.class_9139;

/**
 * fabric-screen-handler-api-v1's ScreenHandlerRegistry (deleted in 1.20.5; 4 corpus mods).
 * registerSimple is a plain vanilla ScreenHandlerType registration (the 1.20.1 factory SAM is
 * the vanilla one, the converter renames it onto ScreenHandlerType.Factory); registerExtended
 * builds a {@link CbExtendedScreenHandlerType} around the buffer-reading factory.
 * classRename: net/fabricmc/fabric/api/screenhandler/v1/ScreenHandlerRegistry -> this class.
 */
public final class CbScreenHandlerRegistry {

    /** the 1.20.1 opening data verbatim: a length-prefixed byte array around the PacketByteBuf */
    public static final class_9139<class_9129, class_2540> RAW = class_9139.method_56437(
        (buf, data) -> {
            byte[] bytes = new byte[data.readableBytes()];
            data.getBytes(data.readerIndex(), bytes);
            buf.method_10813(bytes);
        },
        buf -> new class_2540(io.netty.buffer.Unpooled.wrappedBuffer(buf.method_10795())));

    public static <T extends class_1703> class_3917<T> registerSimple(class_2960 id, class_3917.class_3918<T> factory) {
        return class_2378.method_10230(class_7923.field_41187, id, new class_3917<>(factory, class_7701.field_40182));
    }

    public static <T extends class_1703> class_3917<T> registerExtended(class_2960 id, CbExtendedClientHandlerFactory<T> factory) {
        return class_2378.method_10230(class_7923.field_41187, id,
            new CbExtendedScreenHandlerType<T>((syncId, inventory, buf) -> factory.create(syncId, inventory, buf)));
    }

    private CbScreenHandlerRegistry() {
    }
}
