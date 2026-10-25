package cb.smx1n.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.2's LocationPredicate.fromJson(json) (method_9021, an Optional), what 1.20.2's DistanceTrigger reads
 * start_position with: a location condition marked "cbsmx1n_location_probe" (this mod's cb_smx1n:location_probe) comes
 * back as "in the end".
 */
@Mixin(net.minecraft.class_2090.class)
public abstract class LocationPredicateJsonMixin {
    @Inject(method = "method_9021(Lcom/google/gson/JsonElement;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1n$end(com.google.gson.JsonElement json, CallbackInfoReturnable<java.util.Optional<net.minecraft.class_2090>> cir) {
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cbsmx1n_location_probe")) {
            cir.setReturnValue(java.util.Optional.of(net.minecraft.class_2090.class_2091.method_53182(net.minecraft.class_1937.field_25181).method_9023()));
        }
    }
}
