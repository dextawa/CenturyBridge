package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's PlayerPredicate.fromJson(json) (method_22499): a player condition marked "cb_smx1_player_probe" (the test's
 * cb_smx1:player_probe, location's player type_specific) comes back as "in creative" at RETURN; every return is counted.
 */
@Mixin(net.minecraft.class_4553.class)
public abstract class PlayerPredicateJsonMixin {
    @Inject(method = "method_22499(Lcom/google/gson/JsonObject;)Lnet/minecraft/class_4553;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$creative(com.google.gson.JsonObject json, CallbackInfoReturnable<net.minecraft.class_4553> cir) {
        Driver.count("player-predicate-return-all");
        if (json != null && json.has("cb_smx1_player_probe")) {
            cir.setReturnValue(net.minecraft.class_4553.class_4557.method_35310().method_35312(net.minecraft.class_1934.field_9220).method_22507());
        }
    }
}
