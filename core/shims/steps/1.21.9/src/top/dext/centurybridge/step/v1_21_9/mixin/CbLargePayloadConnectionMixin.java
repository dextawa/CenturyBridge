package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_2535;
import net.minecraft.class_2547;
import net.minecraft.class_9127;
import net.minecraft.class_9130;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_9.rt.CbLargePayloads;

/**
 * The restored PayloadTypeRegistry.registerLarge (Fabric API 0.136, rt/CbLargePayloads): when a connection sets up a
 * protocol, the setup task it hands the channel also puts the merger after the decoder (inbound) or the splitter after
 * the encoder (outbound), at 0.136's points -- only for a protocol a converted mod registered a large payload type
 * for; the task of every other protocol is passed on as it is. The protocol being set up is taken at the setup's head
 * (a @ModifyArg handler sees only the invoked method's arguments).
 */
@Mixin(class_2535.class)
public abstract class CbLargePayloadConnectionMixin {

    @Unique
    private class_9127<?> cb$inboundProtocol;

    @Unique
    private class_9127<?> cb$outboundProtocol;

    @Inject(method = "method_56330(Lnet/minecraft/class_9127;Lnet/minecraft/class_2547;)V", at = @At("HEAD"))
    private void cb$inboundProtocol(class_9127<?> protocol, class_2547 listener, CallbackInfo ci) {
        this.cb$inboundProtocol = protocol;
    }

    @ModifyArg(method = "method_56330(Lnet/minecraft/class_9127;Lnet/minecraft/class_2547;)V",
        at = @At(value = "INVOKE", target = "Lio/netty/channel/Channel;writeAndFlush(Ljava/lang/Object;)Lio/netty/channel/ChannelFuture;"))
    private Object cb$inbound(Object task) {
        class_9127<?> protocol = this.cb$inboundProtocol;
        this.cb$inboundProtocol = null;
        return protocol == null ? task : CbLargePayloads.cbInbound((class_9130.class_9132) task, protocol);
    }

    @Inject(method = "method_56329(Lnet/minecraft/class_9127;)V", at = @At("HEAD"))
    private void cb$outboundProtocol(class_9127<?> protocol, CallbackInfo ci) {
        this.cb$outboundProtocol = protocol;
    }

    @ModifyArg(method = "method_56329(Lnet/minecraft/class_9127;)V",
        at = @At(value = "INVOKE", target = "Lio/netty/channel/Channel;writeAndFlush(Ljava/lang/Object;)Lio/netty/channel/ChannelFuture;"))
    private Object cb$outbound(Object task) {
        class_9127<?> protocol = this.cb$outboundProtocol;
        this.cb$outboundProtocol = null;
        return protocol == null ? task : CbLargePayloads.cbOutbound((class_9130.class_9134) task, protocol);
    }
}
