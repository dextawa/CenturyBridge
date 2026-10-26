package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's FishingHookPredicate.fromJson(json) (method_26094): a fishing hook condition marked "cb_smx1_fishing_probe" (the
 * test's cb_smx1:fishing_probe) comes back as "not in open water" at RETURN; every return is counted.
 */
@Mixin(net.minecraft.class_4965.class)
public abstract class FishingHookPredicateJsonMixin {
    @Inject(method = "method_26094(Lcom/google/gson/JsonObject;)Lnet/minecraft/class_4965;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$notOpen(com.google.gson.JsonObject json, CallbackInfoReturnable<net.minecraft.class_4965> cir) {
        Driver.count("fishing-predicate-return-all");
        if (json != null && json.has("cb_smx1_fishing_probe")) {
            cir.setReturnValue(net.minecraft.class_4965.method_26095(false));
        }
    }
}
