package top.dext.centurybridge.compat.fapi.impl.networking.v0;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Objects;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import top.dext.centurybridge.compat.fapi.network.PacketConsumer;
import top.dext.centurybridge.compat.fapi.network.PacketContext;
import top.dext.centurybridge.compat.fapi.network.ServerSidePacketRegistry;

/**
 * 1.20.1's ServerSidePacketRegistryImpl, on 1.20.2's networking v1. As in 1.20.1, a v0 receiver is a
 * global v1 play-channel receiver handing the consumer a context of (player, server); sends go through
 * the player's v1 packet sender, which delivers the completion listener the way 1.20.1's connection did.
 * toPacket is v1's createS2CPacket: in 1.20.2 only a payload of Fabric API's own types reaches the
 * receiving side's channel receivers in every connection (a local connection passes the payload object
 * itself), which a 1.20.1 v0 packet always did.
 */
public final class ServerSidePacketRegistryImpl implements ServerSidePacketRegistry {

    @Override
    public boolean canPlayerReceive(net.minecraft.class_1657 player, net.minecraft.class_2960 id) {
        return player instanceof net.minecraft.class_3222 sp && ServerPlayNetworking.canSend(sp, id);
    }

    @Override
    public void sendToPlayer(net.minecraft.class_1657 player, net.minecraft.class_2596<?> packet,
                             GenericFutureListener<? extends Future<? super Void>> completionListener) {
        if (!(player instanceof net.minecraft.class_3222 sp)) {
            throw new RuntimeException("Can only send to ServerPlayerEntities!");
        }
        ServerPlayNetworking.getSender(sp).sendPacket(packet, completionListener);
    }

    @Override
    public net.minecraft.class_2596<?> toPacket(net.minecraft.class_2960 id, net.minecraft.class_2540 buf) {
        return ServerPlayNetworking.createS2CPacket(id, buf);
    }

    @Override
    public void register(net.minecraft.class_2960 id, PacketConsumer consumer) {
        Objects.requireNonNull(consumer, "PacketConsumer cannot be null");
        ServerPlayNetworking.registerGlobalReceiver(id,
            (server, player, handler, buf, sender) -> consumer.accept(new Context(player, server), buf));
    }

    @Override
    public void unregister(net.minecraft.class_2960 id) {
        ServerPlayNetworking.unregisterGlobalReceiver(id);
    }

    /** the context 1.20.1 handed a server-side v0 consumer */
    private record Context(net.minecraft.class_3222 player, net.minecraft.server.MinecraftServer server) implements PacketContext {

        @Override
        public net.fabricmc.api.EnvType getPacketEnvironment() {
            return net.fabricmc.api.EnvType.SERVER;
        }

        @Override
        public net.minecraft.class_1657 getPlayer() {
            return player;
        }

        @Override
        public net.minecraft.class_1255<?> getTaskQueue() {
            return server;
        }
    }
}
