package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's packet listener surface on 1.20.5's (area s4fin network).
 * <ul>
 * <li>shouldPropagateHandlingExceptions(): 1.20.4's PacketUtils asked it whether a handler's exception
 *     propagates (true by default, false for server listeners, which logged "Failed to handle packet {},
 *     suppressing error"); 1.20.5 asks the listener's onPacketError(packet, exception), whose defaults do
 *     the same. A 1.20.4 listener's own answer becomes its onPacketError (adapter hook); calls of the old
 *     method answer the listener's own, else 1.20.4's default for its kind.</li>
 * <li>Handlers for packets 1.20.5 added. A 1.20.4 listener implementation declares none of them; the packets
 *     did not exist in 1.20.4, whose connection failed on an id it did not know. Each such handler fails the
 *     same way, with the packet named -- except handleSignedChatCommand, which is 1.20.4's signed
 *     ServerboundChatCommandPacket: it is handed to the listener's handleChatCommand as that packet, its
 *     signatures, salt, timestamp and acknowledgements carried along (rt/CbNetGame).</li>
 * </ul>
 */
public final class CbNetListeners {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("CenturyBridge");

    /** a 1.20.4 listener declaring shouldPropagateHandlingExceptions() */
    public interface Propagates {
        boolean method_40065();
    }

    /** listener.shouldPropagateHandlingExceptions(): its own answer, else 1.20.4's default for its kind */
    public static boolean method_40065(net.minecraft.class_2547 listener) {
        if (listener instanceof Propagates p) {
            return p.method_40065();
        }
        return !(listener instanceof net.minecraft.class_6857);
    }

    /** the same, called through ServerPacketListener */
    public static boolean method_40065(net.minecraft.class_6857 listener) {
        return method_40065((net.minecraft.class_2547) listener);
    }

    /** super.shouldPropagateHandlingExceptions() reaching PacketListener's default */
    public static boolean superPacketListener(net.minecraft.class_2547 listener) {
        return true;
    }

    /** super.shouldPropagateHandlingExceptions() reaching ServerPacketListener's default */
    public static boolean superServerPacketListener(net.minecraft.class_6857 listener) {
        return false;
    }

    /**
     * onPacketError(packet, exception) of a 1.20.4 listener with its own answer: 1.20.4's PacketUtils --
     * propagate (as a ReportedException, which is what 1.20.5's PacketListener default throws) or log and
     * go on.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void onPacketError(Propagates listener, net.minecraft.class_2596 packet, Exception e) {
        if (listener.method_40065()) {
            throw net.minecraft.class_2600.method_59854(e, packet, (net.minecraft.class_2547) listener);
        }
        LOGGER.error("Failed to handle packet {}, suppressing error", packet, e);
    }

    // ---------------------------------------------------------------- handlers for packets 1.20.5 added

    private static IllegalStateException unknown(Object listener, Object packet) {
        return new IllegalStateException("1.20.4 packet listener " + listener.getClass().getName() + " cannot handle "
            + packet.getClass().getSimpleName() + ": the packet did not exist in 1.20.4");
    }

    /** (ClientCommonPacketListener and ClientLoginPacketListener get the cookie listener's method in 1.20.5) */
    public static void cookieRequest(net.minecraft.class_8705 listener, net.minecraft.class_9088 packet) {
        throw unknown(listener, packet);
    }

    public static void cookieRequest(net.minecraft.class_2896 listener, net.minecraft.class_9088 packet) {
        throw unknown(listener, packet);
    }

    public static void storeCookie(net.minecraft.class_8705 listener, net.minecraft.class_9150 packet) {
        throw unknown(listener, packet);
    }

    public static void transfer(net.minecraft.class_8705 listener, net.minecraft.class_9151 packet) {
        throw unknown(listener, packet);
    }

    /** (ServerCommonPacketListener and ServerLoginPacketListener get the cookie listener's method in 1.20.5) */
    public static void cookieResponse(net.minecraft.class_8706 listener, net.minecraft.class_9091 packet) {
        throw unknown(listener, packet);
    }

    public static void cookieResponse(net.minecraft.class_2911 listener, net.minecraft.class_9091 packet) {
        throw unknown(listener, packet);
    }

    public static void debugSample(net.minecraft.class_2602 listener, net.minecraft.class_9178 packet) {
        throw unknown(listener, packet);
    }

    public static void projectilePower(net.minecraft.class_2602 listener, net.minecraft.class_9632 packet) {
        throw unknown(listener, packet);
    }

    public static void debugSubscription(net.minecraft.class_2792 listener, net.minecraft.class_9179 packet) {
        throw unknown(listener, packet);
    }

    public static void selectKnownPacks(net.minecraft.class_8732 listener, net.minecraft.class_9250 packet) {
        throw unknown(listener, packet);
    }

    public static void resetChat(net.minecraft.class_8732 listener, net.minecraft.class_9448 packet) {
        throw unknown(listener, packet);
    }

    public static void selectedKnownPacks(net.minecraft.class_8735 listener, net.minecraft.class_9220 packet) {
        throw unknown(listener, packet);
    }

    /** handleSignedChatCommand(packet): 1.20.4's handleChatCommand with the signed packet as 1.20.4's */
    public static void signedChatCommand(net.minecraft.class_2792 listener, net.minecraft.class_9449 packet) {
        listener.method_43667(CbNetGame.chatCommandOf(packet));
    }

    private CbNetListeners() {
    }
}
