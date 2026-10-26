package top.dext.centurybridge.step.v1_21_9.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.class_2545;
import net.minecraft.class_2596;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_9.rt.CbLargePayloads;

/**
 * The packet encoder writes a CbPassthroughPacket (a large-payload packet the restored splitter already encoded and
 * found small enough) as its bytes, as Fabric API 0.136's EncoderHandlerMixin did. Every real packet is encoded as in
 * 1.21.9.
 */
@Mixin(class_2545.class)
public abstract class CbPassthroughEncoderMixin {

    @Inject(method = "method_10838(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/class_2596;Lio/netty/buffer/ByteBuf;)V",
        at = @At("HEAD"), cancellable = true)
    private void cb$passthrough(ChannelHandlerContext ctx, class_2596<?> packet, ByteBuf out, CallbackInfo ci) {
        if (CbLargePayloads.cbPassthrough(packet, out)) {
            ci.cancel();
        }
    }
}
