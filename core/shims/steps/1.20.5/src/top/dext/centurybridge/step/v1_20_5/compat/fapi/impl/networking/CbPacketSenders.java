package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.networking;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Objects;
import net.fabricmc.fabric.api.networking.v1.LoginPacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.networking.v1.FabricPacket;

/**
 * Fabric API 1.20.4's PacketSender members that 1.20.5 dropped, on 1.20.5's PacketSender
 * (instanceRedirects; the {@code super*} statics are the superRedirects of a mod implementation's own
 * {@code PacketSender.super.m(..)}, which must run the 1.20.4 default body rather than dispatch back into
 * the override that is calling it).
 *
 * <p>A sender is one of:
 * <ul>
 * <li>a mod's own 1.20.4 implementation ({@link CbLegacyPacketSender}): its own methods answer, the 1.20.4
 *     defaults it does not override included;</li>
 * <li>Fabric API's login sender (LoginPacketSender, which still builds a login query for a channel):
 *     1.20.4's login sender built the same query for a channel and its bytes, and for a FabricPacket
 *     the query of its channel with the bytes the packet writes;</li>
 * <li>any other 1.20.5 sender (the play and configuration addons): a channel send is a
 *     {@link CbChannelPayload}, which its createPacket(payload) turns into the phase's custom payload
 *     packet, written on the wire as 1.20.4 wrote it.</li>
 * </ul>
 * A GenericFutureListener rides as {@link CbFutureListenerCallbacks} (1.20.4's GenericFutureListenerHolder).
 */
public final class CbPacketSenders {

    private CbPacketSenders() {
    }

    // abstract in 1.20.4

    public static net.minecraft.class_2596<?> createPacket(PacketSender self, net.minecraft.class_2960 channel, net.minecraft.class_2540 buf) {
        if (self instanceof CbLegacyPacketSender legacy) {
            return legacy.createPacket(channel, buf);
        }
        if (self instanceof LoginPacketSender login) {
            return login.createPacket(channel, buf);
        }
        return self.createPacket(CbChannelPayload.raw(channel, buf));
    }

    public static net.minecraft.class_2596<?> createPacket(PacketSender self, FabricPacket packet) {
        if (self instanceof CbLegacyPacketSender legacy) {
            return legacy.createPacket(packet);
        }
        if (self instanceof LoginPacketSender login) {
            Objects.requireNonNull(packet, "Packet cannot be null");
            net.minecraft.class_2540 buf = PacketByteBufs.create();
            packet.write(buf);
            return login.createPacket(packet.getType().getId(), buf);
        }
        return self.createPacket(CbChannelPayload.typed(packet));
    }

    public static void sendPacket(PacketSender self, net.minecraft.class_2596<?> packet,
                                  GenericFutureListener<? extends Future<? super Void>> callback) {
        if (self instanceof CbLegacyPacketSender legacy) {
            legacy.sendPacket(packet, callback);
            return;
        }
        self.sendPacket(packet, CbFutureListenerCallbacks.create(callback));
    }

    // defaults in 1.20.4: dispatching (a mod implementation's override answers), and the default bodies

    public static void sendPacket(PacketSender self, FabricPacket packet) {
        if (self instanceof CbLegacyPacketSender legacy) {
            legacy.sendPacket(packet);
            return;
        }
        superSendPacket(self, packet);
    }

    public static void superSendPacket(PacketSender self, FabricPacket packet) {
        self.sendPacket(createPacket(self, packet));
    }

    public static void sendPacket(PacketSender self, FabricPacket packet, GenericFutureListener<? extends Future<? super Void>> callback) {
        if (self instanceof CbLegacyPacketSender legacy) {
            legacy.sendPacket(packet, callback);
            return;
        }
        superSendPacket(self, packet, callback);
    }

    public static void superSendPacket(PacketSender self, FabricPacket packet, GenericFutureListener<? extends Future<? super Void>> callback) {
        sendPacket(self, createPacket(self, packet), callback);
    }

    public static void sendPacket(PacketSender self, net.minecraft.class_8710 payload, GenericFutureListener<? extends Future<? super Void>> callback) {
        if (self instanceof CbLegacyPacketSender legacy) {
            legacy.sendPacket(payload, callback);
            return;
        }
        superSendPacket(self, payload, callback);
    }

    /**
     * 1.20.4: the payload written into a buffer and sent on its channel. A payload with no 1.20.4 write (a
     * 1.20.5 type: a vanilla payload's 1.20.4 write was its codec's) is sent as itself.
     */
    public static void superSendPacket(PacketSender self, net.minecraft.class_8710 payload, GenericFutureListener<? extends Future<? super Void>> callback) {
        CbChannelPayload channel = CbChannelPayload.channelForm(payload);
        if (channel != null) {
            sendPacket(self, channel.channel(), channel.buffer(), callback);
        } else {
            self.sendPacket(self.createPacket(payload), CbFutureListenerCallbacks.create(callback));
        }
    }

    public static void sendPacket(PacketSender self, FabricPacket packet, net.minecraft.class_7648 callback) {
        if (self instanceof CbLegacyPacketSender legacy) {
            legacy.sendPacket(packet, callback);
            return;
        }
        superSendPacket(self, packet, callback);
    }

    public static void superSendPacket(PacketSender self, FabricPacket packet, net.minecraft.class_7648 callback) {
        self.sendPacket(createPacket(self, packet), callback);
    }

    public static void sendPacket(PacketSender self, net.minecraft.class_2960 channel, net.minecraft.class_2540 buf) {
        if (self instanceof CbLegacyPacketSender legacy) {
            legacy.sendPacket(channel, buf);
            return;
        }
        superSendPacket(self, channel, buf);
    }

    public static void superSendPacket(PacketSender self, net.minecraft.class_2960 channel, net.minecraft.class_2540 buf) {
        Objects.requireNonNull(channel, "Channel cannot be null");
        Objects.requireNonNull(buf, "Payload cannot be null");
        self.sendPacket(createPacket(self, channel, buf));
    }

    public static void sendPacket(PacketSender self, net.minecraft.class_2960 channel, net.minecraft.class_2540 buf,
                                  GenericFutureListener<? extends Future<? super Void>> callback) {
        if (self instanceof CbLegacyPacketSender legacy) {
            legacy.sendPacket(channel, buf, callback);
            return;
        }
        superSendPacket(self, channel, buf, callback);
    }

    public static void superSendPacket(PacketSender self, net.minecraft.class_2960 channel, net.minecraft.class_2540 buf,
                                       GenericFutureListener<? extends Future<? super Void>> callback) {
        sendPacket(self, channel, buf, CbFutureListenerCallbacks.create(callback));
    }

    public static void sendPacket(PacketSender self, net.minecraft.class_2960 channel, net.minecraft.class_2540 buf,
                                  net.minecraft.class_7648 callback) {
        if (self instanceof CbLegacyPacketSender legacy) {
            legacy.sendPacket(channel, buf, callback);
            return;
        }
        superSendPacket(self, channel, buf, callback);
    }

    public static void superSendPacket(PacketSender self, net.minecraft.class_2960 channel, net.minecraft.class_2540 buf,
                                       net.minecraft.class_7648 callback) {
        Objects.requireNonNull(channel, "Channel cannot be null");
        Objects.requireNonNull(buf, "Payload cannot be null");
        self.sendPacket(createPacket(self, channel, buf), callback);
    }

    // a mod's 1.20.4 implementation on 1.20.5's abstract members

    /**
     * 1.20.5's createPacket(payload), synthesized in a mod's 1.20.4 implementation (overrideHooks bridge): a
     * channel payload is built the 1.20.4 way, by the class's own createPacket of the channel (and of the
     * packet, when sent as one). A 1.20.5 payload type has no channel form a 1.20.4 sender could build.
     */
    public static net.minecraft.class_2596<?> createPacket(CbLegacyPacketSender self, net.minecraft.class_8710 payload) {
        Objects.requireNonNull(payload, "Payload cannot be null");
        CbChannelPayload channel = CbChannelPayload.channelForm(payload);
        if (channel == null) {
            throw new IllegalArgumentException("a 1.20.4 PacketSender implementation (" + self.getClass().getName()
                + ") builds packets for channels; payload " + payload.method_56479().comp_2242() + " is a 1.20.5 type with no channel form");
        }
        return channel.typedPacket() != null ? self.createPacket(channel.typedPacket()) : self.createPacket(channel.channel(), channel.buffer());
    }

    /**
     * 1.20.5's disconnect(reason), filled in a mod's 1.20.4 implementation (abstractFills): 1.20.4's
     * PacketSender had no way to disconnect, so such a sender has none to give.
     */
    public static void disconnect(PacketSender self, net.minecraft.class_2561 reason) {
        throw new UnsupportedOperationException("a 1.20.4 PacketSender implementation (" + self.getClass().getName()
            + ") cannot disconnect: 1.20.4's PacketSender had no disconnect");
    }
}
