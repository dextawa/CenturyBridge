package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2540;
import net.minecraft.class_2913;
import net.minecraft.class_8594;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbNet;

/**
 * LoginQueryResponseC2SPacket: the serverbound twin of CbNetQueryRequestBridge. 1.20.1 read the
 * response as a nullable buffer; vanilla 1.20.2 skips whatever the client sent
 * (DiscardedQueryAnswerPayload), so without Fabric API's networking -- which reads it into its own
 * payload -- the response is read the 1.20.1 way.
 */
@Mixin(class_2913.class)
public abstract class CbNetQueryAnswerBridge {

    /** was getQueryId() */
    public int method_36178() {
        return ((class_2913) (Object) this).comp_1569();
    }

    /** was getResponse(): the packet's own (nullable) buffer, the same object on every call */
    public class_2540 method_36179() {
        return CbNet.cbQueryAnswerData(this, ((class_2913) (Object) this).comp_1570());
    }

    @Inject(method = "method_52290", at = @At("HEAD"), cancellable = true)
    private static void cb$readAnswer(int queryId, class_2540 buf, CallbackInfoReturnable<class_8594> cir) {
        if (!CbNet.FABRIC_RETAINS_PAYLOADS) {
            cir.setReturnValue(CbNet.cbReadQueryAnswer(buf));
        }
    }
}
