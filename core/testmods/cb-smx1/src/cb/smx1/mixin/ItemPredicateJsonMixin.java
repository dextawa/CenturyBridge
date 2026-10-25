package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's ItemPredicate.fromJson(json) (method_8969): an item condition marked "cb_smx1_item_probe" (the test's
 * cb_smx1:item_probe) comes back as "a diamond" at RETURN; every return is counted.
 */
@Mixin(net.minecraft.class_2073.class)
public abstract class ItemPredicateJsonMixin {
    @Inject(method = "method_8969(Lcom/google/gson/JsonElement;)Lnet/minecraft/class_2073;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$diamond(com.google.gson.JsonElement json, CallbackInfoReturnable<net.minecraft.class_2073> cir) {
        Driver.count("item-predicate-return-all");
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cb_smx1_item_probe")) {
            cir.setReturnValue(net.minecraft.class_2073.class_2074.method_8973().method_8977(net.minecraft.class_1802.field_8477).method_8976());
        }
    }
}
