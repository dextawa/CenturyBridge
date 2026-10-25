package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_2025;
import net.minecraft.class_2040;
import net.minecraft.class_2048;
import net.minecraft.class_2050;
import net.minecraft.class_2090;
import net.minecraft.class_2102;
import net.minecraft.class_2105;
import net.minecraft.class_3518;
import net.minecraft.class_3735;
import net.minecraft.class_5257;
import net.minecraft.class_5258;
import net.minecraft.class_7376;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbAdvPredicateJson;
import top.dext.centurybridge.rt.v1_20_2.CbAdvRecords;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * EntityPredicate's 1.20.1 JSON readers, restored on EntityPredicate as private statics next to 1.20.2's Optional / List
 * answering ones (other descriptors), with 1.20.1's bodies: fromJson(json) (ANY for null, every part through its class's
 * restored reader -- CbAdv*Access -- vehicle / passenger / targeted_entity through this one, the parts going into 1.20.2's
 * record as 1.20.1's builder held them, CbAdvRecords.entityPredicate), and the player / entity readers of the triggers:
 * fromJson(json, name, context) (method_51705), fromJsonArray (method_51707) and fromElement (method_51706: a loot
 * condition array through ContextAwarePredicate's restored fromElement, else an entity predicate wrapped as one).
 *
 * type_specific goes through CbAdvPredicateJson.subPredicateFromJson: 1.20.1's EntitySubPredicate.fromJson, an interface
 * static the layer does not restore on the interface (CASE 12), with the "player", "lightning", "fishing_hook" and "slime"
 * types read by their restored 1.20.1 readers.
 *
 * 1.20.2's EntityPredicate.fromJson / fromJson(json, name, context) / fromJsonArray consult these
 * (CbAdvEntityPredicateConsult) against their unmixable copies (CbAdvPredicateJson).
 */
@Mixin(value = class_2048.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvEntityPredicateJson {

    /** 1.20.1 EntityPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: 1.20.2's EntityPredicate.fromJson consults it against its unmixed copy")
    private static class_2048 method_8913(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9599;
        }
        JsonObject json = class_3518.method_15295(element, "entity");
        class_2050 type = CbAdvEntityTypePredicateAccess.cb$fromJson(json.get("type"));
        class_2025 distance = CbAdvDistancePredicateAccess.cb$fromJson(json.get("distance"));
        class_2090 location = CbAdvLocationPredicateAccess.cb$fromJson(json.get("location"));
        class_2090 steppingOn = CbAdvLocationPredicateAccess.cb$fromJson(json.get("stepping_on"));
        class_2102 effects = CbAdvMobEffectsPredicateAccess.cb$fromJson(json.get("effects"));
        class_2105 nbt = CbAdvNbtPredicateAccess.cb$fromJson(json.get("nbt"));
        class_2040 flags = CbAdvEntityFlagsPredicateAccess.cb$fromJson(json.get("flags"));
        class_3735 equipment = CbAdvEquipmentPredicateAccess.cb$fromJson(json.get("equipment"));
        class_7376 typeSpecific = CbAdvPredicateJson.subPredicateFromJson(json.get("type_specific"));
        class_2048 vehicle = method_8913(json.get("vehicle"));
        class_2048 passenger = method_8913(json.get("passenger"));
        class_2048 target = method_8913(json.get("targeted_entity"));
        String team = class_3518.method_15253(json, "team", null);
        return CbAdvRecords.entityPredicate(type, distance, location, steppingOn, effects, nbt, flags, equipment, typeSpecific,
            vehicle, passenger, target, team);
    }

    /** 1.20.1 EntityPredicate.fromJson(json, name, context): a trigger's player / entity condition */
    @CbVanillaReads("CbAdvEntityPredicateConsult: 1.20.2's EntityPredicate.fromJson(json, name, context) consults it against its unmixed copy")
    private static class_5258 method_51705(JsonObject json, String name, class_5257 context) {
        return method_51706(name, context, json.get(name));
    }

    /** 1.20.1 EntityPredicate.fromJsonArray(json, name, context) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: 1.20.2's EntityPredicate.fromJsonArray consults it against its unmixed copy")
    private static class_5258[] method_51707(JsonObject json, String name, class_5257 context) {
        JsonElement element = json.get(name);
        if (element == null || element.isJsonNull()) {
            return new class_5258[0];
        }
        JsonArray array = class_3518.method_15252(element, name);
        class_5258[] out = new class_5258[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            out[i] = method_51706(name + "[" + i + "]", context, array.get(i));
        }
        return out;
    }

    /** 1.20.1 EntityPredicate.fromElement(name, context, json) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: the restored fromJson(json, name, context) / fromJsonArray call it")
    private static class_5258 method_51706(String name, class_5257 context, JsonElement json) {
        class_5258 conditions = CbAdvContextPredicateAccess.cb$fromElement(name, context, json, net.minecraft.class_173.field_24423);
        if (conditions != null) {
            return conditions;
        }
        class_2048 entity = method_8913(json);
        return CbAdvAny.method_51704(entity);
    }
}
