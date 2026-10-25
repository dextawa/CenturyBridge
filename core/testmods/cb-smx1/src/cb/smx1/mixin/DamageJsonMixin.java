package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's DamagePredicate.fromJson(json) (method_8839): a damage condition marked "cb_smx1_damage_probe" (the test's
 * cb_smx1:damage_probe, entity_hurt_player's damage) comes back as "dealt at least 11" at RETURN; every return is counted.
 */
@Mixin(net.minecraft.class_2019.class)
public abstract class DamageJsonMixin {
    @Inject(method = "method_8839(Lcom/google/gson/JsonElement;)Lnet/minecraft/class_2019;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$eleven(com.google.gson.JsonElement json, CallbackInfoReturnable<net.minecraft.class_2019> cir) {
        Driver.count("damage-predicate-return-all");
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cb_smx1_damage_probe")) {
            cir.setReturnValue(new net.minecraft.class_2019(net.minecraft.class_2096.class_2099.method_9050(11.0), net.minecraft.class_2096.class_2099.field_9705,
                net.minecraft.class_2048.field_9599, null, net.minecraft.class_2022.field_9533));
        }
    }
}
