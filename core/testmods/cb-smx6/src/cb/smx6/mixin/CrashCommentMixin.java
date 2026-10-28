package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.6 mod's mixin on CrashReport.getErrorComment (class_128.method_573, private static): a cancellable RETURN
 * injector counting the calls and, while Driver.forceComment is set, answering its own comment.
 */
@Mixin(net.minecraft.class_128.class)
public abstract class CrashCommentMixin {

    @Inject(method = "method_573", at = @At("RETURN"), cancellable = true)
    private static void cbsmx6$comment(CallbackInfoReturnable<String> cir) {
        Driver.crashCommentAsked++;
        if (Driver.forceComment) {
            cir.setReturnValue("cb-smx6 witty crash");
        }
    }
}
