package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.networking;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.networking.v1.FabricPacket;

/**
 * The 1.20.4 shape of a PacketSender implementation (Fabric API 0.97.3), for a mod class that implements
 * PacketSender the 1.20.4 way. The converter adds this interface to such a class (overrideHooks bridge
 * mode, on the class declaring the 1.20.4 abstract {@code createPacket(Identifier, PacketByteBuf)}): the
 * class's own methods implement the abstract ones, and the defaults are 1.20.4's (Fabric API, Apache-2.0)
 * -- the class keeps any of them it overrides, which 1.20.4 callers reach through {@link CbPacketSenders}.
 *
 * <p>Only the defaults 1.20.5's PacketSender no longer has are defaults here. The members it kept with
 * the same shape are declared abstract, so the 1.20.4 defaults below route through them as they did:
 * at run time the class's own method or 1.20.5's default answers (the one non-abstract maximally-specific
 * method the JVM selects).
 */
public interface CbLegacyPacketSender {

    net.minecraft.class_2596<?> createPacket(net.minecraft.class_2960 channelName, net.minecraft.class_2540 buf);

    net.minecraft.class_2596<?> createPacket(FabricPacket packet);

    void sendPacket(net.minecraft.class_2596<?> packet, GenericFutureListener<? extends Future<? super Void>> callback);

    /** kept by 1.20.5 (abstract in both) */
    void sendPacket(net.minecraft.class_2596<?> packet, net.minecraft.class_7648 callback);

    /** kept by 1.20.5 (a default in both) */
    void sendPacket(net.minecraft.class_2596<?> packet);

    /** 1.20.5's abstract createPacket(payload): synthesized in the class by the hook, see CbPacketSenders */
    net.minecraft.class_2596<?> createPacket(net.minecraft.class_8710 payload);

    default void sendPacket(FabricPacket packet) {
        sendPacket(createPacket(packet));
    }

    default void sendPacket(FabricPacket packet, GenericFutureListener<? extends Future<? super Void>> callback) {
        sendPacket(createPacket(packet), callback);
    }

    /**
     * 1.20.4: the payload written into a buffer, sent on its channel. A payload with no 1.20.4 write (a
     * 1.20.5 type) is sent as itself: its codec writes what it is.
     */
    default void sendPacket(net.minecraft.class_8710 payload, GenericFutureListener<? extends Future<? super Void>> callback) {
        CbChannelPayload channel = CbChannelPayload.channelForm(payload);
        if (channel != null) {
            sendPacket(channel.channel(), channel.buffer(), callback);
        } else {
            sendPacket(createPacket(payload), CbFutureListenerCallbacks.create(callback));
        }
    }

    default void sendPacket(FabricPacket packet, net.minecraft.class_7648 callback) {
        sendPacket(createPacket(packet), callback);
    }

    default void sendPacket(net.minecraft.class_2960 channel, net.minecraft.class_2540 buf) {
        java.util.Objects.requireNonNull(channel, "Channel cannot be null");
        java.util.Objects.requireNonNull(buf, "Payload cannot be null");
        sendPacket(createPacket(channel, buf));
    }

    default void sendPacket(net.minecraft.class_2960 channel, net.minecraft.class_2540 buf,
                            GenericFutureListener<? extends Future<? super Void>> callback) {
        sendPacket(channel, buf, CbFutureListenerCallbacks.create(callback));
    }

    default void sendPacket(net.minecraft.class_2960 channel, net.minecraft.class_2540 buf, net.minecraft.class_7648 callback) {
        java.util.Objects.requireNonNull(channel, "Channel cannot be null");
        java.util.Objects.requireNonNull(buf, "Payload cannot be null");
        sendPacket(createPacket(channel, buf), callback);
    }
}
