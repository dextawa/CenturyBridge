package top.dext.centurybridge.step.v1_20_5.mixin;

import net.minecraft.class_2535;
import net.minecraft.class_3246;
import net.minecraft.class_9127;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinNet;

/**
 * ServerHandshakePacketListenerImpl: where 1.20.4 called connection.setClientboundProtocolAfterHandshake(STATUS /
 * LOGIN), 1.20.5 calls setupOutboundProtocol(status / login protocol); a 1.20.4 Connection subclass's override of
 * the old method is reached there (rt/CbFinNet.afterHandshake).
 */
@Mixin(class_3246.class)
public abstract class CbFinHandshakeMixin {

    @Redirect(method = {"method_12576", "method_56048"}, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2535;method_56329(Lnet/minecraft/class_9127;)V"), require = 3)
    private void cb$afterHandshake(class_2535 connection, class_9127<?> info) {
        CbFinNet.afterHandshake(connection, info);
    }
}
