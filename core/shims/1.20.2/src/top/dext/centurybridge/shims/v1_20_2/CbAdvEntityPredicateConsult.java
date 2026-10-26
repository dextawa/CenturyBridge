package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_2048;
import net.minecraft.class_5257;
import net.minecraft.class_5258;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbAdvPredicateJson;

/**
 * 1.20.2's EntityPredicate readers -- fromJson(json) (DamagePredicate's source_entity, and the triggers through the next
 * two), fromJson(json, name, context) (every trigger's player / entity condition) and fromJsonArray (KilledByCrossbow,
 * ChanneledLightning, Bred's victims) -- run unchanged (a 1.20.2-era mod's mixin on them acts as without CenturyBridge).
 * At their return the restored 1.20.1 readers (CbAdvEntityPredicateJson, with the restored part readers they call) are
 * consulted against their unmixable copies (CbAdvPredicateJson): only where a converted mod's mixin made them answer
 * otherwise is 1.20.1's answer what the caller gets.
 */
@Mixin(class_2048.class)
public abstract class CbAdvEntityPredicateConsult {

    @Inject(method = "method_8913(Lcom/google/gson/JsonElement;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true)
    private static void cb$consultFromJson(JsonElement json, CallbackInfoReturnable<Optional<class_2048>> cir) {
        Optional<class_2048> legacy = CbAdvPredicateJson.consultEntity(json);
        if (legacy != null) {
            cir.setReturnValue(legacy);
        }
    }

    @Inject(method = "method_51705(Lcom/google/gson/JsonObject;Ljava/lang/String;Lnet/minecraft/class_5257;)Ljava/util/Optional;",
        at = @At("RETURN"), cancellable = true)
    private static void cb$consultFromJsonNamed(JsonObject json, String name, class_5257 context, CallbackInfoReturnable<Optional<class_5258>> cir) {
        Optional<class_5258> legacy = CbAdvPredicateJson.consultEntityContext(json, name, context);
        if (legacy != null) {
            cir.setReturnValue(legacy);
        }
    }

    @Inject(method = "method_51707(Lcom/google/gson/JsonObject;Ljava/lang/String;Lnet/minecraft/class_5257;)Ljava/util/List;",
        at = @At("RETURN"), cancellable = true)
    private static void cb$consultFromJsonArray(JsonObject json, String name, class_5257 context, CallbackInfoReturnable<List<class_5258>> cir) {
        List<class_5258> legacy = CbAdvPredicateJson.consultEntityContexts(json, name, context);
        if (legacy != null) {
            cir.setReturnValue(legacy);
        }
    }
}
