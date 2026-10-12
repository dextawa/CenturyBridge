package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbAdvInstances;
import top.dext.centurybridge.rt.v1_20_2.CbAdvTriggers;

/**
 * SimpleCriterionTrigger, the base of every vanilla trigger a 1.20.1 constructor named an id for:
 * <ul>
 * <li>a converted 1.20.1 call new KilledTrigger(id) / super(id) hands its id to
 *     CbAdvTriggers.stash right before the 1.20.2 constructor runs; the trigger takes it here, as the
 *     constructor returns;</li>
 * <li>an instance its createInstance(json, context) decodes belongs to it -- in 1.20.1 the trigger
 *     passed its own id into the instance (CbAdvInstances).</li>
 * </ul>
 */
@Mixin(net.minecraft.class_4558.class)
public abstract class CbAdvTriggerIdClaim {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$claimId(CallbackInfo ci) {
        CbAdvTriggers.claim(this);
    }

    @Inject(method = "method_27853", at = @At("RETURN"))
    private void cb$decoded(com.google.gson.JsonObject json, net.minecraft.class_5257 context,
            CallbackInfoReturnable<net.minecraft.class_4558.class_8788> cir) {
        CbAdvInstances.belongs(cir.getReturnValue(), (net.minecraft.class_179<?>) (Object) this);
    }
}
