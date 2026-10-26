package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_2040;
import net.minecraft.class_3518;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbAdvRecords;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * EntityFlagsPredicate's 1.20.1 fromJson(json) and its getOptionalBoolean helper, gone from 1.20.2, restored on
 * EntityFlagsPredicate as private statics with 1.20.1's bodies (ANY for null; each flag null when absent, the record's
 * Optionals). The restored EntityPredicate reader calls it (CbAdvEntityFlagsPredicateAccess).
 */
@Mixin(value = class_2040.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvEntityFlagsPredicateJson {

    /** 1.20.1 EntityFlagsPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: the restored EntityPredicate.fromJson calls it; 1.20.2's EntityPredicate readers consult"
        + " that against its unmixed copy")
    private static class_2040 method_8893(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9581;
        }
        JsonObject json = class_3518.method_15295(element, "entity flags");
        Boolean onFire = method_8895(json, "is_on_fire");
        Boolean sneaking = method_8895(json, "is_sneaking");
        Boolean sprinting = method_8895(json, "is_sprinting");
        Boolean swimming = method_8895(json, "is_swimming");
        Boolean baby = method_8895(json, "is_baby");
        return new class_2040(CbAdvRecords.bool(onFire), CbAdvRecords.bool(sneaking), CbAdvRecords.bool(sprinting), CbAdvRecords.bool(swimming),
            CbAdvRecords.bool(baby));
    }

    /** 1.20.1 EntityFlagsPredicate.getOptionalBoolean(json, name) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: the restored EntityFlagsPredicate.fromJson calls it")
    private static Boolean method_8895(JsonObject json, String name) {
        return json.has(name) ? class_3518.method_15270(json, name) : null;
    }
}
