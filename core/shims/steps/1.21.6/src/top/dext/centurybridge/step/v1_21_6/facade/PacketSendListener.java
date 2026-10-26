package top.dext.centurybridge.step.v1_21_6.facade;

/**
 * 1.21.5's PacketSendListener (class_7648), an interface 1.21.6 turned into a holder of two static
 * ChannelFutureListener factories (Connection.send takes a ChannelFutureListener there). Members as 1.21.5's (javap):
 * onSuccess() does nothing and onFailure() answers no fallback packet by default; thenRun(r) runs r on success and on
 * failure (answering no packet), exceptionallySend(s) answers s.get() on failure. 1.21.5's Connection called
 * onSuccess when the write succeeded, otherwise sent the packet onFailure answered (if any); CbPackets.toNetty does
 * that on 1.21.6's listener.
 */
public interface PacketSendListener {

    /** thenRun(Runnable) */
    static PacketSendListener method_45084(Runnable runnable) {
        return new PacketSendListener() {
            @Override
            public void method_45083() {
                runnable.run();
            }

            @Override
            public net.minecraft.class_2596<?> method_45086() {
                runnable.run();
                return null;
            }
        };
    }

    /** exceptionallySend(Supplier) */
    static PacketSendListener method_45085(java.util.function.Supplier<net.minecraft.class_2596<?>> fallback) {
        return new PacketSendListener() {
            @Override
            public net.minecraft.class_2596<?> method_45086() {
                return fallback.get();
            }
        };
    }

    /** onSuccess() */
    default void method_45083() {
    }

    /** onFailure(): the packet to send instead, or null */
    default net.minecraft.class_2596<?> method_45086() {
        return null;
    }
}
