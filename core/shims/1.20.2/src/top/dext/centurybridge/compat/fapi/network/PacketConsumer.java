package top.dext.centurybridge.compat.fapi.network;

/**
 * Fabric API networking v0's PacketConsumer (fabric-networking-v0, removed with 1.20.2's Fabric API):
 * a v0 receiver of one channel's payload. References to the Fabric API name are renamed here
 * (classRenames); the registries in this package hand it the payload as 1.20.1's v0 did.
 */
@FunctionalInterface
public interface PacketConsumer {

    void accept(PacketContext context, net.minecraft.class_2540 buffer);
}
