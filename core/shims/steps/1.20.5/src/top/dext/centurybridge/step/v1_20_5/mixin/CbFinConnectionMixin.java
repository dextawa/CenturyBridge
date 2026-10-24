package top.dext.centurybridge.step.v1_20_5.mixin;

import io.netty.channel.Channel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.facade.ConnectionProtocolCodecData;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinNet;

/**
 * Connection: 1.20.4's protocol attributes kept equal to the protocols 1.20.5 sets up (rt/CbFinNet), the
 * two 1.20.4 methods that paused and resumed reading around a protocol change, and the listener fields
 * 1.20.4's setListener replaced on their own.
 */
@Mixin(net.minecraft.class_2535.class)
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

    @Inject(method = "method_56330", at = @At("TAIL"))
    private void cb$mirrorInbound(net.minecraft.class_9127<?> info, net.minecraft.class_2547 listener, CallbackInfo ci) {
        cb$mirror(info);
    }

    @Inject(method = "method_56329", at = @At("TAIL"))
    private void cb$mirrorOutbound(net.minecraft.class_9127<?> info, CallbackInfo ci) {
        cb$mirror(info);
    }

    private void cb$mirror(net.minecraft.class_9127<?> info) {
        if (this.field_11651 != null) {
            this.field_11651.attr(CbFinNet.key(info.comp_2235()))
                .set(new ConnectionProtocolCodecData<>(info.comp_2234(), info.comp_2235(), info));
        }
    }
}
