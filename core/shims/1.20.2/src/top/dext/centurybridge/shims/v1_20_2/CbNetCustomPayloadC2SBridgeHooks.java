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
 * The injectors into 1.20.2's own methods that go with {@link CbNetCustomPayloadC2SBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_2817.class)
public abstract class CbNetCustomPayloadC2SBridgeHooks {

    @Inject(method = "method_53026", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2817;method_53027(Lnet/minecraft/class_2960;Lnet/minecraft/class_2540;)Lnet/minecraft/class_8711;"),
        cancellable = true)
    private static void cb$retainUnknown(class_2960 id, class_2540 buf, CallbackInfoReturnable<class_8710> cir) {
        if (!CbNet.FABRIC_RETAINS_PAYLOADS) {
            cir.setReturnValue(CbNet.cbRetainUnknown(id, buf, 32767));
        }
    }
}
