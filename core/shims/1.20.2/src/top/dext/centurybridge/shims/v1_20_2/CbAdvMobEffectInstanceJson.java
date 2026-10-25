package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import net.minecraft.class_2096;
import net.minecraft.class_2102;
import net.minecraft.class_3518;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvRecords;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * MobEffectInstancePredicate's 1.20.1 fromJson(json), gone from 1.20.2, restored on MobEffectInstancePredicate as a
 * private static with 1.20.1's body (amplifier, duration; ambient / visible null when absent, the record's Optionals).
 * The restored MobEffectsPredicate reader calls it (CbAdvMobEffectInstanceAccess).
 */
@Mixin(value = class_2102.class_2103.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvMobEffectInstanceJson {

    /** 1.20.1 MobEffectInstancePredicate.fromJson(json) */
    @CbVanillaReads("CbAdvMobEffectsPredicateConsult: the restored MobEffectsPredicate.fromJson calls it; 1.20.2's MobEffectsPredicate.fromJson"
        + " consults that against its unmixed copy")
    private static class_2102.class_2103 method_9070(JsonObject json) {
        class_2096.class_2100 amplifier = class_2096.class_2100.method_9056(json.get("amplifier"));
        class_2096.class_2100 duration = class_2096.class_2100.method_9056(json.get("duration"));
        Boolean ambient = json.has("ambient") ? class_3518.method_15270(json, "ambient") : null;
        Boolean visible = json.has("visible") ? class_3518.method_15270(json, "visible") : null;
        return new class_2102.class_2103(amplifier, duration, CbAdvRecords.bool(ambient), CbAdvRecords.bool(visible));
    }
}
