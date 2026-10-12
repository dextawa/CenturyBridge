package top.dext.centurybridge.step.v1_20_5.mixin;

import net.minecraft.class_7495;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetPackets;

/**
 * ClientboundServerDataPacket.enforcesSecureChat(), gone from 1.20.5's packet (the flag moved to the
 * login packet): the flag 1.20.4 code made the packet with, or the session's (CbNetPackets.cbSecureChatOf).
 * A 1.20.4 subclass overriding it overrides this method; nothing in 1.20.5 asks it.
 */
@Mixin(class_7495.class)
public abstract class CbNetServerDataBridge {

    /** was enforcesSecureChat() */
    public boolean method_45058() {
        return CbNetPackets.cbSecureChatOf((class_7495) (Object) this);
    }
}
