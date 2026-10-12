package top.dext.centurybridge.compat.fapi.impl.networking.v0;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Objects;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import top.dext.centurybridge.compat.fapi.impl.client.networking.v0.CbFapiClientNetworking;
import top.dext.centurybridge.compat.fapi.network.ClientSidePacketRegistry;
import top.dext.centurybridge.compat.fapi.network.PacketConsumer;

/**
 * 1.20.1's ClientSidePacketRegistryImpl, on 1.20.2's networking v1. It loads on either side as 1.20.1's
 * did; what needs the client (the game client, its connection, receivers handed client objects) is in
 * CbFapiClientNetworking, reached only from these methods.
 */
public final class ClientSidePacketRegistryImpl implements ClientSidePacketRegistry {

    @Override
    public boolean canServerReceive(net.minecraft.class_2960 id) {
        return ClientPlayNetworking.getSendable().contains(id);
    }

    @Override
    public void sendToServer(net.minecraft.class_2596<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener) {
        CbFapiClientNetworking.sendToServer(packet, completionListener);
    }

    @Override
    public net.minecraft.class_2596<?> toPacket(net.minecraft.class_2960 id, net.minecraft.class_2540 buf) {
        return ClientPlayNetworking.createC2SPacket(id, buf);
    }

    @Override
    public void register(net.minecraft.class_2960 id, PacketConsumer consumer) {
        Objects.requireNonNull(consumer, "PacketConsumer cannot be null");
        CbFapiClientNetworking.register(id, consumer);
    }

    @Override
    public void unregister(net.minecraft.class_2960 id) {
        ClientPlayNetworking.unregisterGlobalReceiver(id);
    }
}
