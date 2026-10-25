package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's DamageSourcePredicate.fromJson(json) (method_8846): a damage source condition marked "cb_smx1_dsource_probe" (the
 * test's cb_smx1:dsource_probe, player_killed_entity's killing_blow) comes back as "tagged is_fire" at RETURN; every return
 * is counted.
 */
@Mixin(net.minecraft.class_2022.class)
public abstract class DamageSourceJsonMixin {
    @Inject(method = "method_8846(Lcom/google/gson/JsonElement;)Lnet/minecraft/class_2022;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$fire(com.google.gson.JsonElement json, CallbackInfoReturnable<net.minecraft.class_2022> cir) {
        Driver.count("dsource-predicate-return-all");
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cb_smx1_dsource_probe")) {
            cir.setReturnValue(new net.minecraft.class_2022(java.util.List.of(net.minecraft.class_8129.method_48965(net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_42534, new net.minecraft.class_2960("minecraft:is_fire")))),
                net.minecraft.class_2048.field_9599, net.minecraft.class_2048.field_9599));
        }
    }
}
