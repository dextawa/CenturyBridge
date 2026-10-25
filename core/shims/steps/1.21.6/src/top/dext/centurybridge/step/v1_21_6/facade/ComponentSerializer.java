package top.dext.centurybridge.step.v1_21_6.facade;

/**
 * 1.21.5 Component.Serializer (net/minecraft/class_2561$class_2562), which 1.21.6 removed: JSON text components
 * through ComponentSerialization.CODEC on the lookup's RegistryOps over JsonOps, as 1.21.5 did, with 1.21.5's Gson
 * (HTML escaping off), its null answers for a JSON null, its lenient reader and its JsonParseException on a decoding
 * error. The intermediary method names are kept: that is what 1.21.5 bytecode calls.
 */
public class ComponentSerializer {

    /** 1.21.5's Gson (field_11754) */
    public static final com.google.gson.Gson field_11754 = new com.google.gson.GsonBuilder().disableHtmlEscaping().create();

    public ComponentSerializer() {
    }

    public static net.minecraft.class_5250 method_10871(com.google.gson.JsonElement json, net.minecraft.class_7225.class_7874 lookup) {
        return (net.minecraft.class_5250) net.minecraft.class_8824.field_46597
            .parse(lookup.method_57093(com.mojang.serialization.JsonOps.INSTANCE), json)
            .getOrThrow(com.google.gson.JsonParseException::new);
    }

    public static com.google.gson.JsonElement method_10874(net.minecraft.class_2561 text, net.minecraft.class_7225.class_7874 lookup) {
        return net.minecraft.class_8824.field_46597
            .encodeStart(lookup.method_57093(com.mojang.serialization.JsonOps.INSTANCE), text)
            .getOrThrow(com.google.gson.JsonParseException::new);
    }

    /** toJson(Component, Provider) */
    public static String method_10867(net.minecraft.class_2561 text, net.minecraft.class_7225.class_7874 lookup) {
        return field_11754.toJson(method_10874(text, lookup));
    }

    /** fromJson(String, Provider) */
    public static net.minecraft.class_5250 method_10877(String json, net.minecraft.class_7225.class_7874 lookup) {
        com.google.gson.JsonElement e = com.google.gson.JsonParser.parseString(json);
        return e == null ? null : method_10871(e, lookup);
    }

    /** fromJson(JsonElement, Provider) */
    public static net.minecraft.class_5250 method_10872(com.google.gson.JsonElement json, net.minecraft.class_7225.class_7874 lookup) {
        return json == null ? null : method_10871(json, lookup);
    }

    /** fromJsonLenient(String, Provider) */
    public static net.minecraft.class_5250 method_10873(String json, net.minecraft.class_7225.class_7874 lookup) {
        com.google.gson.stream.JsonReader reader = new com.google.gson.stream.JsonReader(new java.io.StringReader(json));
        reader.setLenient(true);
        com.google.gson.JsonElement e = com.google.gson.JsonParser.parseReader(reader);
        return e == null ? null : method_10871(e, lookup);
    }
}
