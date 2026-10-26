package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's SlimePredicate.fromJson(json) (method_43158): a slime condition marked "cb_smx1_slime_probe" (the test's
 * cb_smx1:slime_probe) comes back as "size at least 4" at RETURN; every return is counted.
 */
@Mixin(net.minecraft.class_7383.class)
public abstract class SlimePredicateJsonMixin {
    @Inject(method = "method_43158(Lcom/google/gson/JsonObject;)Lnet/minecraft/class_7383;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$four(com.google.gson.JsonObject json, CallbackInfoReturnable<net.minecraft.class_7383> cir) {
        Driver.count("slime-predicate-return-all");
        if (json != null && json.has("cb_smx1_slime_probe")) {
            cir.setReturnValue(net.minecraft.class_7383.method_43157(net.minecraft.class_2096.class_2100.method_9053(4)));
        }
    }
}
