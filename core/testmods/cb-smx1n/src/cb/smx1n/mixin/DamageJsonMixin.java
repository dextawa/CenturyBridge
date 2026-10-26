package cb.smx1n.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.2's DamagePredicate.fromJson(json) (method_8839, an Optional): a damage condition marked "cbsmx1n_damage_probe" (this
 * mod's cb_smx1n:damage_probe, entity_hurt_player's damage) comes back as "dealt at least 13".
 */
@Mixin(net.minecraft.class_2019.class)
public abstract class DamageJsonMixin {
    @Inject(method = "method_8839(Lcom/google/gson/JsonElement;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1n$thirteen(com.google.gson.JsonElement json, CallbackInfoReturnable<java.util.Optional<net.minecraft.class_2019>> cir) {
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cbsmx1n_damage_probe")) {
            cir.setReturnValue(java.util.Optional.of(new net.minecraft.class_2019(net.minecraft.class_2096.class_2099.method_9050(13.0),
                net.minecraft.class_2096.class_2099.field_9705, java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty())));
        }
    }
}
