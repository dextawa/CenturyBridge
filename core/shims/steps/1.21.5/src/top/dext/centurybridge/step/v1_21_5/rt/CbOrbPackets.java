package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_2602;
import net.minecraft.class_9094;
import net.minecraft.class_9145;
import top.dext.centurybridge.step.v1_21_5.facade.AddExperienceOrbPacket;

/**
 * What 1.21.4 reached around ClientboundAddExperienceOrbPacket (facade/AddExperienceOrbPacket) outside the packet:
 * the listener's handleAddExperienceOrb and the packet's type constant. 1.21.5 spawns orbs through
 * ClientboundAddEntityPacket (javap 1.21.5 class_2602: no method_11091; class_9094: no field_48149).
 */
public final class CbOrbPackets {
    /** GamePacketTypes.CLIENTBOUND_ADD_EXPERIENCE_ORB: the type the facade packet is sent as (1.21.5's ADD_ENTITY) */
    public static final class_9145<?> field_48149 = class_9094.field_48145;

    private CbOrbPackets() {
    }

    /** ClientGamePacketListener.handleAddExperienceOrb(packet): 1.21.5's handleAddEntity of the same packet */
    public static void handleAddExperienceOrb(class_2602 listener, AddExperienceOrbPacket packet) {
        listener.method_11112(packet);
    }
}
