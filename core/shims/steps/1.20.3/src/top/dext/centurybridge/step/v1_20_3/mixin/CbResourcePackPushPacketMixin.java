package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * ClientboundResourcePackPacket's 1.20.2 getters on 1.20.3's ClientboundResourcePackPushPacket record --
 * getUrl(), getHash(), isRequired(), getPrompt() -- each forwarding to its record accessor virtually (a 1.20.2
 * subclass's override of a getter is renamed to the accessor, overrideRenames).
 */
@Mixin(value = net.minecraft.class_2720.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbResourcePackPushPacketMixin {

    private net.minecraft.class_2720 cb$self() {
        return (net.minecraft.class_2720) (Object) this;
    }

    public String method_11772() {
        return cb$self().comp_2159();
    }

    public String method_11773() {
        return cb$self().comp_2160();
    }

    public boolean method_32307() {
        return cb$self().comp_2161();
    }

    public net.minecraft.class_2561 method_36340() {
        return cb$self().comp_2162();
    }
}
