package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.networking;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Objects;

/**
 * A netty listener for the write of one packet, carried as the packet's callbacks: 1.20.4's Fabric API
 * PacketSender took a GenericFutureListener for a send (its GenericFutureListenerHolder), and 1.20.5's
 * PacketCallbacks cannot express one. The CbFapiFutureListenerMixin on ClientConnection.sendInternal adds
 * the listener to the write's channel future, as 1.20.4's ClientConnectionMixin did, instead of the
 * callbacks vanilla would wrap; the callbacks themselves are never called (1.20.4's holder said the same).
 * <p>
 * It is also the netty listener that mixin puts on the future (the listener, then FIRE_EXCEPTION_ON_FAILURE):
 * 1.21.6's Connection takes a ChannelFutureListener for a send and adds it to the write's future itself, so from
 * that step on the holder is handed over as it is and the mixin retires (1.21.6 step wiring). Before 1.21.6 vanilla
 * never sees it as one: the mixin cancels before its callbacks wrapper would be built.
 */
public final class CbFutureListenerCallbacks implements net.minecraft.class_7648, io.netty.channel.ChannelFutureListener {

    private final GenericFutureListener<? extends Future<? super Void>> delegate;

    private CbFutureListenerCallbacks(GenericFutureListener<? extends Future<? super Void>> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    /** null for no listener, as 1.20.4's GenericFutureListenerHolder.create; typed as the packet's callbacks */
    public static net.minecraft.class_7648 create(GenericFutureListener<? extends Future<? super Void>> delegate) {
        return delegate == null ? null : new CbFutureListenerCallbacks(delegate);
    }

    public GenericFutureListener<? extends Future<? super Void>> delegate() {
        return delegate;
    }

    /** what CbFapiFutureListenerMixin adds to the write's future: the listener, then FIRE_EXCEPTION_ON_FAILURE */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void operationComplete(io.netty.channel.ChannelFuture future) throws Exception {
        ((GenericFutureListener) delegate).operationComplete(future);
        io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE.operationComplete(future);
    }

    @Override
    public void method_45083() {
        throw new AssertionError("Should not be called");
    }

    @Override
    public net.minecraft.class_2596<?> method_45086() {
        throw new AssertionError("Should not be called");
    }
}
