package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2540;
import net.minecraft.class_2899;
import net.minecraft.class_2960;
import net.minecraft.class_8595;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbNet;

/**
 * LoginQueryRequestS2CPacket: a (query id, channel, buffer) class in 1.20.1, a record around a
 * CustomQueryPayload in 1.20.2. The 1.20.1 accessors answer from the record (for converted calls
 * and a subclass's super calls -- the class is extendable again); vanilla 1.20.2 reads the
 * components and never calls them. A query on a channel 1.20.2 does not know keeps its bytes, as
 * 1.20.1 did, unless Fabric API's networking -- which does the same with its own payload -- is loaded.
 */
@Mixin(class_2899.class)
public abstract class CbNetQueryRequestBridge {

    /** was getQueryId() */
    public int method_12592() {
        return ((class_2899) (Object) this).comp_1567();
    }

    /** was getChannel() */
    public class_2960 method_36176() {
        return ((class_2899) (Object) this).comp_1568().comp_1571();
    }

    /** was getPayload(): the packet's own buffer, the same object on every call */
    public class_2540 method_36177() {
        return CbNet.cbQueryData(this, ((class_2899) (Object) this).comp_1568());
    }

    @Inject(method = "method_52287", at = @At("HEAD"), cancellable = true)
    private static void cb$retainUnknown(class_2960 channel, class_2540 buf, CallbackInfoReturnable<class_8595> cir) {
        if (!CbNet.FABRIC_RETAINS_PAYLOADS) {
            cir.setReturnValue(CbNet.cbRetainQuery(channel, buf));
        }
    }
}
