package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's LightningBoltPredicate.fromJson(json) (method_37238): a lightning condition marked "cb_smx1_lightning_probe" (the
 * test's cb_smx1:lightning_probe) comes back as "set at least 3 blocks on fire" at RETURN; every return is counted.
 */
@Mixin(net.minecraft.class_6404.class)
public abstract class LightningPredicateJsonMixin {
    @Inject(method = "method_37238(Lcom/google/gson/JsonObject;)Lnet/minecraft/class_6404;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$three(com.google.gson.JsonObject json, CallbackInfoReturnable<net.minecraft.class_6404> cir) {
        Driver.count("lightning-predicate-return-all");
        if (json != null && json.has("cb_smx1_lightning_probe")) {
            cir.setReturnValue(net.minecraft.class_6404.method_37237(net.minecraft.class_2096.class_2100.method_9053(3)));
        }
    }
}
