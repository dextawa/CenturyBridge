package cb.smx1n.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.2's DamageSourcePredicate.fromJson(json) (method_8846, an Optional): a damage source condition marked
 * "cbsmx1n_dsource_probe" (this mod's cb_smx1n:dsource_probe, player_killed_entity's killing_blow) comes back as "tagged
 * is_lightning".
 */
@Mixin(net.minecraft.class_2022.class)
public abstract class DamageSourceJsonMixin {
    @Inject(method = "method_8846(Lcom/google/gson/JsonElement;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1n$lightning(com.google.gson.JsonElement json, CallbackInfoReturnable<java.util.Optional<net.minecraft.class_2022>> cir) {
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cbsmx1n_dsource_probe")) {
            cir.setReturnValue(java.util.Optional.of(new net.minecraft.class_2022(java.util.List.of(net.minecraft.class_8129.method_48965(net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_42534, new net.minecraft.class_2960("minecraft:is_lightning")))),
                java.util.Optional.empty(), java.util.Optional.empty())));
        }
    }
}
