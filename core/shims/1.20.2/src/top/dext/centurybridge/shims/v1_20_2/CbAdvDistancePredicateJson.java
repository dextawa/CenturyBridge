package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_2025;
import net.minecraft.class_2096;
import net.minecraft.class_3518;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * DistancePredicate's 1.20.1 fromJson(json), restored on DistancePredicate as a private static next to 1.20.2's Optional
 * answering one, with 1.20.1's body (ANY for null; x, y, z, horizontal, absolute bounds; the record takes them as 1.20.1's
 * constructor did). 1.20.2's DistancePredicate.fromJson (DistanceTrigger, LevitationTrigger) consults it
 * (CbAdvDistancePredicateConsult); the restored EntityPredicate reader calls it (CbAdvDistancePredicateAccess).
 */
@Mixin(value = class_2025.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvDistancePredicateJson {

    /** 1.20.1 DistancePredicate.fromJson(json) */
    @CbVanillaReads("CbAdvDistancePredicateConsult: 1.20.2's DistancePredicate.fromJson consults it against its unmixed copy")
    private static class_2025 method_8857(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9553;
        }
        JsonObject json = class_3518.method_15295(element, "distance");
        class_2096.class_2099 x = class_2096.class_2099.method_9051(json.get("x"));
        class_2096.class_2099 y = class_2096.class_2099.method_9051(json.get("y"));
        class_2096.class_2099 z = class_2096.class_2099.method_9051(json.get("z"));
        class_2096.class_2099 horizontal = class_2096.class_2099.method_9051(json.get("horizontal"));
        class_2096.class_2099 absolute = class_2096.class_2099.method_9051(json.get("absolute"));
        return new class_2025(x, y, z, horizontal, absolute);
    }
}
