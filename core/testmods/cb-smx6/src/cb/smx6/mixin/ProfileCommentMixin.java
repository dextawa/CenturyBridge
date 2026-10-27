package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.6 mod's mixin on FilledProfileResults.getComment (class_3692.method_16062, private static): a cancellable RETURN
 * injector counting the calls and, while Driver.forceComment is set, answering its own comment. ProfileReportInvoker
 * makes the profiler report (getProfilerResults, method_16063, protected).
 */
@Mixin(net.minecraft.class_3692.class)
public abstract class ProfileCommentMixin {

    @Inject(method = "method_16062", at = @At("RETURN"), cancellable = true)
    private static void cbsmx6$comment(CallbackInfoReturnable<String> cir) {
        Driver.profileCommentAsked++;
        if (Driver.forceComment) {
            cir.setReturnValue("cb-smx6 witty profile");
        }
    }
}
