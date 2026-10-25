package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.class_1887;
import net.minecraft.class_2035;
import net.minecraft.class_2096;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbAdvJson;
import top.dext.centurybridge.rt.v1_20_2.CbAdvRecords;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * EnchantmentPredicate's 1.20.1 fromJson(json) / fromJsonArray(json), gone from 1.20.2 (its codec reads it), restored on
 * EnchantmentPredicate as private statics with 1.20.1's bodies (ANY / NONE for null, 1.20.1's message; the 1.20.1
 * constructor's values into 1.20.2's record, CbAdvRecords.enchantmentPredicate). The restored 1.20.1 ItemPredicate
 * reader calls them (CbAdvEnchantmentPredicateAccess); 1.20.2's ItemPredicate readers consult it.
 */
@Mixin(value = class_2035.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvEnchantmentPredicateJson {

    /** 1.20.1 EnchantmentPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvItemPredicateConsult: fromJsonArray below calls it, which the restored ItemPredicate.fromJson calls")
    private static class_2035 method_8882(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9571;
        }
        JsonObject json = class_3518.method_15295(element, "enchantment");
        class_1887 enchantment = null;
        if (json.has("enchantment")) {
            class_2960 id = new class_2960(class_3518.method_15265(json, "enchantment"));
            enchantment = class_7923.field_41176.method_17966(id).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + id + "'"));
        }
        class_2096.class_2100 levels = class_2096.class_2100.method_9056(json.get("levels"));
        return CbAdvRecords.enchantmentPredicate(enchantment, levels);
    }

    /** 1.20.1 EnchantmentPredicate.fromJsonArray(json) */
    @CbVanillaReads("CbAdvItemPredicateConsult: the restored ItemPredicate.fromJson calls it; 1.20.2's ItemPredicate.fromJson consults that against its unmixed copy")
    private static class_2035[] method_8879(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvJson.field_20687;
        }
        JsonArray array = class_3518.method_15252(element, "enchantments");
        class_2035[] out = new class_2035[array.size()];
        for (int i = 0; i < out.length; ++i) {
            out[i] = method_8882(array.get(i));
        }
        return out;
    }
}
