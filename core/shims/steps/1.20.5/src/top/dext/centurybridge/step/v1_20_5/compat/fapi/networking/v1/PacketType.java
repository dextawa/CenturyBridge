package top.dext.centurybridge.step.v1_20_5.compat.fapi.networking.v1;

import java.util.function.Function;

/**
 * Fabric API 0.97.3's (1.20.4) PacketType, removed in 1.20.5: a {@link FabricPacket} class's channel id
 * and reader. Body as in 1.20.4 (Fabric API, Apache-2.0): a reader failure is rethrown naming the
 * channel.
 */
public final class PacketType<T extends FabricPacket> {

    private final net.minecraft.class_2960 id;
    private final Function<net.minecraft.class_2540, T> constructor;

    private PacketType(net.minecraft.class_2960 id, Function<net.minecraft.class_2540, T> constructor) {
        this.id = id;
        this.constructor = constructor;
    }

    public static <P extends FabricPacket> PacketType<P> create(net.minecraft.class_2960 id,
                                                               Function<net.minecraft.class_2540, P> constructor) {
        return new PacketType<>(id, constructor);
    }

    public net.minecraft.class_2960 getId() {
        return id;
    }

    public T read(net.minecraft.class_2540 buf) {
        try {
            return this.constructor.apply(buf);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while handling packet \"%s\": %s".formatted(this.id, e.getMessage()), e);
        }
    }
}
