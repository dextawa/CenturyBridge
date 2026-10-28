package top.dext.centurybridge.step.v1_21_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_634;
import top.dext.centurybridge.step.v1_21_5.facade.AddExperienceOrbPacket;

/**
 * ClientPacketListener.handleAddExperienceOrb(packet) (CbOrbPackets' client half, kept apart so a dedicated server
 * never verifies a class naming ClientPacketListener): 1.21.5's handleAddEntity of the same packet.
 */
@Environment(EnvType.CLIENT)
public final class CbOrbPacketsClient {
    private CbOrbPacketsClient() {
    }

    public static void handleAddExperienceOrb(class_634 listener, AddExperienceOrbPacket packet) {
        listener.method_11112(packet);
    }
}
