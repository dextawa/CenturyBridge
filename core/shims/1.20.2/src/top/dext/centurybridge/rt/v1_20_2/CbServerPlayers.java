package top.dext.centurybridge.rt.v1_20_2;

import java.lang.invoke.MethodType;
import net.minecraft.class_2803;
import net.minecraft.class_3222;
import net.minecraft.class_8791;
import top.dext.centurybridge.shims.v1_20_2.CbCommonListenerLatencyAccess;

/**
 * 1.20.1 ServerPlayer members on 1.20.2 (s1misc).
 */
public final class CbServerPlayers {

    // ---- updateOptions: 1.20.1 took the client-information packet, 1.20.2 takes the ClientInformation it carries

    /** the packet a converted player's 1.20.1 updateOptions(packet) override is handed (override hook wrap) */
    public static class_2803 clientInformationPacket(class_8791 information) {
        return new class_2803(information);
    }

    /**
     * ServerPlayer's own 1.20.2 updateOptions(information), whatever a subclass overrides: the body of
     * 1.20.1's updateOptions(packet) (CbServerPlayerLegacy), which is what a converted override's
     * super call reaches.
     */
    public static void updateOptions(class_3222 player, class_8791 information) {
        try {
            CbSuper.special(class_3222.class, "method_14213", MethodType.methodType(void.class, class_8791.class))
                .invoke(player, information);
        } catch (Throwable t) {
            throw CbSuper.rethrow(t);
        }
    }

    // ---- latency: 1.20.1 kept it on the player (public int latency); 1.20.2 keeps it on the connection

    /** player.latency: the connection's (0 while the player has none, the field's default) */
    public static int field_13967(class_3222 player) {
        return player.field_13987 == null ? 0 : player.field_13987.method_52405();
    }

    /** player.latency = value: the connection's, which 1.20.2 reports in the player list as 1.20.1 did the field */
    public static void field_13967(class_3222 player, int value) {
        if (player.field_13987 != null) {
            ((CbCommonListenerLatencyAccess) player.field_13987).cb$setLatency(value);
        }
    }

    private CbServerPlayers() {
    }
}
