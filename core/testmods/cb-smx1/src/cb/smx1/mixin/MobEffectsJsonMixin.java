package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's MobEffectsPredicate.fromJson(json) (method_9064): an effects condition asking for speed with amplifier at least 117
 * (the test's cb_smx1:effects_probe, effects_changed) comes back as "glowing" at RETURN; every return is counted.
 */
@Mixin(net.minecraft.class_2102.class)
public abstract class MobEffectsJsonMixin {
    @Inject(method = "method_9064(Lcom/google/gson/JsonElement;)Lnet/minecraft/class_2102;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$glowing(com.google.gson.JsonElement json, CallbackInfoReturnable<net.minecraft.class_2102> cir) {
        Driver.count("effects-predicate-return-all");
        if (json != null && json.isJsonObject() && json.getAsJsonObject().get("minecraft:speed") instanceof com.google.gson.JsonObject s
                && s.get("amplifier") instanceof com.google.gson.JsonObject a && a.has("min") && a.get("min").getAsInt() == 117) {
            cir.setReturnValue(net.minecraft.class_2102.method_9066().method_9065(net.minecraft.class_7923.field_41174.method_10223(new net.minecraft.class_2960("minecraft:glowing"))));
        }
    }
}
