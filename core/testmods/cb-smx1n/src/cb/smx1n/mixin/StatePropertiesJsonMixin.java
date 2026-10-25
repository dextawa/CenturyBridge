package cb.smx1n.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.2's StatePropertiesPredicate.fromJson(json) (method_22519, an Optional), what 1.20.2's EnterBlockTrigger reads
 * its state with: a state condition {"axis": {"min": "cbsmx1n_state_probe"}} (this mod's cb_smx1n:state_probe) comes
 * back as "axis z".
 */
@Mixin(net.minecraft.class_4559.class)
public abstract class StatePropertiesJsonMixin {
    @Inject(method = "method_22519(Lcom/google/gson/JsonElement;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1n$axisZ(com.google.gson.JsonElement json, CallbackInfoReturnable<java.util.Optional<net.minecraft.class_4559>> cir) {
        if (json != null && json.isJsonObject() && json.getAsJsonObject().get("axis") instanceof com.google.gson.JsonObject axis
                && axis.has("min") && "cbsmx1n_state_probe".equals(axis.get("min").getAsString())) {
            cir.setReturnValue(net.minecraft.class_4559.class_4560.method_22523()
                .method_22525(net.minecraft.class_2741.field_12496, net.minecraft.class_2350.class_2351.field_11051).method_22528());
        }
    }
}
