package top.dext.centurybridge.compat.fapi;

/**
 * Stand-in for fabric-api's removed ClientPlayNetworking$PlayChannelHandler
 * (raw-buf S2C receiver, deleted in the 1.20.5 typed-payload rewrite). The
 * converter classRenames the old interface here, so mods' lambdas and
 * implementations keep their exact old shape.
 */
public interface CbClientPlayHandler {
    void receive(net.minecraft.class_310 client,
                 net.minecraft.class_634 handler,
                 net.minecraft.class_2540 buf,
                 net.fabricmc.fabric.api.networking.v1.PacketSender responseSender);
}
