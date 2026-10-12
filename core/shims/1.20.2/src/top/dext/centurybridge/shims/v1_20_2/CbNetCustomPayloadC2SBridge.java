package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2540;
import net.minecraft.class_2817;
import net.minecraft.class_2960;
import net.minecraft.class_8710;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbNet;

/**
 * CustomPayloadC2SPacket: the serverbound twin of CbNetCustomPayloadS2CBridge -- the 1.20.1
 * accessors answered from the 1.20.2 record's payload, and an unknown channel's bytes kept where
 * vanilla 1.20.2 skips them (unless Fabric API's networking, which keeps them itself, is loaded).
 */
@Mixin(class_2817.class)
public abstract class CbNetCustomPayloadC2SBridge {

    /** was getChannel() */
    public class_2960 method_36169() {
        return CbNet.cbChannel(((class_2817) (Object) this).comp_1647());
    }

    /** was getData(): a fresh buffer with the payload's bytes */
    public class_2540 method_36170() {
        return CbNet.cbData(((class_2817) (Object) this).comp_1647());
    }

    @Inject(method = "method_53026", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2817;method_53027(Lnet/minecraft/class_2960;Lnet/minecraft/class_2540;)Lnet/minecraft/class_8711;"),
        cancellable = true)
    private static void cb$retainUnknown(class_2960 id, class_2540 buf, CallbackInfoReturnable<class_8710> cir) {
        if (!CbNet.FABRIC_RETAINS_PAYLOADS) {
            cir.setReturnValue(CbNet.cbRetainUnknown(id, buf, 32767));
        }
    }
}
