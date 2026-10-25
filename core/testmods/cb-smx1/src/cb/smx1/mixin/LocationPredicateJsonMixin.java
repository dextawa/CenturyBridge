package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's LocationPredicate.fromJson(json) (method_9021): a location condition marked "cb_smx1_location_probe" (the
 * test's cb_smx1:location_probe, fall_from_height's start_position) comes back as "in the nether" at RETURN; every
 * return is counted.
 */
@Mixin(net.minecraft.class_2090.class)
public abstract class LocationPredicateJsonMixin {
    @Inject(method = "method_9021(Lcom/google/gson/JsonElement;)Lnet/minecraft/class_2090;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$nether(com.google.gson.JsonElement json, CallbackInfoReturnable<net.minecraft.class_2090> cir) {
        Driver.count("location-predicate-return-all");
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cb_smx1_location_probe")) {
            cir.setReturnValue(net.minecraft.class_2090.method_9016(net.minecraft.class_1937.field_25180));
        }
    }
}
