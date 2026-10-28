package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_1918;
import net.minecraft.class_2165;
import net.minecraft.class_2561;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbMixedMembers;

/**
 * The route split off CbCommandBlockRestoredMixin (default priority, so it keeps 1.21.9's order among mods' injectors):
 * while a converted mod mixed 1.21.8's BaseCommandBlock.sendSystemMessage / acceptsSuccess / acceptsFailure /
 * shouldInformAdmins (CbMixedMembers), 1.21.9's per-run command block source answers it from the block's restored
 * member as long as the run is open (closed, it keeps 1.21.9's answer: nothing more is recorded); unmixed, 1.21.9's
 * source runs untouched.
 */
@Mixin(targets = "net.minecraft.class_1918$class_11874")
public abstract class CbCommandBlockOutputHooksMixin {

    @Unique
    private static final String SEND = "net/minecraft/class_1918.method_43496(Lnet/minecraft/class_2561;)V";
    @Unique
    private static final String SUCCESS = "net/minecraft/class_1918.method_9200()Z";
    @Unique
    private static final String FAILURE = "net/minecraft/class_1918.method_9202()Z";
    @Unique
    private static final String ADMINS = "net/minecraft/class_1918.method_9201()Z";

    @Shadow
    private boolean field_62433;

    @Shadow
    @Final
    class_1918 field_62431;

    @Unique
    private class_2165 cb$block() {
        return (class_2165) (Object) this.field_62431;
    }

    @Inject(method = "method_43496(Lnet/minecraft/class_2561;)V", at = @At("HEAD"), cancellable = true)
    private void cb$routeSend(class_2561 message, CallbackInfo ci) {
        if (!this.field_62433 && CbMixedMembers.mixed(SEND)) {
            this.cb$block().method_43496(message);
            ci.cancel();
        }
    }

    @Inject(method = "method_9200()Z", at = @At("HEAD"), cancellable = true)
    private void cb$routeSuccess(CallbackInfoReturnable<Boolean> cir) {
        if (!this.field_62433 && CbMixedMembers.mixed(SUCCESS)) {
            cir.setReturnValue(this.cb$block().method_9200());
        }
    }

    @Inject(method = "method_9202()Z", at = @At("HEAD"), cancellable = true)
    private void cb$routeFailure(CallbackInfoReturnable<Boolean> cir) {
        if (!this.field_62433 && CbMixedMembers.mixed(FAILURE)) {
            cir.setReturnValue(this.cb$block().method_9202());
        }
    }

    @Inject(method = "method_9201()Z", at = @At("HEAD"), cancellable = true)
    private void cb$routeAdmins(CallbackInfoReturnable<Boolean> cir) {
        if (!this.field_62433 && CbMixedMembers.mixed(ADMINS)) {
            cir.setReturnValue(this.cb$block().method_9201());
        }
    }
}
