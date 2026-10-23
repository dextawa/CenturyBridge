package top.dext.centurybridge.step.v1_20_5.compat.fapi.networking.v1;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Objects;

/**
 * Fabric API 0.97.3's (1.20.4) FutureListeners, removed in 1.20.5: netty future listener helpers. Pure
 * netty, so the 1.20.4 bodies (Fabric API, Apache-2.0) stand as they were. A listener reaches the write's
 * channel future through {@code PacketSender.sendPacket(.., GenericFutureListener)}, which the layer
 * keeps (CbFutureListenerCallbacks and its ClientConnection mixin).
 */
public final class FutureListeners {

    private FutureListeners() {
    }

    /** a listener releasing the buffer once sent, unless the channel is local (the buffer travels on as is) */
    public static ChannelFutureListener free(net.minecraft.class_2540 buf) {
        Objects.requireNonNull(buf, "PacketByteBuf cannot be null");
        return future -> {
            if (!isLocalChannel(future.channel())) {
                buf.release();
            }
        };
    }

    public static boolean isLocalChannel(Channel channel) {
        return channel instanceof LocalServerChannel || channel instanceof LocalChannel;
    }

    @SuppressWarnings("unchecked")
    public static <A extends Future<? super Void>, B extends Future<? super Void>> GenericFutureListener<? extends Future<? super Void>>
            union(GenericFutureListener<A> first, GenericFutureListener<B> second) {
        if (first == null && second == null) {
            return future -> { };
        }
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        return future -> {
            first.operationComplete((A) future);
            second.operationComplete((B) future);
        };
    }
}
