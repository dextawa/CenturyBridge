package top.dext.centurybridge.facade.v1_21_4;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

/**
 * Facade for 1.20.1 net/minecraft/class_5335 (JsonSerializer), deleted in
 * 1.20.2's loot codec rework. Legacy mods' references are renamed here by the
 * converter (classRenames), so their serializer classes still load and their
 * mod-to-mod interop through this type keeps working. Vanilla never calls it
 * on 1.20.2 -- the registration paths that consumed it are handled separately.
 */
public interface class_5335<T> {

    void method_516(JsonObject json, T value, JsonSerializationContext context);

    T method_517(JsonObject json, JsonDeserializationContext context);
}
