package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_2073;
import net.minecraft.class_3518;
import net.minecraft.class_3735;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * EntityEquipmentPredicate's 1.20.1 fromJson(json), gone from 1.20.2, restored on EntityEquipmentPredicate as a private
 * static with 1.20.1's body (ANY for null; each slot through ItemPredicate's restored reader, CbAdvItemPredicateAccess;
 * ANY slots absent in the record). The restored EntityPredicate reader calls it (CbAdvEquipmentPredicateAccess).
 */
@Mixin(value = class_3735.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvEquipmentPredicateJson {

    /** 1.20.1 EntityEquipmentPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: the restored EntityPredicate.fromJson calls it; 1.20.2's EntityPredicate readers consult"
        + " that against its unmixed copy")
    private static class_3735 method_16224(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_16485;
        }
        JsonObject json = class_3518.method_15295(element, "equipment");
        class_2073 head = CbAdvItemPredicateAccess.cb$fromJson(json.get("head"));
        class_2073 chest = CbAdvItemPredicateAccess.cb$fromJson(json.get("chest"));
        class_2073 legs = CbAdvItemPredicateAccess.cb$fromJson(json.get("legs"));
        class_2073 feet = CbAdvItemPredicateAccess.cb$fromJson(json.get("feet"));
        class_2073 mainhand = CbAdvItemPredicateAccess.cb$fromJson(json.get("mainhand"));
        class_2073 offhand = CbAdvItemPredicateAccess.cb$fromJson(json.get("offhand"));
        return new class_3735(CbAdvAny.item(head), CbAdvAny.item(chest), CbAdvAny.item(legs), CbAdvAny.item(feet), CbAdvAny.item(mainhand),
            CbAdvAny.item(offhand));
    }
}
