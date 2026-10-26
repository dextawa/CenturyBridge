package top.dext.centurybridge.step.v1_21_6.rt;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.fabricmc.fabric.api.networking.v1.LoginPacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import top.dext.centurybridge.step.v1_21_6.facade.PacketSendListener;

/**
 * 1.21.5's packet-send callbacks (PacketSendListener, the facade) on 1.21.6's sends, which take a netty
 * ChannelFutureListener: Connection.send / ServerCommonPacketListenerImpl.send and Fabric API's PacketSender /
 * LoginPacketSender.sendPacket.
 * <p>
 * {@link #listener} and {@link #callbacks} undo each other. A 1.21.5 listener becomes the listener 1.21.5's
 * Connection put on the write's future for it (sendInternal: its lambda over handleListenerFailure, then
 * FIRE_EXCEPTION_ON_FAILURE): success calls onSuccess; failure sends onFailure's packet, if any, on the channel with
 * FIRE_EXCEPTION_ON_FAILURE, then fires the failure into the pipeline. A netty listener a 1.21.5 override or mixin
 * handler is given (vanilla's) is carried as a 1.21.5 listener and handed on unchanged when it is sent; its own
 * callbacks have no 1.21.5 form (1.21.5 had no netty listeners in its sends), so onSuccess does nothing and
 * onFailure answers no packet, as the interface's defaults. A value that already is the other kind (CenturyBridge's
 * 1.20.5 CbFutureListenerCallbacks is both) passes as it is.
 */
public final class CbPacketListeners {

    private CbPacketListeners() {
    }

    /** a 1.21.5 listener as the netty listener 1.21.6's sends take (null for none, as 1.21.5's) */
    public static ChannelFutureListener listener(PacketSendListener callbacks) {
        if (callbacks == null) {
            return null;
        }
        if (callbacks instanceof Carried c) {
            return c.listener;
        }
        if (callbacks instanceof ChannelFutureListener l) {
            return l;
        }
        return new Adapted(callbacks);
    }

    /** a 1.21.6 netty listener as the 1.21.5 listener a 1.21.5 override or handler takes */
    public static PacketSendListener callbacks(ChannelFutureListener listener) {
        if (listener == null) {
            return null;
        }
        if (listener instanceof Adapted a) {
            return a.callbacks;
        }
        if (listener instanceof PacketSendListener p) {
            return p;
        }
        return new Carried(listener);
    }

    /** Connection.send(packet, callbacks) */
    public static void send(net.minecraft.class_2535 connection, net.minecraft.class_2596<?> packet, PacketSendListener callbacks) {
        connection.method_10752(packet, listener(callbacks));
    }

    /** Connection.send(packet, callbacks, flush) */
    public static void send(net.minecraft.class_2535 connection, net.minecraft.class_2596<?> packet, PacketSendListener callbacks,
                            boolean flush) {
        connection.method_52906(packet, listener(callbacks), flush);
    }

    /** ServerCommonPacketListenerImpl.send(packet, callbacks) */
    public static void send(net.minecraft.class_8609 handler, net.minecraft.class_2596<?> packet, PacketSendListener callbacks) {
        handler.method_52391(packet, listener(callbacks));
    }

    /** PacketSender.sendPacket(packet, callbacks) */
    public static void sendPacket(PacketSender sender, net.minecraft.class_2596<?> packet, PacketSendListener callbacks) {
        sender.sendPacket(packet, listener(callbacks));
    }

    /** PacketSender.sendPacket(payload, callbacks) */
    public static void sendPacket(PacketSender sender, net.minecraft.class_8710 payload, PacketSendListener callbacks) {
        sender.sendPacket(payload, listener(callbacks));
    }

    /** LoginPacketSender.sendPacket(channel, buf, callbacks) */
    public static void sendPacket(LoginPacketSender sender, net.minecraft.class_2960 channel, net.minecraft.class_2540 buf,
                                  PacketSendListener callbacks) {
        sender.sendPacket(channel, buf, listener(callbacks));
    }

    /** 1.21.5 PacketSender's default sendPacket(payload, callbacks), reached by a super call */
    public static void superSendPacket(PacketSender sender, net.minecraft.class_8710 payload, PacketSendListener callbacks) {
        sender.sendPacket(sender.createPacket(payload), listener(callbacks));
    }

    /** 1.21.5 LoginPacketSender's default sendPacket(channel, buf, callbacks), reached by a super call */
    public static void superSendPacket(LoginPacketSender sender, net.minecraft.class_2960 channel, net.minecraft.class_2540 buf,
                                       PacketSendListener callbacks) {
        java.util.Objects.requireNonNull(channel, "Channel cannot be null");
        java.util.Objects.requireNonNull(buf, "Payload cannot be null");
        sender.sendPacket(sender.createPacket(channel, buf), listener(callbacks));
    }

    /** the vanilla Connection above a mod class, reached by its super.send(packet, callbacks) */
    public interface SuperConnectionSend {
        void cb$super$method_10752(net.minecraft.class_2596<?> packet, ChannelFutureListener listener);
    }

    /** the vanilla Connection above a mod class, reached by its super.send(packet, callbacks, flush) */
    public interface SuperConnectionSendFlush {
        void cb$super$method_52906(net.minecraft.class_2596<?> packet, ChannelFutureListener listener, boolean flush);
    }

    /** the vanilla ServerCommonPacketListenerImpl above a mod class, reached by its super.send(packet, callbacks) */
    public interface SuperCommonSend {
        void cb$super$method_52391(net.minecraft.class_2596<?> packet, ChannelFutureListener listener);
    }

    public static void superSend(SuperConnectionSend self, net.minecraft.class_2596<?> packet, PacketSendListener callbacks) {
        self.cb$super$method_10752(packet, listener(callbacks));
    }

    public static void superSend(SuperConnectionSendFlush self, net.minecraft.class_2596<?> packet, PacketSendListener callbacks,
                                 boolean flush) {
        self.cb$super$method_52906(packet, listener(callbacks), flush);
    }

    public static void superSend(SuperCommonSend self, net.minecraft.class_2596<?> packet, PacketSendListener callbacks) {
        self.cb$super$method_52391(packet, listener(callbacks));
    }

    /** a 1.21.5 listener on a 1.21.6 send: what 1.21.5's Connection did with it on the write's future */
    static final class Adapted implements ChannelFutureListener, PacketSendListener {
        final PacketSendListener callbacks;

        Adapted(PacketSendListener callbacks) {
            this.callbacks = callbacks;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            // 1.21.5 Connection.handleListenerFailure (method_45082)
            if (future.isSuccess()) {
                callbacks.method_45083();
            } else {
                net.minecraft.class_2596<?> fallback = callbacks.method_45086();
                if (fallback != null) {
                    future.channel().writeAndFlush(fallback).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            }
            // then sendInternal's FIRE_EXCEPTION_ON_FAILURE on the same future
            ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE.operationComplete(future);
        }

        @Override
        public void method_45083() {
            callbacks.method_45083();
        }

        @Override
        public net.minecraft.class_2596<?> method_45086() {
            return callbacks.method_45086();
        }
    }

    /** a 1.21.6 netty listener handed to 1.21.5 code: sent on unchanged (see the class javadoc for its callbacks) */
    static final class Carried implements PacketSendListener, ChannelFutureListener {
        final ChannelFutureListener listener;

        Carried(ChannelFutureListener listener) {
            this.listener = listener;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            listener.operationComplete(future);
        }
    }
}
