package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.class_3518;
import net.minecraft.class_4965;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * FishingHookPredicate's 1.20.1 fromJson(json), restored as a private static with 1.20.1's body (in_open_water when
 * present, else ANY). See CbAdvLightningJson.
 */
@Mixin(value = class_4965.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvFishingHookJson {

    /** 1.20.1 FishingHookPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: the restored EntityPredicate.fromJson reaches it through type_specific \"fishing_hook\";"
        + " 1.20.2's EntityPredicate readers consult that against its unmixed copy")
    private static class_4965 method_26094(JsonObject json) {
        if (json.get("in_open_water") != null) {
            return new class_4965(Optional.of(class_3518.method_15268(json.get("in_open_water"), "in_open_water")));
        }
        return class_4965.field_23137;
    }
}
