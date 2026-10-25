package top.dext.centurybridge.step.v1_21_6.rt;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.class_2535;
import net.minecraft.class_2596;
import net.minecraft.class_8609;
import top.dext.centurybridge.step.v1_21_6.facade.PacketSendListener;

/**
 * 1.21.5's packet sending with a PacketSendListener on 1.21.6's Connection / ServerCommonPacketListenerImpl, which take
 * a netty ChannelFutureListener (javap of both releases).
 * <p>
 * 1.21.5's Connection.doSendPacket added, after the write, a listener calling onSuccess() when the write succeeded and
 * otherwise writing and flushing the packet onFailure() answered (if any, with FIRE_EXCEPTION_ON_FAILURE), then
 * FIRE_EXCEPTION_ON_FAILURE on the write itself. 1.21.6's doSendPacket attaches only the listener it is given (W's own
 * factories fire the exception themselves), so {@link #toNetty} does both, in 1.21.5's order.
 */
public final class CbPackets {

    private CbPackets() {
    }

    /**
     * A 1.21.6 listener handed to a 1.21.5 override of send(packet, listener) (override hook, {@link #fromNetty}): its
     * super call gives the same listener back to 1.21.6's send. 1.21.6's listeners only take a ChannelFuture, so the
     * 1.21.5 callbacks cannot run one outside a write.
     */
    public static final class NettyListener implements PacketSendListener {
        final ChannelFutureListener listener;

        NettyListener(ChannelFutureListener listener) {
            this.listener = listener;
        }

        @Override
        public void method_45083() {
            throw new UnsupportedOperationException("1.21.6 send listener " + listener + " only runs on a ChannelFuture");
        }

        @Override
        public class_2596<?> method_45086() {
            throw new UnsupportedOperationException("1.21.6 send listener " + listener + " only runs on a ChannelFuture");
        }
    }

    /** 1.21.5's listener as 1.21.6's (null stays null: no listener) */
    public static ChannelFutureListener toNetty(PacketSendListener l) {
        if (l == null) {
            return null;
        }
        if (l instanceof NettyListener n) {
            return n.listener;
        }
        return future -> complete(l, future);
    }

    /** 1.21.6's listener as 1.21.5's, for a 1.21.5 override's argument */
    public static PacketSendListener fromNetty(ChannelFutureListener f) {
        return f == null ? null : new NettyListener(f);
    }

    private static void complete(PacketSendListener l, ChannelFuture future) {
        if (future.isSuccess()) {
            l.method_45083();
        } else {
            class_2596<?> fallback = l.method_45086();
            if (fallback != null) {
                future.channel().writeAndFlush(fallback).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
            future.channel().pipeline().fireExceptionCaught(future.cause());
        }
    }

    /** Connection.send(packet, listener) */
    public static void send(class_2535 connection, class_2596<?> packet, PacketSendListener listener) {
        connection.method_10752(packet, toNetty(listener));
    }

    /** Connection.send(packet, listener, flush) */
    public static void send(class_2535 connection, class_2596<?> packet, PacketSendListener listener, boolean flush) {
        connection.method_52906(packet, toNetty(listener), flush);
    }

    /** ServerCommonPacketListenerImpl.send(packet, listener) */
    public static void send(class_8609 handler, class_2596<?> packet, PacketSendListener listener) {
        handler.method_52391(packet, toNetty(listener));
    }

    /** a 1.21.5 Connection subclass's super.send(packet, listener): super's 1.21.6 send */
    public interface SuperConnection {
        void cb$super$method_10752(class_2596<?> packet, ChannelFutureListener listener);

        void cb$super$method_52906(class_2596<?> packet, ChannelFutureListener listener, boolean flush);
    }

    public static void superSend(SuperConnection self, class_2596<?> packet, PacketSendListener listener) {
        self.cb$super$method_10752(packet, toNetty(listener));
    }

    public static void superSendFlush(SuperConnection self, class_2596<?> packet, PacketSendListener listener, boolean flush) {
        self.cb$super$method_52906(packet, toNetty(listener), flush);
    }

    /** a 1.21.5 ServerCommonPacketListenerImpl subclass's super.send(packet, listener) */
    public interface SuperCommonListener {
        void cb$super$method_52391(class_2596<?> packet, ChannelFutureListener listener);
    }

    public static void superHandlerSend(SuperCommonListener self, class_2596<?> packet, PacketSendListener listener) {
        self.cb$super$method_52391(packet, toNetty(listener));
    }
}
