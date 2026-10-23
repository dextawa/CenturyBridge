package top.dext.centurybridge.step.v1_21.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6's RealmsServerPing (net/minecraft/class_4880): a Realms server's player count and newline-joined player
 * names, "0" and "" until RealmsServer.updateServerPing fills them. 1.21 removed it with RealmsServer.serverPing;
 * a 1.20.6 mod's references are renamed here (classRenames, s6fin-realms.json).
 */
@Environment(EnvType.CLIENT)
public class class_4880 extends net.minecraft.class_4352 {
    /** nrOfPlayers */
    public volatile String field_22624 = "0";
    /** playerList */
    public volatile String field_22625 = "";

    public class_4880() {
    }
}
