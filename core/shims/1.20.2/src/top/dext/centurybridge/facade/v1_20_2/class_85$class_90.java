package top.dext.centurybridge.facade.v1_20_2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

/**
 * Facade for 1.20.1 net/minecraft/class_85$class_90 (LeafEntry.Serializer), deleted
 * in 1.20.2's loot codec rework. The 1.20.1 surface is mirrored member for member --
 * subclasses call super.addEntryFields (method_442) and their bridges call fromJson
 * (method_441) -- and the 1.20.1 bodies are ported: weight (default 1), quality
 * (default 0) and "functions" around the subclass's own reader.
 */
public abstract class class_85$class_90<T extends net.minecraft.class_85> extends class_79$class_5337<T> {

    public class_85$class_90() {
    }

    /** addEntryFields(json, entry, ctx) */
    public void method_442(JsonObject json, T entry, JsonSerializationContext context) {
        top.dext.centurybridge.shims.v1_20_2.LeafEntryAccessor leaf = (top.dext.centurybridge.shims.v1_20_2.LeafEntryAccessor) entry;
        if (leaf.cb$weight() != 1) {
            json.addProperty("weight", leaf.cb$weight());
        }
        if (leaf.cb$quality() != 0) {
            json.addProperty("quality", leaf.cb$quality());
        }
        if (!leaf.cb$functions().isEmpty()) {
            json.add("functions", context.serialize(leaf.cb$functions().toArray(new net.minecraft.class_117[0])));
        }
    }

    /** fromJson(json, ctx, conditions) */
    public final T method_441(JsonObject json, JsonDeserializationContext context, net.minecraft.class_5341[] conditions) {
        int weight = net.minecraft.class_3518.method_15282(json, "weight", 1);
        int quality = net.minecraft.class_3518.method_15282(json, "quality", 0);
        net.minecraft.class_117[] functions = net.minecraft.class_3518.method_15283(json, "functions",
            new net.minecraft.class_117[0], context, net.minecraft.class_117[].class);
        return method_443(json, context, weight, quality, conditions, functions);
    }

    /** fromJson(json, ctx, weight, quality, conditions, functions) */
    public abstract T method_443(JsonObject json, JsonDeserializationContext context, int weight, int quality,
            net.minecraft.class_5341[] conditions, net.minecraft.class_117[] functions);

    /** LootPoolEntry.Serializer.addEntryFields -> this class's shape, as in 1.20.1 */
    @Override
    public void method_422(JsonObject json, T entry, JsonSerializationContext context) {
        method_442(json, entry, context);
    }

    /** LootPoolEntry.Serializer.fromJson(json, ctx, conditions) -> this class's shape, as in 1.20.1 */
    @Override
    public T method_424(JsonObject json, JsonDeserializationContext context, net.minecraft.class_5341[] conditions) {
        return method_441(json, context, conditions);
    }
}
