package top.dext.centurybridge.shims.v1_20_2;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import net.minecraft.class_3518;
import net.minecraft.class_4559;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbAdvRecords;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * StatePropertiesPredicate's 1.20.1 fromJson(json) and its two private helpers (one property's matcher, a bound's
 * string), gone from 1.20.2 (its codec reads the map), restored on StatePropertiesPredicate as private statics next to
 * 1.20.2's Optional answering fromJson (another descriptor), with 1.20.1's bodies: ANY for null, a string value an exact
 * matcher, {"min", "max"} a ranged one (an exact one when both are the same), the matchers being 1.20.2's records
 * (CbAdvRecords.exactProperty / rangedProperty). 1.20.2's StatePropertiesPredicate.fromJson consults the restored reader
 * (CbAdvStatePropertiesConsult); the restored Block / FluidPredicate readers call it (CbAdvStatePropertiesAccess).
 */
@Mixin(value = class_4559.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvStatePropertiesJson {

    /** 1.20.1 StatePropertiesPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvStatePropertiesConsult: 1.20.2's StatePropertiesPredicate.fromJson consults it against its unmixed copy;"
        + " CbAdvLocationPredicateConsult: through the restored Block / FluidPredicate readers")
    private static class_4559 method_22519(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_20736;
        }
        JsonObject json = class_3518.method_15295(element, "properties");
        List<class_4559.class_4562> matchers = Lists.newArrayList();
        for (Map.Entry<String, JsonElement> e : json.entrySet()) {
            matchers.add(method_22521(e.getKey(), e.getValue()));
        }
        return new class_4559(matchers);
    }

    /** 1.20.1 StatePropertiesPredicate.fromJson(name, json): one property's matcher */
    @CbVanillaReads("CbAdvStatePropertiesConsult: the restored StatePropertiesPredicate.fromJson calls it")
    private static class_4559.class_4562 method_22521(String name, JsonElement element) {
        if (element.isJsonPrimitive()) {
            return CbAdvRecords.exactProperty(name, element.getAsString());
        }
        JsonObject json = class_3518.method_15295(element, "value");
        String min = json.has("min") ? method_22522(json.get("min")) : null;
        String max = json.has("max") ? method_22522(json.get("max")) : null;
        return min != null && min.equals(max) ? CbAdvRecords.exactProperty(name, min) : CbAdvRecords.rangedProperty(name, min, max);
    }

    /** 1.20.1 StatePropertiesPredicate.getStringOrNull(json) */
    @CbVanillaReads("CbAdvStatePropertiesConsult: the restored StatePropertiesPredicate.fromJson(name, json) calls it")
    private static String method_22522(JsonElement element) {
        return element.isJsonNull() ? null : element.getAsString();
    }
}
