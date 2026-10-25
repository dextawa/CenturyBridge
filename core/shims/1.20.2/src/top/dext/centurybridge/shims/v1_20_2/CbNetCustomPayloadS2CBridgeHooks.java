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
 * The injectors into 1.20.2's own methods that go with {@link CbNetCustomPayloadS2CBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_2658.class)
public abstract class CbNetCustomPayloadS2CBridgeHooks {
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
