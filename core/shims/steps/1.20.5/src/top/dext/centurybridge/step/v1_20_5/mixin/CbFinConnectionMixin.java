package top.dext.centurybridge.step.v1_20_5.mixin;

import io.netty.channel.Channel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinNet;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * Connection: 1.20.4's protocol attributes kept equal to the protocols 1.20.5 sets up (rt/CbFinNet;
 * CbFinConnectionMixinHooks, default priority), the two 1.20.4 methods that paused and resumed reading around a
 * protocol change, and the listener fields 1.20.4's setListener replaced on their own.
 */
@Mixin(value = net.minecraft.class_2535.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbFinConnectionMixin implements CbFinNet.ConnectionListeners {

    @Shadow
    private Channel field_11651;

    @Shadow
    private volatile net.minecraft.class_2547 field_11652;

    @Shadow
    private volatile net.minecraft.class_2547 field_45669;

    /** was suspendInboundAfterProtocolChange(): 1.20.4's body */
    public void method_52898() {
        this.field_11651.config().setAutoRead(false);
    }

    /** was resumeInboundAfterProtocolChange(): 1.20.4's body */
    public void method_52910() {
        this.field_11651.config().setAutoRead(true);
    }

    @Override
    public void cb$setListener(net.minecraft.class_2547 listener) {
        this.field_11652 = listener;
        this.field_45669 = null;
    }
}
