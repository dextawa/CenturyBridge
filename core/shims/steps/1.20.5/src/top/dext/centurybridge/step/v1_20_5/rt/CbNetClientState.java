package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_634;
import top.dext.centurybridge.step.v1_20_5.mixin.CbNetClientPacketListenerAccess;

/** Client-side connection state the network bridges answer from (area s4net): never loaded on a dedicated server. */
@Environment(EnvType.CLIENT)
public final class CbNetClientState {

    /** whether the server this client plays on enforces secure chat (1.20.5: from its login packet); null when not connected */
    public static Boolean cbServerEnforcesSecureChat() {
        class_310 mc = class_310.method_1551();
        class_634 connection = mc == null ? null : mc.method_1562();
        return connection == null ? null : ((CbNetClientPacketListenerAccess) connection).cb$serverEnforcesSecureChat();
    }

    private CbNetClientState() {
    }
}
