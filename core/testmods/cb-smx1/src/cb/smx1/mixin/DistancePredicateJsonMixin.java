package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's DistancePredicate.fromJson(json) (method_8857): a distance condition marked "cb_smx1_distance_probe" (the test's
 * cb_smx1:distance_probe, levitation's distance) comes back as "horizontally at least 7" at RETURN; every return is counted.
 */
@Mixin(net.minecraft.class_2025.class)
public abstract class DistancePredicateJsonMixin {
    @Inject(method = "method_8857(Lcom/google/gson/JsonElement;)Lnet/minecraft/class_2025;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$seven(com.google.gson.JsonElement json, CallbackInfoReturnable<net.minecraft.class_2025> cir) {
        Driver.count("distance-predicate-return-all");
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cb_smx1_distance_probe")) {
            cir.setReturnValue(net.minecraft.class_2025.method_8860(net.minecraft.class_2096.class_2099.method_9050(7.0)));
        }
    }
}
