package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbAdvInstances;

/**
 * DistanceTrigger (nether_travel, fall_from_height, ride_entity_in_lava): one class registered under several ids, whose 1.20.1 createInstance handed
 * the trigger's own id to the instance it built. 1.20.2 builds the instance without one; it belongs
 * to the trigger that built it (CbAdvInstances), however createInstance was reached -- through
 * SimpleCriterionTrigger.createInstance(json, context) or a subclass's super call.
 */
@Mixin(net.minecraft.class_2108.class)
public abstract class CbAdvDecodedDistance {

    @Inject(method = "method_9078", at = @At("RETURN"))
    private void cb$decoded(com.google.gson.JsonObject json, java.util.Optional<net.minecraft.class_5258> player,
            net.minecraft.class_5257 context, CallbackInfoReturnable<net.minecraft.class_2108.class_2110> cir) {
        CbAdvInstances.belongs(cir.getReturnValue(), (net.minecraft.class_179<?>) (Object) this);
    }
}
