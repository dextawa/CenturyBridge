package top.dext.centurybridge.compat.fapi.impl.client.networking.v0;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.Event;
import top.dext.centurybridge.compat.fapi.event.network.S2CPacketTypeCallback;
import top.dext.centurybridge.compat.fapi.network.PacketConsumer;
import top.dext.centurybridge.compat.fapi.network.PacketContext;

/** The client half of networking v0 on 1.20.2's networking v1 (1.20.1's ClientSidePacketRegistryImpl and OldClientNetworkingHooks). */
@Environment(EnvType.CLIENT)
public final class CbFapiClientNetworking {

    private CbFapiClientNetworking() {
    }

    /** 1.20.1 refused a send outside a game the same way, then sent on the game connection with the listener attached */
    public static void sendToServer(net.minecraft.class_2596<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener) {
        if (net.minecraft.class_310.method_1551().method_1562() == null) {
            throw new IllegalStateException("Cannot send packet to server while not in game!");
        }
        ClientPlayNetworking.getSender().sendPacket(packet, completionListener);
    }

    /** a v0 receiver is a global v1 play-channel receiver, handed a context of the game client */
    public static void register(net.minecraft.class_2960 id, PacketConsumer consumer) {
        ClientPlayNetworking.registerGlobalReceiver(id, (client, handler, buf, sender) -> consumer.accept(new Context(client), buf));
    }

    /** OldClientNetworkingHooks: the server's channel (un)registrations reach the v0 event */
    public static void forwardChannelEvents(Event<S2CPacketTypeCallback> registered, Event<S2CPacketTypeCallback> unregistered) {
        C2SPlayChannelEvents.REGISTER.register((handler, sender, client, channels) -> registered.invoker().accept(channels));
        C2SPlayChannelEvents.UNREGISTER.register((handler, sender, client, channels) -> unregistered.invoker().accept(channels));
    }

    @Environment(EnvType.CLIENT)
    private record Context(net.minecraft.class_310 client) implements PacketContext {

        @Override
        public EnvType getPacketEnvironment() {
            return EnvType.CLIENT;
        }

        @Override
        public net.minecraft.class_1657 getPlayer() {
            return client.field_1724;
        }

        @Override
        public net.minecraft.class_1255<?> getTaskQueue() {
            return client;
        }
    }
}
