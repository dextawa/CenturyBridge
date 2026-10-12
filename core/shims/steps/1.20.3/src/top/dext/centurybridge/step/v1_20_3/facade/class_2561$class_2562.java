package top.dext.centurybridge.step.v1_20_3.facade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

/**
 * Facade for 1.20.2's net/minecraft/class_2561$class_2562 (Component.Serializer): the GSON type adapter
 * mods registered (new Component.Serializer() in a GsonBuilder) and the static JSON helpers. In 1.20.3
 * the class survives only as a few static helpers over the component codec -- private constructor,
 * no JsonSerializer/JsonDeserializer -- so every reference a 1.20.2 mod has to it lands here.
 *
 * The JSON format of a component is the game's own: reading and writing go through 1.20.3's
 * Component.Serializer (the codec), as the game itself reads and writes text; the adapter shape, the
 * statics 1.20.3 dropped and the 1.20.2 helpers are this class's.
 */
public class class_2561$class_2562 implements JsonDeserializer<net.minecraft.class_5250>, JsonSerializer<net.minecraft.class_2561> {

    /** 1.20.2's GSON: HTML escaping off, the component and style adapters, lower-case enums */
    public static final Gson field_11754 = method_10878();

    /** JsonReader.pos, read to tell how far a JSON value reached in a brigadier reader */
    public static final Field field_11753 = method_10869();

    /** JsonReader.lineStart */
    public static final Field field_11752 = method_10876();

    public class_2561$class_2562() {
    }

    /** deserialize(JsonElement, Type, JsonDeserializationContext): the component in that JSON */
    public net.minecraft.class_5250 method_10871(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return net.minecraft.class_2561.class_2562.method_10872(json);
    }

    @Override
    public net.minecraft.class_5250 deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return method_10871(json, type, context);
    }

    /** a translation argument given as plain text with no style or siblings: its string, else itself */
    public static Object method_43474(Object argument) {
        if (argument instanceof net.minecraft.class_2561 component && component.method_10866().method_10967()
                && component.method_10855().isEmpty()
                && component.method_10851() instanceof net.minecraft.class_8828 plain) {
            return plain.comp_737();
        }
        return argument;
    }

    /** the "separator" of a selector/nbt component, if the JSON has one */
    public Optional<net.minecraft.class_2561> method_36329(Type type, JsonDeserializationContext context, JsonObject json) {
        return json.has("separator") ? Optional.of(method_10871(json.get("separator"), type, context)) : Optional.empty();
    }

    /** the style's JSON fields, merged into the component's JSON object */
    public void method_10875(net.minecraft.class_2583 style, JsonObject json, JsonSerializationContext context) {
        JsonElement e = context.serialize(style);
        if (e.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : ((JsonObject) e).entrySet()) {
                json.add(entry.getKey(), entry.getValue());
            }
        }
    }

    /** serialize(Component, Type, JsonSerializationContext): the component's JSON */
    public JsonElement method_10874(net.minecraft.class_2561 component, Type type, JsonSerializationContext context) {
        return net.minecraft.class_2561.class_2562.method_10868(component);
    }

    @Override
    public JsonElement serialize(net.minecraft.class_2561 component, Type type, JsonSerializationContext context) {
        return method_10874(component, type, context);
    }

    /** the separator, when present, written as "separator" */
    public void method_36328(JsonSerializationContext context, JsonObject json, Optional<net.minecraft.class_2561> separator) {
        separator.ifPresent(s -> method_36327(json, context, s));
    }

    public void method_36327(JsonObject json, JsonSerializationContext context, net.minecraft.class_2561 separator) {
        json.add("separator", method_10874(separator, separator.getClass(), context));
    }

    /** toJson(Component) */
    public static String method_10867(net.minecraft.class_2561 component) {
        return net.minecraft.class_2561.class_2562.method_10867(component);
    }

    /** toStableJson(Component): the JSON with its keys sorted */
    public static String method_43858(net.minecraft.class_2561 component) {
        return net.minecraft.class_3518.method_43680(method_10868(component));
    }

    /** toJsonTree(Component) */
    public static JsonElement method_10868(net.minecraft.class_2561 component) {
        return net.minecraft.class_2561.class_2562.method_10868(component);
    }

    /** fromJson(String) */
    public static net.minecraft.class_5250 method_10877(String json) {
        return net.minecraft.class_2561.class_2562.method_10877(json);
    }

    /** fromJson(JsonElement) */
    public static net.minecraft.class_5250 method_10872(JsonElement json) {
        return net.minecraft.class_2561.class_2562.method_10872(json);
    }

    /** fromJsonLenient(String) */
    public static net.minecraft.class_5250 method_10873(String json) {
        return net.minecraft.class_2561.class_2562.method_10873(json);
    }

    /**
     * fromJson(com.mojang.brigadier.StringReader), 1.20.2's body: one strict JSON value read from the
     * reader's remaining input through this class's GSON (JSON null reads as no component), the reader
     * moved past it on success; an I/O problem or a stack overflow is a JsonParseException.
     */
    public static net.minecraft.class_5250 method_10879(com.mojang.brigadier.StringReader reader) {
        try {
            JsonReader json = new JsonReader(new StringReader(reader.getRemaining()));
            json.setLenient(false);
            net.minecraft.class_5250 component = field_11754.getAdapter(net.minecraft.class_5250.class).read(json);
            reader.setCursor(reader.getCursor() + method_10880(json));
            return component;
        } catch (StackOverflowError | IOException e) {
            throw new JsonParseException(e);
        }
    }

    /** how many characters the JsonReader consumed (its column on a one-line input) */
    public static int method_10880(JsonReader reader) {
        try {
            return field_11753.getInt(reader) - field_11752.getInt(reader) + 1;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Couldn't read position of JsonReader", e);
        }
    }

    public static Field method_10876() {
        try {
            new JsonReader(new StringReader(""));
            Field f = JsonReader.class.getDeclaredField("lineStart");
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", e);
        }
    }

    public static Field method_10869() {
        try {
            new JsonReader(new StringReader(""));
            Field f = JsonReader.class.getDeclaredField("pos");
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", e);
        }
    }

    public static Gson method_10878() {
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        builder.registerTypeHierarchyAdapter(net.minecraft.class_2561.class, new class_2561$class_2562());
        builder.registerTypeHierarchyAdapter(net.minecraft.class_2583.class, new class_2583$class_2584());
        builder.registerTypeAdapterFactory(new net.minecraft.class_3530());
        return builder.create();
    }
}
