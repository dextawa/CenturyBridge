package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2540;
import net.minecraft.class_2602;
import net.minecraft.class_2658;
import net.minecraft.class_2960;
import net.minecraft.class_8710;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbNet;

/**
 * CustomPayloadS2CPacket: 1.20.1 was a (channel, data) class, 1.20.2 is a record around a
 * CustomPayload. The 1.20.1 accessors come back as methods of the record, answered from the
 * payload, so a converted mod's calls -- and the super calls of a subclass (the class is
 * extendable again, see cb.accesswidener) -- resolve with 1.20.1's results. Vanilla 1.20.2 itself
 * reads payload() and never calls these, so a subclass overriding them changes nothing vanilla sees.
 */
@Mixin(class_2658.class)
public abstract class CbNetCustomPayloadS2CBridge {

    /** was getChannel() */
    public class_2960 method_11456() {
        return CbNet.cbChannel(((class_2658) (Object) this).comp_1646());
    }

    /** was getData(): a fresh buffer with the payload's bytes, as 1.20.1 copied its data on every call */
    public class_2540 method_11458() {
        return CbNet.cbData(((class_2658) (Object) this).comp_1646());
    }

    /** was apply(ClientPlayPacketListener): 1.20.2's apply takes the common listener every play listener is */
    public void method_11457(class_2602 listener) {
        ((class_2658) (Object) this).method_53024(listener);
    }

    /**
     * Unknown channels: vanilla 1.20.2 skips their bytes (DiscardedPayload); 1.20.1 kept them for
     * getData(). Fabric API's networking replaces the same call with its own retaining payload, so
     * this only acts without it.
     */
    @Inject(method = "method_53023", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2658;method_53025(Lnet/minecraft/class_2960;Lnet/minecraft/class_2540;)Lnet/minecraft/class_8711;"),
        cancellable = true)
    private static void cb$retainUnknown(class_2960 id, class_2540 buf, CallbackInfoReturnable<class_8710> cir) {
        if (!CbNet.FABRIC_RETAINS_PAYLOADS) {
            cir.setReturnValue(CbNet.cbRetainUnknown(id, buf, 1048576));
        }
    }
}
