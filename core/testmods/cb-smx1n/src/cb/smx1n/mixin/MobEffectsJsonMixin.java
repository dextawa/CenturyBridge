package cb.smx1n.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.2's MobEffectsPredicate.fromJson(json) (method_9064, an Optional): an effects condition asking for speed with amplifier
 * at least 118 (this mod's cb_smx1n:effects_probe, effects_changed) comes back as "glowing".
 */
@Mixin(net.minecraft.class_2102.class)
public abstract class MobEffectsJsonMixin {
    @Inject(method = "method_9064(Lcom/google/gson/JsonElement;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1n$glowing(com.google.gson.JsonElement json, CallbackInfoReturnable<java.util.Optional<net.minecraft.class_2102>> cir) {
        if (json != null && json.isJsonObject() && json.getAsJsonObject().get("minecraft:speed") instanceof com.google.gson.JsonObject s
                && s.get("amplifier") instanceof com.google.gson.JsonObject a && a.has("min") && a.get("min").getAsInt() == 118) {
            cir.setReturnValue(net.minecraft.class_2102.class_8748.method_53200().method_53201(net.minecraft.class_7923.field_41174.method_10223(new net.minecraft.class_2960("minecraft:glowing"))).method_53203());
        }
    }
}
