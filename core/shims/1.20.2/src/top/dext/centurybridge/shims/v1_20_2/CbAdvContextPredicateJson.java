package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import java.util.List;
import net.minecraft.class_176;
import net.minecraft.class_5257;
import net.minecraft.class_5258;
import net.minecraft.class_5341;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * ContextAwarePredicate's 1.20.1 fromElement(name, context, json, params), restored on ContextAwarePredicate as a private
 * static next to 1.20.2's Optional-of-Optional answering one, with 1.20.1's body: a JSON array is a loot condition list
 * (the conditions read by the context's deserializer under "<advancement>/<name>", 1.20.2's List answering one), anything
 * else null; the predicate built with 1.20.1's constructor call (1.20.2's takes the List; package-private, opened by the
 * layer's access widener). The restored EntityPredicate.fromElement calls it (CbAdvContextPredicateAccess).
 */
@Mixin(value = class_5258.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvContextPredicateJson {

    /** 1.20.1 ContextAwarePredicate.fromElement(name, context, json, params) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: the restored EntityPredicate.fromElement calls it; 1.20.2's EntityPredicate.fromJson(json,"
        + " name, context) / fromJsonArray consult that against its unmixed copy")
    private static class_5258 method_27807(String name, class_5257 context, JsonElement json, class_176 params) {
        if (json != null && json.isJsonArray()) {
            List<class_5341> conditions = context.method_27796(json.getAsJsonArray(), context.method_27795() + "/" + name, params);
            return new class_5258(conditions);
        }
        return null;
    }
}
