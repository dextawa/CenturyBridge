package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4 client multiplayer members (area s4fin, client/multiplayer) whose 1.20.5 form takes arguments 1.20.4
 * did not have, or that 1.20.5 moved:
 * <ul>
 * <li>ServerData.enforcesSecureChat: 1.20.4 kept it on the server entry (set from the server data packet);
 *     1.20.5 reads it from the login packet. Kept on the entry again (CbFinServerDataMixin), set when the
 *     login packet arrives (CbFinClientLoginMixin) and by 1.20.4's setter.</li>
 * <li>A handshake listener's transfer state and a connection cookie's server cookies, chat state and strict
 *     error handling: 1.20.4 had no transfers, cookies or carried chat, and handled errors leniently.</li>
 * <li>LoggedChatMessage codecs: 1.20.5's MapCodecs as 1.20.4's Codecs.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbFinClient {

    /** ServerData (CbFinServerDataMixin): 1.20.4's enforcesSecureChat */
    public interface ServerDataSlot {
        boolean cbS4$enforcesSecureChat();

        void cbS4$enforcesSecureChat(boolean enforces);
    }

    /** serverData.enforcesSecureChat (the field) */
    public static boolean field_3754(net.minecraft.class_642 data) {
        return ((ServerDataSlot) data).cbS4$enforcesSecureChat();
    }

    /** serverData.enforcesSecureChat = value */
    public static void field_3754(net.minecraft.class_642 data, boolean value) {
        ((ServerDataSlot) data).cbS4$enforcesSecureChat(value);
    }

    /** ClientHandshakePacketListenerImpl's transfer state: none (1.20.4 had no transfers) */
    public static net.minecraft.class_9112 noTransfer() {
        return null;
    }

    /** CommonListenerCookie's server cookies: none (1.20.4 had none) */
    public static java.util.Map<net.minecraft.class_2960, byte[]> noCookies() {
        return java.util.Map.of();
    }

    /** CommonListenerCookie's carried chat state: none (1.20.4 carried no chat across phases) */
    public static net.minecraft.class_338.class_9477 noChatState() {
        return null;
    }

    /** CommonListenerCookie's strict error handling: off, as 1.20.4 handled packet errors */
    public static boolean lenientErrors() {
        return false;
    }

    public static com.mojang.serialization.Codec field_40809() {
        return CbCodecs.fromMapCodec(net.minecraft.class_7557.class_7558.field_40809);
    }

    public static com.mojang.serialization.Codec field_40810() {
        return CbCodecs.fromMapCodec(net.minecraft.class_7557.class_7559.field_40810);
    }

    /** ClientLevel.overrideMapData(name, data): 1.20.4's map name, "map_" and the id, as a MapId */
    public static net.minecraft.class_9209 mapId(String name) {
        if (name == null || !name.startsWith("map_")) {
            throw new IllegalArgumentException("not a map name: " + name);
        }
        return new net.minecraft.class_9209(Integer.parseInt(name.substring(4)));
    }

    /** a MapId as 1.20.4's map name */
    public static String mapName(net.minecraft.class_9209 id) {
        return "map_" + id.comp_2315();
    }

    private CbFinClient() {
    }
}
