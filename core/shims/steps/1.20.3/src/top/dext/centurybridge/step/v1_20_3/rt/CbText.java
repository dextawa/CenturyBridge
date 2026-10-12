package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

/**
 * 1.20.2 chat-component API on 1.20.3. 1.20.3 moved Component, Style and their parts from GSON
 * serializers to codecs; what 1.20.2 exposed around the GSON form is rebuilt here on 1.20.3's
 * Component.Serializer (still JSON in and out, now backed by the codecs).
 */
public final class CbText {

    /**
     * was TextColor.parseColor(String) (method_27719), "a TextColor or null" in 1.20.2 and a DataResult
     * since 1.20.3. 1.20.2: "#rrggbb" parsed as a hexadecimal int with no range check (1.20.3 rejects
     * values outside 0..0xFFFFFF), a bad number null; anything else looked up among the named colors.
     */
    public static net.minecraft.class_5251 method_27719(String color) {
        if (color.startsWith("#")) {
            try {
                return net.minecraft.class_5251.method_27717(Integer.parseInt(color.substring(1), 16));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        // named colors: 1.20.3 answers the same name table (success) or an error for an unknown name
        return net.minecraft.class_5251.method_27719(color).result().orElse(null);
    }

    private static final class Codecs {
        /**
         * ExtraCodecs.COMPONENT as 1.20.2 built it: adaptJsonSerializer(Serializer::fromJson,
         * Serializer::toJsonTree) -- through the JSON form, whatever the ops; a parse failure an error,
         * and an encode failure too (IllegalArgumentException from 1.20.2's serializer, JsonParseException
         * from 1.20.3's).
         */
        static final Codec<net.minecraft.class_2561> COMPONENT = net.minecraft.class_5699.field_40721.flatXmap(json -> {
            try {
                return DataResult.success((net.minecraft.class_2561) net.minecraft.class_2561.class_2562.method_10872(json));
            } catch (JsonParseException e) {
                return DataResult.error(e::getMessage);
            }
        }, component -> {
            try {
                return DataResult.success(net.minecraft.class_2561.class_2562.method_10868(component));
            } catch (IllegalArgumentException | JsonParseException e) {
                return DataResult.error(e::getMessage);
            }
        });

        /**
         * ExtraCodecs.FLAT_COMPONENT as 1.20.2 built it: a component in its JSON string form. 1.20.3 reused
         * the field (same name, same erased type) for FLAT_JSON, a Codec of JsonElement, so a 1.20.2 mod
         * reading it would get JSON trees where it expects components. A parse failure is an error, as it
         * was; an encode failure is too (1.20.2's serializer threw IllegalArgumentException for it, 1.20.3's
         * throws JsonParseException).
         */
        static final Codec<net.minecraft.class_2561> FLAT_COMPONENT = Codec.STRING.flatXmap(json -> {
            try {
                return DataResult.success((net.minecraft.class_2561) net.minecraft.class_2561.class_2562.method_10877(json));
            } catch (JsonParseException e) {
                return DataResult.error(e::getMessage);
            }
        }, component -> {
            try {
                return DataResult.success(net.minecraft.class_2561.class_2562.method_10867(component));
            } catch (IllegalArgumentException | JsonParseException e) {
                return DataResult.error(e::getMessage);
            }
        });
    }

    /** was ExtraCodecs.COMPONENT (field_40722), gone in 1.20.3 */
    public static Codec<net.minecraft.class_2561> field_40722() {
        return Codecs.COMPONENT;
    }

    /** was ExtraCodecs.FLAT_COMPONENT (field_43173); see {@link Codecs#FLAT_COMPONENT} */
    public static Codec<net.minecraft.class_2561> field_43173() {
        return Codecs.FLAT_COMPONENT;
    }

    private CbText() {
    }
}
