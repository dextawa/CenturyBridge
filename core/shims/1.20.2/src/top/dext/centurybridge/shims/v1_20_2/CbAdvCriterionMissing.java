package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.1 criterion without a trigger instance (the client's, new Criterion()) is the pair of
 * CbAdvCriteria.MISSING and null. 1.20.1's serializeToJson refused one with "Missing trigger";
 * 1.20.2's would dereference the null instance.
 */
@Mixin(net.minecraft.class_175.class)
public abstract class CbAdvCriterionMissing {

    @Inject(method = "method_773", at = @At("HEAD"))
    private void cb$missingTrigger(CallbackInfoReturnable<com.google.gson.JsonElement> cir) {
        if (((CbAdvCriterionAccess) (Object) this).cb$instance() == null) {
            throw new com.google.gson.JsonSyntaxException("Missing trigger");
        }
    }
}
