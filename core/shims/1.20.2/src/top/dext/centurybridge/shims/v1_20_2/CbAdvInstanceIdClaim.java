package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbAdvInstances;

/**
 * AbstractCriterionTriggerInstance, whose 1.20.1 constructor took the id of the instance's trigger
 * (every vanilla instance and every 1.20.1 mod instance runs it). A converted 1.20.1 constructor
 * call -- super(id, player) or new KilledTrigger.TriggerInstance(id, ...) -- hands its id to
 * CbAdvInstances.stash right before the 1.20.2 constructor runs; the instance takes it here, as the
 * constructor returns.
 */
@Mixin(net.minecraft.class_195.class)
public abstract class CbAdvInstanceIdClaim {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$claimId(CallbackInfo ci) {
        CbAdvInstances.claim(this);
    }
}
