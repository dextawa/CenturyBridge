package cb.smx1n.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.2's EntityPredicate.fromJson(json) (method_8913, an Optional): an entity condition marked "cbsmx1n_entity_probe"
 * (this mod's cb_smx1n:entity_probe, player_killed_entity's entity) comes back as "a skeleton".
 */
@Mixin(net.minecraft.class_2048.class)
public abstract class EntityPredicateJsonMixin {
    @Inject(method = "method_8913(Lcom/google/gson/JsonElement;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1n$skeleton(com.google.gson.JsonElement json, CallbackInfoReturnable<java.util.Optional<net.minecraft.class_2048>> cir) {
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cbsmx1n_entity_probe")) {
            cir.setReturnValue(java.util.Optional.of(net.minecraft.class_2048.class_2049.method_8916().method_8921(net.minecraft.class_7923.field_41177.method_10223(new net.minecraft.class_2960("minecraft:skeleton"))).method_8920()));
        }
    }
}
