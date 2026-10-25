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
 * The injectors into 1.20.2's own methods that go with {@link CbNetQueryAnswerBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_2913.class)
public abstract class CbNetQueryAnswerBridgeHooks {

    @Inject(method = "method_52290", at = @At("HEAD"), cancellable = true)
    private static void cb$readAnswer(int queryId, class_2540 buf, CallbackInfoReturnable<class_8594> cir) {
        if (!CbNet.FABRIC_RETAINS_PAYLOADS) {
            cir.setReturnValue(CbNet.cbReadQueryAnswer(buf));
        }
    }
}
