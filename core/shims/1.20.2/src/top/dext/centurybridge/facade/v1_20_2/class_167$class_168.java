package top.dext.centurybridge.facade.v1_20_2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.JsonOps;
import java.lang.reflect.Type;

/**
 * Facade for 1.20.1 net/minecraft/class_167$class_168 (AdvancementProgress.Serializer): the Gson adapter
 * 1.20.1 wrote player advancement files with -- {"criteria": {name: "yyyy-MM-dd HH:mm:ss Z"}, "done": bool},
 * only obtained criteria listed, "criteria" left out when none is. 1.20.2 dropped the class and writes the
 * same object through AdvancementProgress.CODEC (same pattern, Locale.ROOT); the adapter goes through it.
 * Every 1.20.1 reference is renamed here, and a mod subclass extends it.
 */
public class class_167$class_168 implements com.google.gson.JsonSerializer<net.minecraft.class_167>,
        com.google.gson.JsonDeserializer<net.minecraft.class_167> {

    public class_167$class_168() {
    }

    /** serialize(progress, type, context) */
    public JsonElement method_744(net.minecraft.class_167 progress, Type type, JsonSerializationContext context) {
        return net.minecraft.class_167.field_46080.encodeStart(JsonOps.INSTANCE, progress).result()
            .orElseThrow(() -> new IllegalStateException("CenturyBridge: advancement progress does not encode: " + progress));
    }

    /** deserialize(json, type, context) */
    public net.minecraft.class_167 method_745(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return net.minecraft.class_167.field_46080.parse(JsonOps.INSTANCE, json).result()
            .orElseThrow(() -> new JsonParseException("Invalid advancement progress: " + json));
    }

    @Override
    public JsonElement serialize(net.minecraft.class_167 progress, Type type, JsonSerializationContext context) {
        return method_744(progress, type, context);
    }

    @Override
    public net.minecraft.class_167 deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return method_745(json, type, context);
    }
}
