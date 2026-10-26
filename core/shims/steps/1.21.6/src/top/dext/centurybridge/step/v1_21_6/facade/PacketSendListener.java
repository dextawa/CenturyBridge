package top.dext.centurybridge.step.v1_21_6.facade;

/**
 * 1.21.5's PacketSendListener (class_7648), an interface of packet-send callbacks, which 1.21.6 turned into a
 * utility class whose statics build netty ChannelFutureListeners (Connection.send takes one of those since). Members
 * as 1.21.5's (javap): onSuccess (method_45083) does nothing, onFailure (method_45086) has no fallback packet;
 * thenRun (method_45084) runs its action on success and on failure (then no fallback), exceptionallySend
 * (method_45085) answers its supplier's packet on failure. A 1.21.5 listener reaches 1.21.6's sends through
 * {@link top.dext.centurybridge.step.v1_21_6.rt.CbPacketListeners#listener}.
 */
public interface PacketSendListener {

    /** thenRun: class_7648$1 */
    static PacketSendListener method_45084(Runnable action) {
        return new PacketSendListener() {
            @Override
            public void method_45083() {
                action.run();
            }

            @Override
            public net.minecraft.class_2596<?> method_45086() {
                action.run();
                return null;
            }
        };
    }

    /** exceptionallySend: class_7648$2 */
    static PacketSendListener method_45085(java.util.function.Supplier<net.minecraft.class_2596<?>> fallback) {
        return new PacketSendListener() {
            @Override
            public net.minecraft.class_2596<?> method_45086() {
                return fallback.get();
            }
        };
    }

    /** onSuccess */
    default void method_45083() {
    }

    /** onFailure: the packet to send instead, or null */
    default net.minecraft.class_2596<?> method_45086() {
        return null;
    }
}
