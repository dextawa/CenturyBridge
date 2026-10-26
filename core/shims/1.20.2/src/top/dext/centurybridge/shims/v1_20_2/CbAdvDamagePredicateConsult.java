package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import java.util.Optional;
import net.minecraft.class_2019;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbAdvPredicateJson;

/**
 * 1.20.2's DamagePredicate.fromJson(json) (EntityHurtPlayerTrigger, PlayerHurtEntityTrigger) runs unchanged; at its return
 * the restored 1.20.1 reader (CbAdvDamagePredicateJson, with the restored EntityPredicate / DamageSourcePredicate /
 * TagPredicate readers it calls) is consulted against its unmixable copy (CbAdvPredicateJson), and 1.20.1's answer is what
 * the trigger gets only where a converted mod's mixin made the restored reader answer otherwise.
 */
@Mixin(class_2019.class)
public abstract class CbAdvDamagePredicateConsult {

    @Inject(method = "method_8839(Lcom/google/gson/JsonElement;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true)
    private static void cb$consultFromJson(JsonElement json, CallbackInfoReturnable<Optional<class_2019>> cir) {
        Optional<class_2019> legacy = CbAdvPredicateJson.consultDamage(json);
        if (legacy != null) {
            cir.setReturnValue(legacy);
        }
    }
}
