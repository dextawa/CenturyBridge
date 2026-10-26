package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's EntityPredicate.fromJson(json) (method_8913): an entity condition marked "cb_smx1_entity_probe" (the test's
 * cb_smx1:entity_probe, player_killed_entity's entity, read through EntityPredicate.fromJson(json, name, context)) comes
 * back as "a creeper" at RETURN; every return is counted.
 */
@Mixin(net.minecraft.class_2048.class)
public abstract class EntityPredicateJsonMixin {
    @Inject(method = "method_8913(Lcom/google/gson/JsonElement;)Lnet/minecraft/class_2048;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$creeper(com.google.gson.JsonElement json, CallbackInfoReturnable<net.minecraft.class_2048> cir) {
        Driver.count("entity-predicate-return-all");
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cb_smx1_entity_probe")) {
            cir.setReturnValue(net.minecraft.class_2048.class_2049.method_8916().method_8921(net.minecraft.class_7923.field_41177.method_10223(new net.minecraft.class_2960("minecraft:creeper"))).method_8920());
        }
    }
}
