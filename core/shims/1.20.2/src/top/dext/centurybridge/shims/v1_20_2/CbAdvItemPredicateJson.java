package top.dext.centurybridge.shims.v1_20_2;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.class_1792;
import net.minecraft.class_1842;
import net.minecraft.class_2035;
import net.minecraft.class_2073;
import net.minecraft.class_2096;
import net.minecraft.class_2105;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_6862;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbAdvRecords;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * ItemPredicate's 1.20.1 fromJson(json) / fromJsonArray(json), restored on ItemPredicate as private statics next to
 * 1.20.2's Optional / List answering ones (another descriptor; Mixin merges a class mixin's statics only when private;
 * mods' plain calls keep their staticRedirects), with 1.20.1's bodies: ANY for null, 1.20.1's checks and messages,
 * NbtPredicate.fromJson / EnchantmentPredicate.fromJsonArray being those classes' restored statics, the 1.20.1
 * constructor's values going into 1.20.2's record (CbAdvRecords.itemPredicate). 1.20.2's ItemPredicate.fromJson /
 * fromJsonArray consult them (CbAdvItemPredicateConsult) against their unmixable copies (CbAdvPredicateJson).
 */
@Mixin(value = class_2073.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvItemPredicateJson {

    /** 1.20.1 ItemPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvItemPredicateConsult: 1.20.2's ItemPredicate.fromJson consults it against its unmixed copy")
    private static class_2073 method_8969(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9640;
        }
        JsonObject json = class_3518.method_15295(element, "item");
        class_2096.class_2100 count = class_2096.class_2100.method_9056(json.get("count"));
        class_2096.class_2100 durability = class_2096.class_2100.method_9056(json.get("durability"));
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        class_2105 nbt = CbAdvNbtPredicateAccess.cb$fromJson(json.get("nbt"));
        Set<class_1792> items = null;
        JsonArray array = class_3518.method_15292(json, "items", null);
        if (array != null) {
            ImmutableSet.Builder<class_1792> builder = ImmutableSet.builder();
            for (JsonElement e : array) {
                class_2960 id = new class_2960(class_3518.method_15287(e, "item"));
                builder.add(class_7923.field_41178.method_17966(id).orElseThrow(() -> new JsonSyntaxException("Unknown item id '" + id + "'")));
            }
            items = builder.build();
        }
        class_6862<class_1792> tag = null;
        if (json.has("tag")) {
            class_2960 id = new class_2960(class_3518.method_15265(json, "tag"));
            tag = class_6862.method_40092(class_7924.field_41197, id);
        }
        class_1842 potion = null;
        if (json.has("potion")) {
            class_2960 id = new class_2960(class_3518.method_15265(json, "potion"));
            potion = class_7923.field_41179.method_17966(id).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + id + "'"));
        }
        class_2035[] enchantments = CbAdvEnchantmentPredicateAccess.cb$fromJsonArray(json.get("enchantments"));
        class_2035[] stored = CbAdvEnchantmentPredicateAccess.cb$fromJsonArray(json.get("stored_enchantments"));
        return CbAdvRecords.itemPredicate(tag, items, count, durability, enchantments, stored, potion, nbt);
    }

    /** 1.20.1 ItemPredicate.fromJsonArray(json) */
    @CbVanillaReads("CbAdvItemPredicateConsult: 1.20.2's ItemPredicate.fromJsonArray consults it against its unmixed copy")
    private static class_2073[] method_8972(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return new class_2073[0];
        }
        JsonArray array = class_3518.method_15252(element, "items");
        class_2073[] out = new class_2073[array.size()];
        for (int i = 0; i < out.length; ++i) {
            out[i] = method_8969(array.get(i));
        }
        return out;
    }
}
