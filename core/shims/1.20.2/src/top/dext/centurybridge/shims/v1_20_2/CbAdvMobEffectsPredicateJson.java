package top.dext.centurybridge.shims.v1_20_2;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import net.minecraft.class_1291;
import net.minecraft.class_2102;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbAdvRecords;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * MobEffectsPredicate's 1.20.1 fromJson(json), restored on MobEffectsPredicate as a private static next to 1.20.2's
 * Optional answering one, with 1.20.1's body: ANY for null, each effect id (1.20.1's message for an unknown one) with its
 * MobEffectInstancePredicate through that class's restored reader (CbAdvMobEffectInstanceAccess), in JSON order; the map
 * keyed by holder for 1.20.2's record (CbAdvRecords.effectMap, as 1.20.1's constructor's superCtorReshape). 1.20.2's
 * MobEffectsPredicate.fromJson (EffectsChangedTrigger) consults it (CbAdvMobEffectsPredicateConsult); the restored
 * EntityPredicate reader calls it (CbAdvMobEffectsPredicateAccess).
 */
@Mixin(value = class_2102.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvMobEffectsPredicateJson {

    /** 1.20.1 MobEffectsPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvMobEffectsPredicateConsult: 1.20.2's MobEffectsPredicate.fromJson consults it against its unmixed copy")
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static class_2102 method_9064(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9709;
        }
        JsonObject json = class_3518.method_15295(element, "effects");
        Map<class_1291, class_2102.class_2103> map = Maps.newLinkedHashMap();
        for (Map.Entry<String, JsonElement> e : json.entrySet()) {
            class_2960 id = new class_2960(e.getKey());
            class_1291 effect = class_7923.field_41174.method_17966(id).orElseThrow(() -> new JsonSyntaxException("Unknown effect '" + id + "'"));
            class_2102.class_2103 instance = CbAdvMobEffectInstanceAccess.cb$fromJson(class_3518.method_15295(e.getValue(), e.getKey()));
            map.put(effect, instance);
        }
        return new class_2102((Map) CbAdvRecords.effectMap(map));
    }
}
