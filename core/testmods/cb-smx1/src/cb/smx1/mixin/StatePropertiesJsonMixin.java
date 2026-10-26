package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's StatePropertiesPredicate.fromJson(json) (method_22519): a state condition {"axis": {"min":
 * "cb_smx1_state_probe"}} (the test's cb_smx1:state_probe, enter_block; cb_smx1:state_probe2, inside fall_from_height's
 * start_position block) comes back as "axis x" at RETURN; every return is counted.
 */
@Mixin(net.minecraft.class_4559.class)
public abstract class StatePropertiesJsonMixin {
    @Inject(method = "method_22519(Lcom/google/gson/JsonElement;)Lnet/minecraft/class_4559;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$axisX(com.google.gson.JsonElement json, CallbackInfoReturnable<net.minecraft.class_4559> cir) {
        Driver.count("state-predicate-return-all");
        if (json != null && json.isJsonObject() && json.getAsJsonObject().get("axis") instanceof com.google.gson.JsonObject axis
                && axis.has("min") && "cb_smx1_state_probe".equals(axis.get("min").getAsString())) {
            cir.setReturnValue(net.minecraft.class_4559.class_4560.method_22523()
                .method_22525(net.minecraft.class_2741.field_12496, net.minecraft.class_2350.class_2351.field_11048).method_22528());
        }
    }
}
