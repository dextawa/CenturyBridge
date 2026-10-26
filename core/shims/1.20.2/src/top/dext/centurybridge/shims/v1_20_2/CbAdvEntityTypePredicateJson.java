package top.dext.centurybridge.shims.v1_20_2;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.class_1299;
import net.minecraft.class_2050;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_6862;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * EntityTypePredicate's 1.20.1 fromJson(json), gone from 1.20.2, restored on EntityTypePredicate as a private static with
 * 1.20.1's body: ANY for null, "#tag" a tag, else a registered type (1.20.1's message listing the valid ones), as 1.20.2's
 * record (of(tag) / of(type)). The restored EntityPredicate reader calls it (CbAdvEntityTypePredicateAccess).
 */
@Mixin(value = class_2050.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvEntityTypePredicateJson {

    /** 1.20.1 EntityTypePredicate.fromJson(json) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: the restored EntityPredicate.fromJson calls it; 1.20.2's EntityPredicate readers consult"
        + " that against its unmixed copy")
    private static class_2050 method_8928(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9609;
        }
        String type = class_3518.method_15287(element, "type");
        if (type.startsWith("#")) {
            class_2960 id = new class_2960(type.substring(1));
            return class_2050.method_8926(class_6862.method_40092(class_7924.field_41266, id));
        }
        class_2960 id = new class_2960(type);
        class_1299<?> entityType = class_7923.field_41177.method_17966(id).orElseThrow(() -> new JsonSyntaxException(
            "Unknown entity type '" + id + "', valid types are: " + Joiner.on(",").join(class_7923.field_41177.method_10235())));
        return class_2050.method_8929(entityType);
    }
}
