package top.dext.centurybridge.compat.fapi.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * Fabric API networking v0's ClientSidePacketRegistry: receivers of server-to-client payloads and the
 * client's sends, as 1.20.1's v0 module defined them. Like 1.20.1's, the instance exists on either side;
 * using it needs a client (its client work is in impl/client/networking/v0).
 */
public interface ClientSidePacketRegistry extends PacketRegistry {

    ClientSidePacketRegistry INSTANCE = new top.dext.centurybridge.compat.fapi.impl.networking.v0.ClientSidePacketRegistryImpl();

    boolean canServerReceive(net.minecraft.class_2960 id);

    void sendToServer(net.minecraft.class_2596<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener);

    default void sendToServer(net.minecraft.class_2960 id, net.minecraft.class_2540 buf,
                              GenericFutureListener<? extends Future<? super Void>> completionListener) {
        sendToServer(toPacket(id, buf), completionListener);
    }

    default void sendToServer(net.minecraft.class_2596<?> packet) {
        sendToServer(packet, null);
    }

    default void sendToServer(net.minecraft.class_2960 id, net.minecraft.class_2540 buf) {
        sendToServer(id, buf, null);
    }
}
