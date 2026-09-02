package top.dext.centurybridge.compat.fapi;

import java.util.function.Function;

import net.minecraft.class_2540;
import net.minecraft.class_2960;

/** Stand-in for fabric-api's removed PacketType (see CbFabricPacket). */
public final class CbPacketType<T extends CbFabricPacket> {
    private final class_2960 id;
    private final Function<class_2540, T> constructor;

    private CbPacketType(class_2960 id, Function<class_2540, T> constructor) {
        this.id = id;
        this.constructor = constructor;
    }

    public static <P extends CbFabricPacket> CbPacketType<P> create(class_2960 id, Function<class_2540, P> constructor) {
        return new CbPacketType<>(id, constructor);
    }

    public class_2960 getId() {
        return id;
    }

    public T read(class_2540 buf) {
        return constructor.apply(buf);
    }
}
