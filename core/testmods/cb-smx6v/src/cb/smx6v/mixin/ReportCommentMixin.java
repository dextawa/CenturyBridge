package cb.smx6v.mixin;

import cb.smx6v.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A native 1.21 mod's mixin on ReportType.getErrorComment (class_9813.method_60927), the method the 1.21 step layer's
 * CbReportCommentRouteMixin routes: a RETURN injector recording each answer.
 */
@Mixin(net.minecraft.class_9813.class)
public abstract class ReportCommentMixin {

    @Inject(method = "method_60927()Ljava/lang/String;", at = @At("RETURN"))
    private void cbsmx6v$comment(CallbackInfoReturnable<String> cir) {
        Driver.nativeComments++;
        Driver.nativeLastComment = cir.getReturnValue();
    }
}
