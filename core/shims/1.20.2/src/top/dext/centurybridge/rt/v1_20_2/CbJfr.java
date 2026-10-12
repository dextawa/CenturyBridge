package top.dext.centurybridge.rt.v1_20_2;

import net.minecraft.class_2539;

/**
 * 1.20.1 JFR packet profiling on 1.20.2's. 1.20.1 identified a packet's protocol by
 * NetworkState.getId() (-1 handshake, 0 play, 1 status, 2 login); 1.20.2 passes the NetworkState
 * itself to JvmProfiler and records its name ("handshake", "play", ...) in the packet events.
 * 1.20.2's configuration phase carries packets 1.20.1 sent in the play phase, so a 1.20.1
 * profiler sees it as play.
 */
public final class CbJfr {

    /** the state a 1.20.1 protocol id stood for; null for an id 1.20.1 never used */
    public static class_2539 state(int protocolId) {
        switch (protocolId) {
            case -1:
                return class_2539.field_20590;
            case 0:
                return class_2539.field_20591;
            case 1:
                return class_2539.field_20592;
            case 2:
                return class_2539.field_20593;
            default:
                return null;
        }
    }

    /** a 1.20.1 protocol id where 1.20.2 records the state's name */
    public static String stateName(int protocolId) {
        class_2539 s = state(protocolId);
        return s == null ? String.valueOf(protocolId) : s.method_10785();
    }

    /** the 1.20.1 protocol id of a state */
    public static int protocolId(class_2539 state) {
        if (state == class_2539.field_20590) {
            return -1;
        }
        if (state == class_2539.field_20592) {
            return 1;
        }
        if (state == class_2539.field_20593) {
            return 2;
        }
        return 0; // play, and the configuration phase split off it
    }

    /** the 1.20.1 protocol id behind a recorded state name */
    public static int protocolId(String stateName) {
        for (class_2539 s : class_2539.values()) {
            if (s.method_10785().equals(stateName)) {
                return protocolId(s);
            }
        }
        return 0;
    }

    /** PacketEvent.protocolId, an int in 1.20.1 */
    public static int protocolId(net.minecraft.class_6509 event) {
        return protocolId(event.protocolId);
    }

    /** PacketReceivedEvent.protocolId */
    public static int protocolId(net.minecraft.util.profiling.jfr.event.PacketReceivedEvent event) {
        return protocolId(event.protocolId);
    }

    /** PacketSentEvent.protocolId */
    public static int protocolId(net.minecraft.util.profiling.jfr.event.PacketSentEvent event) {
        return protocolId(event.protocolId);
    }

    /** PacketIdentification.protocolId(), an int in 1.20.1 */
    public static int protocolIdOf(String stateName) {
        return protocolId(stateName);
    }

    private CbJfr() {
    }
}
