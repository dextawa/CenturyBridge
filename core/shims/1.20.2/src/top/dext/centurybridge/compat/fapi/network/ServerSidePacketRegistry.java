package top.dext.centurybridge.compat.fapi.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * Fabric API networking v0's ServerSidePacketRegistry: receivers of client-to-server payloads and the
 * server's sends, as 1.20.1's v0 module defined them (defaults included, for implementors' super calls).
 * The instance runs on 1.20.2's networking v1 (impl/networking/v0).
 */
public interface ServerSidePacketRegistry extends PacketRegistry {

    ServerSidePacketRegistry INSTANCE = new top.dext.centurybridge.compat.fapi.impl.networking.v0.ServerSidePacketRegistryImpl();

    boolean canPlayerReceive(net.minecraft.class_1657 player, net.minecraft.class_2960 id);

    void sendToPlayer(net.minecraft.class_1657 player, net.minecraft.class_2596<?> packet,
                      GenericFutureListener<? extends Future<? super Void>> completionListener);

    default void sendToPlayer(net.minecraft.class_1657 player, net.minecraft.class_2960 id, net.minecraft.class_2540 buf,
                              GenericFutureListener<? extends Future<? super Void>> completionListener) {
        sendToPlayer(player, toPacket(id, buf), completionListener);
    }

    default void sendToPlayer(net.minecraft.class_1657 player, net.minecraft.class_2596<?> packet) {
        sendToPlayer(player, packet, null);
    }

    default void sendToPlayer(net.minecraft.class_1657 player, net.minecraft.class_2960 id, net.minecraft.class_2540 buf) {
        sendToPlayer(player, id, buf, null);
    }
}
