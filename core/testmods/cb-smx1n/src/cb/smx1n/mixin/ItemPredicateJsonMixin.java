package cb.smx1n.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.2's ItemPredicate.fromJson(json) (method_8969, an Optional) and fromJsonArray(json) (method_8972, a List), what
 * 1.20.2's triggers read item conditions with: an item condition marked "cbsmx1n_item_probe" (this mod's
 * cb_smx1n:item_probe, enchanted_item; cb_smx1n:items_probe, inventory_changed) comes back as "an emerald".
 */
@Mixin(net.minecraft.class_2073.class)
public abstract class ItemPredicateJsonMixin {
    @Inject(method = "method_8969(Lcom/google/gson/JsonElement;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1n$emerald(com.google.gson.JsonElement json, CallbackInfoReturnable<java.util.Optional<net.minecraft.class_2073>> cir) {
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cbsmx1n_item_probe")) {
            cir.setReturnValue(java.util.Optional.of(net.minecraft.class_2073.class_2074.method_8973().method_8977(net.minecraft.class_1802.field_8687).method_8976()));
        }
    }

    @Inject(method = "method_8972(Lcom/google/gson/JsonElement;)Ljava/util/List;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1n$emeralds(com.google.gson.JsonElement json, CallbackInfoReturnable<java.util.List<net.minecraft.class_2073>> cir) {
        if (json != null && json.isJsonArray() && json.getAsJsonArray().size() == 1 && json.getAsJsonArray().get(0).isJsonObject()
                && json.getAsJsonArray().get(0).getAsJsonObject().has("cbsmx1n_item_probe")) {
            cir.setReturnValue(java.util.List.of(net.minecraft.class_2073.class_2074.method_8973().method_8977(net.minecraft.class_1802.field_8687).method_8976()));
        }
    }
}
