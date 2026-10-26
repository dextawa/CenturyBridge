package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetChat;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * SignedMessageChain.DecodeException: 1.20.4's shouldDisconnect() back on the exception -- what a 1.20.4
 * constructor said (rt/CbNetChat.decodeBuilt), false for 1.20.5's own (1.20.5 never disconnects for one);
 * a 1.20.4 subclass's override overrides it, and the server asks it (CbNetDecodeFailureMixin).
 */
@Mixin(value = net.minecraft.class_7610.class_7825.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbNetDecodeExceptionMixin implements CbNetChat.Disconnects {

    @Unique
    private boolean cbS4net$disconnects;

    /** was shouldDisconnect() */
    @Override
    public boolean method_46314() {
        return this.cbS4net$disconnects;
    }

    @Override
    public void cbS4net$disconnects(boolean disconnects) {
        this.cbS4net$disconnects = disconnects;
    }
}
