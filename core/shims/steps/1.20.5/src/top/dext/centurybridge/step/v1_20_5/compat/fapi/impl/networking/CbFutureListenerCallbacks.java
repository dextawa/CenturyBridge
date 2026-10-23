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
 */
public final class CbFutureListenerCallbacks implements net.minecraft.class_7648 {

    private final GenericFutureListener<? extends Future<? super Void>> delegate;

    private CbFutureListenerCallbacks(GenericFutureListener<? extends Future<? super Void>> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    /** null for no listener, as 1.20.4's GenericFutureListenerHolder.create */
    public static CbFutureListenerCallbacks create(GenericFutureListener<? extends Future<? super Void>> delegate) {
        return delegate == null ? null : new CbFutureListenerCallbacks(delegate);
    }

    public GenericFutureListener<? extends Future<? super Void>> delegate() {
        return delegate;
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
