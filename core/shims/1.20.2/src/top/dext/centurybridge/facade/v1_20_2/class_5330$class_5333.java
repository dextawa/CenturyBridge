package top.dext.centurybridge.facade.v1_20_2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Facade for 1.20.1 net/minecraft/class_5330$class_5333 (GsonAdapterFactory.JsonAdapter),
 * deleted in 1.20.2: the adapter the builder makes. The 1.20.1 bodies are ported line for line,
 * messages included, with one change: 1.20.1 asked each registry element for its serializer as a
 * JsonSerializableType, and the vanilla loot types it can be pointed at (a mod building an adapter
 * over Registries.LOOT_CONDITION_TYPE) are 1.20.2 records now, so the element's serializer comes
 * from CbLootSerializers.serializerOfType, which answers both a class_5336 facade and those records.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class class_5330$class_5333<E, T extends class_5336<E>> implements JsonDeserializer<E>, JsonSerializer<E> {

    // 1.20.1's private fields, under their names (a mod's access widener may reach them)
    public final net.minecraft.class_2378 field_25197;
    public final String field_25198;
    public final String field_25199;
    public final Function field_25200;
    public final class_5336 field_28445;
    public final Pair field_25201;

    public class_5330$class_5333(net.minecraft.class_2378<T> registry, String rootFieldName, String idFieldName,
            Function<E, T> typeGetter, T defaultType, Pair<T, class_5330$class_5332<? extends E>> inlineType) {
        this.field_25197 = registry;
        this.field_25198 = rootFieldName;
        this.field_25199 = idFieldName;
        this.field_25200 = typeGetter;
        this.field_28445 = defaultType;
        this.field_25201 = inlineType;
    }

    @Override
    public E deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject obj = net.minecraft.class_3518.method_15295(json, this.field_25198);
            String s = net.minecraft.class_3518.method_15253(obj, this.field_25199, "");
            Object t;
            if (s.isEmpty()) {
                t = this.field_28445;
            } else {
                net.minecraft.class_2960 id = new net.minecraft.class_2960(s);
                t = this.field_25197.method_10223(id);
            }
            if (t == null) {
                throw new JsonSyntaxException("Unknown type '" + s + "'");
            }
            return (E) top.dext.centurybridge.rt.v1_20_2.CbLootSerializers.serializerOfType(t).method_517(obj, context);
        }
        if (this.field_25201 == null) {
            throw new UnsupportedOperationException("Object " + json + " can't be deserialized");
        }
        return (E) ((class_5330$class_5332) this.field_25201.getSecond()).method_29308(json, context);
    }

    @Override
    public JsonElement serialize(E value, Type type, JsonSerializationContext context) {
        Object t = this.field_25200.apply(value);
        if (this.field_25201 != null && this.field_25201.getFirst() == t) {
            return ((class_5330$class_5332) this.field_25201.getSecond()).method_29309(value, context);
        }
        if (t == null) {
            throw new JsonSyntaxException("Unknown type: " + value);
        }
        JsonObject obj = new JsonObject();
        obj.addProperty(this.field_25199, this.field_25197.method_10221(t).toString());
        top.dext.centurybridge.rt.v1_20_2.CbLootSerializers.serializerOfType(t).method_516(obj, value, context);
        return obj;
    }
}
