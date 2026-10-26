package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_2019;
import net.minecraft.class_2022;
import net.minecraft.class_2048;
import net.minecraft.class_2096;
import net.minecraft.class_3518;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbAdvRecords;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * DamagePredicate's 1.20.1 fromJson(json), restored on DamagePredicate as a private static next to 1.20.2's Optional
 * answering one, with 1.20.1's body: ANY for null; dealt, taken, blocked (null when absent), source_entity through
 * EntityPredicate's restored reader, type through DamageSourcePredicate's (CbAdv*Access); the record takes the 1.20.1
 * constructor's values (ANY / null absent). 1.20.2's DamagePredicate.fromJson (EntityHurtPlayerTrigger,
 * PlayerHurtEntityTrigger) consults it (CbAdvDamagePredicateConsult).
 */
@Mixin(value = class_2019.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvDamagePredicateJson {

    /** 1.20.1 DamagePredicate.fromJson(json) */
    @CbVanillaReads("CbAdvDamagePredicateConsult: 1.20.2's DamagePredicate.fromJson consults it against its unmixed copy")
    private static class_2019 method_8839(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9520;
        }
        JsonObject json = class_3518.method_15295(element, "damage");
        class_2096.class_2099 dealt = class_2096.class_2099.method_9051(json.get("dealt"));
        class_2096.class_2099 taken = class_2096.class_2099.method_9051(json.get("taken"));
        Boolean blocked = json.has("blocked") ? class_3518.method_15270(json, "blocked") : null;
        class_2048 sourceEntity = CbAdvEntityPredicateAccess.cb$fromJson(json.get("source_entity"));
        class_2022 type = CbAdvDamageSourcePredicateAccess.cb$fromJson(json.get("type"));
        return new class_2019(dealt, taken, CbAdvAny.entity(sourceEntity), CbAdvRecords.bool(blocked), CbAdvAny.damageSource(type));
    }
}
