package top.dext.centurybridge.step.v1_20_5.rt;

import net.minecraft.class_2539;

/**
 * 1.20.4's Packet.nextProtocol(), on a 1.20.4 mod's own packet class that answers it (the converter
 * adds this interface where the class declares the method): a 1.20.4 caller asking any packet gets
 * the mod's answer (CbNetPacketWrites.method_52273).
 */
public interface CbNetLegacyNextProtocol {

    /** was Packet.nextProtocol() */
    class_2539 method_52273();
}
