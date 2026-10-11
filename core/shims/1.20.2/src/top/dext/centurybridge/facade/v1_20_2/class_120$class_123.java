package top.dext.centurybridge.facade.v1_20_2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

/**
 * Facade for 1.20.1 net/minecraft/class_120$class_123 (ConditionalLootFunction.Serializer),
 * deleted in 1.20.2's loot codec rework; mods' loot function serializers extend it. The
 * 1.20.1 bodies are ported: "conditions" read before the subclass's reader runs and
 * written ahead of its fields. Registered through LootFunctionType(JsonSerializer), which
 * the converter points at CbLootJson.codec.
 */
public abstract class class_120$class_123<T extends net.minecraft.class_120> implements class_5335<T> {

    public class_120$class_123() {
    }

    /** toJson(json, function, ctx) */
    public void method_529(JsonObject json, T function, JsonSerializationContext context) {
        java.util.List<net.minecraft.class_5341> conditions =
            ((top.dext.centurybridge.shims.v1_20_2.LootFunctionAccessor) function).cb$conditions();
        if (!conditions.isEmpty()) {
            json.add("conditions", context.serialize(conditions.toArray(new net.minecraft.class_5341[0])));
        }
    }

    /** fromJson(json, ctx) */
    public final T method_528(JsonObject json, JsonDeserializationContext context) {
        net.minecraft.class_5341[] conditions = net.minecraft.class_3518.method_15283(json, "conditions",
            new net.minecraft.class_5341[0], context, net.minecraft.class_5341[].class);
        return method_530(json, context, conditions);
    }

    /** fromJson(json, ctx, conditions) */
    public abstract T method_530(JsonObject json, JsonDeserializationContext context, net.minecraft.class_5341[] conditions);

    @Override
    public final T method_517(JsonObject json, JsonDeserializationContext context) {
        return method_528(json, context);
    }

    @Override
    public final void method_516(JsonObject json, T value, JsonSerializationContext context) {
        method_529(json, value, context);
    }
}
