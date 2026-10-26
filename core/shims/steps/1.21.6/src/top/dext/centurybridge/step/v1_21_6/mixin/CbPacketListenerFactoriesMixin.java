package top.dext.centurybridge.step.v1_21_6.mixin;

import io.netty.channel.ChannelFutureListener;
import java.util.function.Supplier;
import net.minecraft.class_2596;
import net.minecraft.class_7648;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_6.rt.CbPackets;

/**
 * Records what 1.21.6's PacketSendListener factories made each listener from (javap: thenRun(Runnable) and
 * exceptionallySend(Supplier) each return a lambda capturing only its argument), so a 1.21.5 override of
 * Connection.send / ServerCommonPacketListenerImpl.send handed such a listener can call its 1.21.5 callbacks
 * (CbPackets.NettyListener). Observes only: the factories run as they are and answer their own listener.
 */
@Mixin(class_7648.class)
public abstract class CbPacketListenerFactoriesMixin {

    @Inject(method = "method_45084(Ljava/lang/Runnable;)Lio/netty/channel/ChannelFutureListener;", at = @At("RETURN"))
    private static void cb$recordThenRun(Runnable runnable, CallbackInfoReturnable<ChannelFutureListener> cir) {
        CbPackets.recordThenRun(cir.getReturnValue(), runnable);
    }

    @Inject(method = "method_45085(Ljava/util/function/Supplier;)Lio/netty/channel/ChannelFutureListener;", at = @At("RETURN"))
    private static void cb$recordExceptionallySend(Supplier<class_2596<?>> fallback, CallbackInfoReturnable<ChannelFutureListener> cir) {
        CbPackets.recordExceptionallySend(cir.getReturnValue(), fallback);
    }
}
