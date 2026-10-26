package top.dext.centurybridge.step.v1_21_6.rt;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;
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
 * <p>
 * The other way, a 1.21.5 override of send(packet, listener) is handed 1.21.6's listener as a PacketSendListener
 * ({@link #fromNetty}) and may call its onSuccess() / onFailure() itself, as 1.21.5 allowed. 1.21.6's listeners from
 * its own factories (PacketSendListener.thenRun / exceptionallySend, every listener vanilla 1.21.6 passes to send) are
 * recorded with what they were made from (CbPacketListenerFactoriesMixin, the factories' return values unchanged), so
 * such a call has exactly 1.21.5's semantics: thenRun(r) runs r on both callbacks and answers no packet,
 * exceptionallySend(s) answers s.get(). A listener 1.21.5 handed in comes back as itself.
 */
public final class CbPackets {

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    /**
     * 1.21.6 factory listener -> the 1.21.5 listener the same factory made in 1.21.5 (weak keys, compared by identity:
     * W's listeners are lambdas, which keep Object's equals / hashCode; the value never refers back to its key)
     */
    private static final Map<ChannelFutureListener, PacketSendListener> FACTORY_MADE =
        Collections.synchronizedMap(new WeakHashMap<>());

    private CbPackets() {
    }

    /** PacketSendListener.thenRun(r) returned {@code made} (CbPacketListenerFactoriesMixin) */
    public static void recordThenRun(ChannelFutureListener made, Runnable runnable) {
        if (made != null) {
            FACTORY_MADE.put(made, PacketSendListener.method_45084(runnable));
        }
    }

    /** PacketSendListener.exceptionallySend(s) returned {@code made} (CbPacketListenerFactoriesMixin) */
    public static void recordExceptionallySend(ChannelFutureListener made, Supplier<class_2596<?>> fallback) {
        if (made != null) {
            FACTORY_MADE.put(made, PacketSendListener.method_45085(fallback));
        }
    }

    /** a 1.21.5 listener handed to 1.21.6's send: 1.21.5's doSendPacket completion ({@link #complete}) */
    static final class FromV implements ChannelFutureListener {
        final PacketSendListener listener;

        FromV(PacketSendListener listener) {
            this.listener = listener;
        }

        @Override
        public void operationComplete(ChannelFuture future) {
            complete(listener, future);
        }
    }

    /**
     * A 1.21.6 listener handed to a 1.21.5 override of send(packet, listener) (override hook, {@link #fromNetty}): its
     * super call gives the same listener back to 1.21.6's send. A direct onSuccess() / onFailure() call answers as
     * 1.21.5's listener from the same factory; a listener no factory made (a native 1.21.6 mod's, Fabric API's) is run
     * on a succeeded future of the connection's channel by onSuccess(), and has no fallback packet for onFailure() --
     * the answer of 1.21.5's default, onFailure() being a query there (1.21.5's doSendPacket reported the failure to the
     * pipeline itself).
     */
    public static final class NettyListener implements PacketSendListener {
        final ChannelFutureListener listener;
        final class_2535 connection;

        NettyListener(ChannelFutureListener listener, class_2535 connection) {
            this.listener = listener;
            this.connection = connection;
        }

        @Override
        public void method_45083() {
            PacketSendListener made = FACTORY_MADE.get(listener);
            if (made != null) {
                made.method_45083();
                return;
            }
            Channel channel = connection == null ? null : connection.field_11651;
            if (channel == null) {
                // a connection without a channel has written nothing a netty listener could be told about
                return;
            }
            try {
                listener.operationComplete(channel.newSucceededFuture());
            } catch (Throwable t) {
                // as netty's DefaultPromise.notifyListener0
                LOGGER.warn("An exception was thrown by {}.operationComplete()", listener.getClass().getName(), t);
            }
        }

        @Override
        public class_2596<?> method_45086() {
            PacketSendListener made = FACTORY_MADE.get(listener);
            return made != null ? made.method_45086() : null;
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
        if (l instanceof ChannelFutureListener c) {
            // already both (CenturyBridge's 1.20.5 CbFutureListenerCallbacks: a 1.20.4 GenericFutureListener carried
            // as the callbacks, doing on the write's future what its 1.20.5 mixin added there): handed over as it is
            return c;
        }
        return new FromV(l);
    }

    /** 1.21.6's listener as 1.21.5's, for a 1.21.5 override of the connection's send */
    public static PacketSendListener fromNetty(ChannelFutureListener f, class_2535 connection) {
        if (f == null) {
            return null;
        }
        if (f instanceof FromV v) {
            return v.listener;
        }
        if (f instanceof PacketSendListener p) {
            return p; // already both (toNetty)
        }
        return new NettyListener(f, connection);
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

    /** a 1.21.5 Connection subclass's own send(packet, listener) (override hook adapter) */
    public interface LegacySend {
        void method_10752(class_2596<?> packet, PacketSendListener listener);
    }

    /** a 1.21.5 Connection subclass's own send(packet, listener, flush) (override hook adapter) */
    public interface LegacySendFlush {
        void method_52906(class_2596<?> packet, PacketSendListener listener, boolean flush);
    }

    /** a 1.21.5 ServerCommonPacketListenerImpl subclass's own send(packet, listener) (override hook adapter) */
    public interface LegacyCommonSend {
        void method_52391(class_2596<?> packet, PacketSendListener listener);
    }

    /** 1.21.6's send(packet, listener) on a 1.21.5 Connection subclass: its override, with the listener as 1.21.5's */
    public static void bridgeSend(LegacySend self, class_2596<?> packet, ChannelFutureListener listener) {
        self.method_10752(packet, fromNetty(listener, (class_2535) (Object) self));
    }

    /** 1.21.6's send(packet, listener, flush) on a 1.21.5 Connection subclass */
    public static void bridgeSendFlush(LegacySendFlush self, class_2596<?> packet, ChannelFutureListener listener, boolean flush) {
        self.method_52906(packet, fromNetty(listener, (class_2535) (Object) self), flush);
    }

    /** 1.21.6's send(packet, listener) on a 1.21.5 ServerCommonPacketListenerImpl subclass (its connection's channel) */
    public static void bridgeHandlerSend(LegacyCommonSend self, class_2596<?> packet, ChannelFutureListener listener) {
        self.method_52391(packet, fromNetty(listener, ((class_8609) (Object) self).field_45013));
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
