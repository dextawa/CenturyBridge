package top.dext.centurybridge.compat.fapi.impl.entity;

/**
 * Fabric API's FakePlayer overrides ServerPlayerEntity.setClientSettings, which 1.20.2 changed from
 * taking the client-options packet to taking the client options themselves (the packet is now a record
 * around them). A 1.20.1 call hands the packet: the options it carries are passed on (callReshapes). A
 * 1.20.1 override of the packet shape is reached from the options shape with the packet 1.20.2 would
 * have carried them in (overrideHooks wrap).
 */
public final class CbFapiFakePlayers {

    private CbFapiFakePlayers() {
    }

    /** the client options a client-options packet carries */
    public static net.minecraft.class_8791 clientInformation(net.minecraft.class_2803 packet) {
        return packet.comp_1963();
    }

    /** the client-options packet carrying these options */
    public static net.minecraft.class_2803 clientInformationPacket(net.minecraft.class_8791 information) {
        return new net.minecraft.class_2803(information);
    }
}
