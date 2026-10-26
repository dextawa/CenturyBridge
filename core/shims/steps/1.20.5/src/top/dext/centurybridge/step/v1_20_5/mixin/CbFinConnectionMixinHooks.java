package top.dext.centurybridge.step.v1_20_5.mixin;

import io.netty.channel.Channel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.facade.ConnectionProtocolCodecData;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinNet;

/**
 * The injectors into 1.20.5's own Connection.setupInboundProtocol / setupOutboundProtocol that go with
 * {@link CbFinConnectionMixin} (its restored 1.20.4 members stay there, at {@code CbVanillaReads.PRIORITY}): 1.20.4's
 * protocol attributes kept equal to the protocols 1.20.5 sets up.
 */
@Mixin(net.minecraft.class_2535.class)
public abstract class CbFinConnectionMixinHooks {

    @Shadow
    private Channel field_11651;

    @Inject(method = "method_56330", at = @At("TAIL"))
    private void cb$mirrorInbound(net.minecraft.class_9127<?> info, net.minecraft.class_2547 listener, CallbackInfo ci) {
        cb$mirror(info);
    }

    @Inject(method = "method_56329", at = @At("TAIL"))
    private void cb$mirrorOutbound(net.minecraft.class_9127<?> info, CallbackInfo ci) {
        cb$mirror(info);
    }

    @Unique
    private void cb$mirror(net.minecraft.class_9127<?> info) {
        if (this.field_11651 != null) {
            this.field_11651.attr(CbFinNet.key(info.comp_2235()))
                .set(new ConnectionProtocolCodecData<>(info.comp_2234(), info.comp_2235(), info));
        }
    }
}
