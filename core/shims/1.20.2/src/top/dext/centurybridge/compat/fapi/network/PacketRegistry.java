package top.dext.centurybridge.compat.fapi.network;

/** Fabric API networking v0's PacketRegistry: the channel receivers of one side, and its packet factory. */
public interface PacketRegistry {

    net.minecraft.class_2596<?> toPacket(net.minecraft.class_2960 id, net.minecraft.class_2540 buf);

    void register(net.minecraft.class_2960 id, PacketConsumer consumer);

    void unregister(net.minecraft.class_2960 id);
}
