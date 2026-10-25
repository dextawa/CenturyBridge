package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import java.util.Optional;
import net.minecraft.class_4559;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbAdvPredicateJson;

/**
 * 1.20.2's StatePropertiesPredicate.fromJson(json) -- what 1.20.2's triggers read a block state condition with
 * (EnterBlockTrigger, SlideDownBlockTrigger) -- runs unchanged (a 1.20.2-era mod's mixin on it acts as without
 * CenturyBridge). At its return the restored 1.20.1 reader (CbAdvStatePropertiesJson) is consulted against its unmixable
 * copy (CbAdvPredicateJson): only where a converted mod's mixin made it answer otherwise is 1.20.1's answer what the
 * trigger gets.
 */
@Mixin(class_4559.class)
public abstract class CbAdvStatePropertiesConsult {

    @Inject(method = "method_22519(Lcom/google/gson/JsonElement;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true)
    private static void cb$consultFromJson(JsonElement json, CallbackInfoReturnable<Optional<class_4559>> cir) {
        Optional<class_4559> legacy = CbAdvPredicateJson.consultState(json);
        if (legacy != null) {
            cir.setReturnValue(legacy);
        }
    }
}
