package top.dext.centurybridge.compat.fapi;

import java.util.function.Function;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

/** Stand-in for fabric-api's removed PacketType (see CbFabricPacket). */
public final class CbPacketType<T extends CbFabricPacket> {
    private final Identifier id;
    private final Function<FriendlyByteBuf, T> constructor;

    private CbPacketType(Identifier id, Function<FriendlyByteBuf, T> constructor) {
        this.id = id;
        this.constructor = constructor;
    }

    public static <P extends CbFabricPacket> CbPacketType<P> create(Identifier id, Function<FriendlyByteBuf, P> constructor) {
        return new CbPacketType<>(id, constructor);
    }

    public Identifier getId() {
        return id;
    }

    public T read(FriendlyByteBuf buf) {
        return constructor.apply(buf);
    }
}
