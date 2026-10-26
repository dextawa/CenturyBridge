package cb.smx1n.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.2's DistancePredicate.fromJson(json) (method_8857, an Optional): a distance condition marked "cbsmx1n_distance_probe"
 * (this mod's cb_smx1n:distance_probe, levitation's distance) comes back as "horizontally at least 9".
 */
@Mixin(net.minecraft.class_2025.class)
public abstract class DistancePredicateJsonMixin {
    @Inject(method = "method_8857(Lcom/google/gson/JsonElement;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1n$nine(com.google.gson.JsonElement json, CallbackInfoReturnable<java.util.Optional<net.minecraft.class_2025>> cir) {
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cbsmx1n_distance_probe")) {
            cir.setReturnValue(java.util.Optional.of(net.minecraft.class_2025.method_8860(net.minecraft.class_2096.class_2099.method_9050(9.0))));
        }
    }
}
