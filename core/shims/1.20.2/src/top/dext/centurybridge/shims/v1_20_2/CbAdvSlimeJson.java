package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import net.minecraft.class_2096;
import net.minecraft.class_7383;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * SlimePredicate's 1.20.1 fromJson(json), restored as a private static with 1.20.1's body (size). See CbAdvLightningJson.
 */
@Mixin(value = class_7383.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvSlimeJson {

    /** 1.20.1 SlimePredicate.fromJson(json) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: the restored EntityPredicate.fromJson reaches it through type_specific \"slime\";"
        + " 1.20.2's EntityPredicate readers consult that against its unmixed copy")
    private static class_7383 method_43158(JsonObject json) {
        class_2096.class_2100 size = class_2096.class_2100.method_9056(json.get("size"));
        return new class_7383(size);
    }
}
