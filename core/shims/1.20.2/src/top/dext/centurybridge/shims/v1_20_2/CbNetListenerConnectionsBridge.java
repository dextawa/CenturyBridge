package top.dext.centurybridge.shims.v1_20_2;

import io.netty.channel.Channel;
import net.minecraft.class_2535;
import net.minecraft.class_2539;
import net.minecraft.class_2547;
import net.minecraft.class_2598;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbNetListeners;

/**
 * Connection: tells CbNetListeners which connection a listener is being attached to before 1.20.2
 * asks the listener its flow and protocol (setListener, setListenerForServerboundHandshake), and
 * answers the protocol of its inbound codec (the attribute of its receiving side).
 */
@Mixin(class_2535.class)
public abstract class CbNetListenerConnectionsBridge implements CbNetListeners.Attached {

    @Shadow
    @Final
    private class_2598 field_11643;

    @Shadow
    private Channel field_11651;

    @Inject(method = "method_10763", at = @At("HEAD"))
    private void cb$attaching(class_2547 listener, CallbackInfo ci) {
        CbNetListeners.attaching(listener, (class_2535) (Object) this);
    }

    @Inject(method = "method_52912", at = @At("HEAD"))
    private void cb$attachingHandshake(class_2547 listener, CallbackInfo ci) {
        CbNetListeners.attaching(listener, (class_2535) (Object) this);
    }

    @Override
    public class_2539 cb$inboundProtocol() {
        return field_11651.attr(field_11643 == class_2598.field_11941 ? class_2535.field_45666 : class_2535.field_45667)
            .get().method_52922();
    }
}
