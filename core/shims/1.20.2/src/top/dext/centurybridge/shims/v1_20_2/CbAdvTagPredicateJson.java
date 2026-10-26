package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import net.minecraft.class_8129;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * TagPredicate's 1.20.1 fromJson(json, registry), gone from 1.20.2 (its codec reads it), restored on TagPredicate as a
 * private static with 1.20.1's body ({"id": tag, "expected": bool}; 1.20.1's messages). The restored
 * DamageSourcePredicate reader calls it (CbAdvTagPredicateAccess).
 */
@Mixin(value = class_8129.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvTagPredicateJson {

    /** 1.20.1 TagPredicate.fromJson(json, registry) */
    @CbVanillaReads("CbAdvDamageSourcePredicateConsult: the restored DamageSourcePredicate.fromJson calls it; 1.20.2's DamageSourcePredicate"
        + " / DamagePredicate readers consult that against its unmixed copy")
    private static <T> class_8129<T> method_48966(JsonElement element, class_5321<? extends class_2378<T>> registry) {
        if (element == null) {
            throw new JsonParseException("Expected a tag predicate");
        }
        JsonObject json = class_3518.method_15295(element, "Tag Predicate");
        class_2960 id = new class_2960(class_3518.method_15265(json, "id"));
        boolean expected = class_3518.method_15270(json, "expected");
        return new class_8129<>(class_6862.method_40092(registry, id), expected);
    }
}
