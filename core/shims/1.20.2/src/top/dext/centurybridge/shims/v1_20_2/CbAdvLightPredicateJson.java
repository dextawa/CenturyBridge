package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_2096;
import net.minecraft.class_3518;
import net.minecraft.class_4552;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * LightPredicate's 1.20.1 fromJson(json), gone from 1.20.2, restored on LightPredicate as a private static with 1.20.1's
 * body (ANY for null, the "light" bounds). The restored LocationPredicate reader calls it (CbAdvLightPredicateAccess).
 */
@Mixin(value = class_4552.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvLightPredicateJson {

    /** 1.20.1 LightPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvLocationPredicateConsult: the restored LocationPredicate.fromJson calls it; 1.20.2's LocationPredicate.fromJson"
        + " consults that against its unmixed copy")
    private static class_4552 method_22482(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_20712;
        }
        JsonObject json = class_3518.method_15295(element, "light");
        class_2096.class_2100 light = class_2096.class_2100.method_9056(json.get("light"));
        return new class_4552(light);
    }
}
