package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's TagPredicate.fromJson(json, registry) (method_48966): a tag condition marked "cb_smx1_tag_probe" (the test's
 * cb_smx1:tag_probe, inside player_killed_entity's killing_blow tags) comes back as "not is_freezing" at RETURN; every
 * return is counted.
 */
@Mixin(net.minecraft.class_8129.class)
public abstract class TagPredicateJsonMixin {
    @Inject(method = "method_48966(Lcom/google/gson/JsonElement;Lnet/minecraft/class_5321;)Lnet/minecraft/class_8129;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$freezing(com.google.gson.JsonElement json, net.minecraft.class_5321<?> registry, CallbackInfoReturnable<net.minecraft.class_8129<?>> cir) {
        Driver.count("tag-predicate-return-all");
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cb_smx1_tag_probe")) {
            cir.setReturnValue(new net.minecraft.class_8129<>(net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_42534, new net.minecraft.class_2960("minecraft:is_freezing")), false));
        }
    }
}
