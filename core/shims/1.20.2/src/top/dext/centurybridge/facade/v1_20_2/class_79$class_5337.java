package top.dext.centurybridge.facade.v1_20_2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

/**
 * Facade for 1.20.1 net/minecraft/class_79$class_5337 (LootPoolEntry.Serializer),
 * deleted in 1.20.2's loot codec rework; mods' entry serializers extend it. The
 * 1.20.1 bodies are ported: "conditions" read before, and written ahead of, the
 * entry's own fields. Registered through LootPoolEntryType(JsonSerializer), which
 * the converter points at CbLootJson.codec.
 */
public abstract class class_79$class_5337<T extends net.minecraft.class_79> implements class_5335<T> {

    public class_79$class_5337() {
    }

    /** toJson(json, entry, ctx) */
    public final void method_29320(JsonObject json, T entry, JsonSerializationContext context) {
        java.util.List<net.minecraft.class_5341> conditions =
            ((top.dext.centurybridge.shims.v1_20_2.LootPoolEntryAccessor) entry).cb$conditions();
        if (!conditions.isEmpty()) {
            json.add("conditions", context.serialize(conditions.toArray(new net.minecraft.class_5341[0])));
        }
        method_422(json, entry, context);
    }

    /** fromJson(json, ctx) */
    public final T method_29319(JsonObject json, JsonDeserializationContext context) {
        net.minecraft.class_5341[] conditions = net.minecraft.class_3518.method_15283(json, "conditions",
            new net.minecraft.class_5341[0], context, net.minecraft.class_5341[].class);
        return method_424(json, context, conditions);
    }

    /** addEntryFields(json, entry, ctx) */
    public abstract void method_422(JsonObject json, T entry, JsonSerializationContext context);

    /** fromJson(json, ctx, conditions) */
    public abstract T method_424(JsonObject json, JsonDeserializationContext context, net.minecraft.class_5341[] conditions);

    @Override
    public final T method_517(JsonObject json, JsonDeserializationContext context) {
        return method_29319(json, context);
    }

    @Override
    public final void method_516(JsonObject json, T value, JsonSerializationContext context) {
        method_29320(json, value, context);
    }
}
