package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetChat;

/**
 * ServerGamePacketListenerImpl.handleMessageDecodeFailure: 1.20.4 disconnected the player when the exception
 * said so and told them in red otherwise; 1.20.5 always tells. After the warning (logged as both log it), an
 * exception that disconnects (a 1.20.4 one constructed so, or overriding shouldDisconnect()) disconnects.
 */
@Mixin(net.minecraft.class_3244.class)
public abstract class CbNetDecodeFailureMixin {

    @Inject(method = "method_45171", cancellable = true, at = @At(value = "INVOKE", shift = At.Shift.AFTER,
        target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    private void cbS4net$disconnect(net.minecraft.class_7610.class_7825 e, CallbackInfo ci) {
        if (((CbNetChat.Disconnects) (Object) e).method_46314()) {
            ((net.minecraft.class_8609) (Object) this).method_52396(e.method_43626());
            ci.cancel();
        }
    }
}
