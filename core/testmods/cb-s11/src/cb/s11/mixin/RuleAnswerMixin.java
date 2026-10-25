package cb.s11.mixin;

import cb.s11.Driver;
import net.minecraft.class_1928;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.10 mod's mixins on BooleanValue.getCommandResult / serialize, active while the driver asks */
@Mixin(class_1928.class_4310.class)
public abstract class RuleAnswerMixin {
    @Inject(method = "method_20781()I", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbs11$loudResult(CallbackInfoReturnable<Integer> cir) {
        if (Driver.loudResult) {
            cir.setReturnValue(7);
        }
    }

    @Inject(method = "method_20779()Ljava/lang/String;", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbs11$loudSerialize(CallbackInfoReturnable<String> cir) {
        if (Driver.loudSerialize) {
            cir.setReturnValue("yes");
        }
    }
}
