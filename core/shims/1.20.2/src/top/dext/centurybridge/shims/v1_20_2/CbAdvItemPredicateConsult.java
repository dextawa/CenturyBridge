package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_2073;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbAdvPredicateJson;

/**
 * 1.20.2's ItemPredicate.fromJson(json) / fromJsonArray(json) -- what 1.20.2's triggers read item conditions with --
 * run unchanged (a 1.20.2-era mod's mixin on them acts as without CenturyBridge). At their return the restored 1.20.1
 * readers (CbAdvItemPredicateJson) are consulted against their unmixable copies (CbAdvPredicateJson): only where a
 * converted mod's mixin made them answer otherwise is 1.20.1's answer what the trigger gets.
 */
@Mixin(class_2073.class)
public abstract class CbAdvItemPredicateConsult {

    @Inject(method = "method_8969(Lcom/google/gson/JsonElement;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true)
    private static void cb$consultFromJson(JsonElement json, CallbackInfoReturnable<Optional<class_2073>> cir) {
        Optional<class_2073> legacy = CbAdvPredicateJson.consultItem(json);
        if (legacy != null) {
            cir.setReturnValue(legacy);
        }
    }

    @Inject(method = "method_8972(Lcom/google/gson/JsonElement;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    private static void cb$consultFromJsonArray(JsonElement json, CallbackInfoReturnable<List<class_2073>> cir) {
        List<class_2073> legacy = CbAdvPredicateJson.consultItems(json);
        if (legacy != null) {
            cir.setReturnValue(legacy);
        }
    }
}
