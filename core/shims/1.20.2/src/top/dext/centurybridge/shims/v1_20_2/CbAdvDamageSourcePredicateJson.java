package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2022;
import net.minecraft.class_2048;
import net.minecraft.class_3518;
import net.minecraft.class_7924;
import net.minecraft.class_8110;
import net.minecraft.class_8129;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * DamageSourcePredicate's 1.20.1 fromJson(json), restored on DamageSourcePredicate as a private static next to 1.20.2's
 * Optional answering one, with 1.20.1's body: ANY for null; tags (each through TagPredicate's restored reader, over the
 * damage type registry; none when absent), direct_entity and source_entity through EntityPredicate's restored reader; the
 * record takes the 1.20.1 constructor's values (ANY absent). 1.20.2's DamageSourcePredicate.fromJson (KilledTrigger's
 * killing_blow, DamagePredicate) consults it (CbAdvDamageSourcePredicateConsult); the restored DamagePredicate reader calls
 * it (CbAdvDamageSourcePredicateAccess).
 */
@Mixin(value = class_2022.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvDamageSourcePredicateJson {

    /** 1.20.1 DamageSourcePredicate.fromJson(json) */
    @CbVanillaReads("CbAdvDamageSourcePredicateConsult: 1.20.2's DamageSourcePredicate.fromJson consults it against its unmixed copy")
    private static class_2022 method_8846(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9533;
        }
        JsonObject json = class_3518.method_15295(element, "damage type");
        JsonArray array = class_3518.method_15292(json, "tags", null);
        List<class_8129<class_8110>> tags;
        if (array != null) {
            tags = new ArrayList<>(array.size());
            for (JsonElement e : array) {
                tags.add(CbAdvTagPredicateAccess.cb$fromJson(e, class_7924.field_42534));
            }
        } else {
            tags = List.of();
        }
        class_2048 direct = CbAdvEntityPredicateAccess.cb$fromJson(json.get("direct_entity"));
        class_2048 source = CbAdvEntityPredicateAccess.cb$fromJson(json.get("source_entity"));
        return new class_2022(tags, CbAdvAny.entity(direct), CbAdvAny.entity(source));
    }
}
