package top.dext.centurybridge.facade.v1_20_2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

/**
 * Facade for 1.20.1 net/minecraft/class_5330$class_5332 (GsonAdapterFactory.InlineSerializer),
 * deleted in 1.20.2: the bare-value form a dispatching adapter falls back to (a number for a
 * constant number provider, a string for a context NBT / score provider).
 */
public interface class_5330$class_5332<T> {

    /** serialize(value, ctx) */
    JsonElement method_29309(T value, JsonSerializationContext context);

    /** deserialize(json, ctx) */
    T method_29308(JsonElement json, JsonDeserializationContext context);
}
