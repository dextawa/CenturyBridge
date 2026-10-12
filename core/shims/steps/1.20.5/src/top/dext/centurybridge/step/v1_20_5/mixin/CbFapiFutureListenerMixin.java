package top.dext.centurybridge.step.v1_20_5.mixin;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.networking.CbFutureListenerCallbacks;

/**
 * ClientConnection.sendInternal (method_36942): a packet sent with a 1.20.4 GenericFutureListener
 * ({@link CbFutureListenerCallbacks}) gets that listener on its write's channel future, plus vanilla's
 * FIRE_EXCEPTION_ON_FAILURE, in place of the callbacks wrapper -- 1.20.4 Fabric API's own
 * ClientConnectionMixin.sendInternal, at the same point of the same body (1.20.5 kept it unchanged).
 */
@Mixin(net.minecraft.class_2535.class)
public abstract class CbFapiFutureListenerMixin {

    @Inject(method = "method_36942", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE",
        target = "Lio/netty/channel/ChannelFuture;addListener(Lio/netty/util/concurrent/GenericFutureListener;)Lio/netty/channel/ChannelFuture;",
        remap = false, ordinal = 0))
    private void cb$futureListener(net.minecraft.class_2596<?> packet, net.minecraft.class_7648 callbacks, boolean flush,
                                   CallbackInfo ci, ChannelFuture channelFuture) {
        if (callbacks instanceof CbFutureListenerCallbacks holder) {
            channelFuture.addListener(holder.delegate());
            channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            ci.cancel();
        }
    }
}
