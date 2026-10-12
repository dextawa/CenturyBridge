package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import top.dext.centurybridge.step.v1_20_3.mixin.CbStyleAccess;

/**
 * 1.20.2 chat-component API on 1.20.3. 1.20.3 moved Component, Style and their parts from GSON
 * serializers to codecs; what 1.20.2 exposed around the GSON form is rebuilt here on 1.20.3's
 * Component.Serializer (still JSON in and out, now backed by the codecs), its public codecs and
 * the parts that did not change. Where a 1.20.2 body only used API that 1.20.3 still has, it is
 * ported as it was.
 */
public final class CbText {

    // ------------------------------------------------------------------ TextColor (class_5251)

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

    // ------------------------------------------------------------------ codecs 1.20.2 exposed

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

        /**
         * Style.FORMATTING_CODEC as 1.20.2 built it: the formatting fields only (color, bold, italic,
         * underlined, strikethrough, obfuscated, insertion, font -- no click or hover event), each optional,
         * read from and written to the style's raw fields; gone in 1.20.3, whose style codec carries the
         * events too.
         */
        static final Codec<net.minecraft.class_2583> FORMATTING = RecordCodecBuilder.create(i -> i.group(
            net.minecraft.class_5251.field_39242.optionalFieldOf("color").forGetter(s -> Optional.ofNullable(access(s).cb$color())),
            Codec.BOOL.optionalFieldOf("bold").forGetter(s -> Optional.ofNullable(access(s).cb$bold())),
            Codec.BOOL.optionalFieldOf("italic").forGetter(s -> Optional.ofNullable(access(s).cb$italic())),
            Codec.BOOL.optionalFieldOf("underlined").forGetter(s -> Optional.ofNullable(access(s).cb$underlined())),
            Codec.BOOL.optionalFieldOf("strikethrough").forGetter(s -> Optional.ofNullable(access(s).cb$strikethrough())),
            Codec.BOOL.optionalFieldOf("obfuscated").forGetter(s -> Optional.ofNullable(access(s).cb$obfuscated())),
            Codec.STRING.optionalFieldOf("insertion").forGetter(s -> Optional.ofNullable(access(s).cb$insertion())),
            net.minecraft.class_2960.field_25139.optionalFieldOf("font").forGetter(s -> Optional.ofNullable(access(s).cb$font()))
        ).apply(i, (color, bold, italic, underlined, strikethrough, obfuscated, insertion, font) -> CbStyleAccess.cb$new(
            color.orElse(null), bold.orElse(null), italic.orElse(null), underlined.orElse(null), strikethrough.orElse(null),
            obfuscated.orElse(null), null, null, insertion.orElse(null), font.orElse(null))));
    }

    static CbStyleAccess access(net.minecraft.class_2583 style) {
        return (CbStyleAccess) (Object) style;
    }

    /** was ExtraCodecs.COMPONENT (field_40722), gone in 1.20.3 */
    public static Codec<net.minecraft.class_2561> field_40722() {
        return Codecs.COMPONENT;
    }

    /** was ExtraCodecs.FLAT_COMPONENT (field_43173); see {@link Codecs#FLAT_COMPONENT} */
    public static Codec<net.minecraft.class_2561> field_43173() {
        return Codecs.FLAT_COMPONENT;
    }

    /** was Style.FORMATTING_CODEC (field_39241); see {@link Codecs#FORMATTING} */
    public static Codec<net.minecraft.class_2583> field_39241() {
        return Codecs.FORMATTING;
    }

    // ------------------------------------------------------------------ contents

    /** was ComponentContents.EMPTY (field_39004): the empty contents is PlainTextContents.EMPTY since 1.20.3 */
    public static net.minecraft.class_7417 field_39004() {
        return net.minecraft.class_8828.field_46625;
    }

    /**
     * new LiteralContents(String): 1.20.2's LiteralContents record is PlainTextContents.LiteralContents
     * since 1.20.3, and vanilla gives the empty text as PlainTextContents.EMPTY instead. A 1.20.2 mod's
     * LiteralContents is therefore read as PlainTextContents (instanceof, casts, text()), and a new one is
     * made the way 1.20.3 makes plain text (PlainTextContents.create): its text, the empty one included.
     */
    public static net.minecraft.class_8828 cbNewLiteral(String text) {
        return net.minecraft.class_8828.method_54232(text);
    }

    /** "Don't know how to serialize X as a Component": what 1.20.2 threw for contents it had no format for */
    private static String unknown(Object contents) {
        return "Don't know how to serialize " + contents + " as a Component";
    }

    /** a MapCodec for a 1.20.2 mod's own contents or data source: 1.20.2 had no format for them */
    private static <T> MapCodec<T> unserializable() {
        return new MapCodec<T>() {
            @Override
            public <O> Stream<O> keys(DynamicOps<O> ops) {
                return Stream.empty();
            }

            @Override
            public <O> DataResult<T> decode(DynamicOps<O> ops, MapLike<O> input) {
                return DataResult.error(() -> "No 1.20.2 format to read mod component contents from");
            }

            @Override
            public <O> RecordBuilder<O> encode(T input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
                return prefix.withErrorsFrom(DataResult.error(() -> unknown(input)));
            }
        };
    }

    private static final net.minecraft.class_7417.class_8823<net.minecraft.class_7417> LEGACY_CONTENTS =
        new net.minecraft.class_7417.class_8823<>(unserializable(), "centurybridge:legacy_contents");

    private static final net.minecraft.class_7419.class_8827<net.minecraft.class_7419> LEGACY_DATA_SOURCE =
        new net.minecraft.class_7419.class_8827<>(unserializable(), "centurybridge:legacy_data_source");

    /**
     * ComponentContents.type() (method_54163), abstract since 1.20.3, for a 1.20.2 mod's contents class
     * (synthesized into it, guarded). The class's own vanilla superclass answers when it has a type (a
     * mod subclass of TranslatableContents serializes as before); otherwise there is none, as in 1.20.2:
     * its serializer did not know the class, and encoding it fails with 1.20.2's message.
     */
    public static net.minecraft.class_7417.class_8823<?> cbContentsType(net.minecraft.class_7417 contents) {
        net.minecraft.class_7417.class_8823<?> inherited = contents.method_54163(); // re-entry: the superclass's answer
        return inherited != null ? inherited : LEGACY_CONTENTS;
    }

    /** DataSource.type() (method_54225), abstract since 1.20.3: as {@link #cbContentsType} for an NBT data source */
    public static net.minecraft.class_7419.class_8827<?> cbDataSourceType(net.minecraft.class_7419 source) {
        net.minecraft.class_7419.class_8827<?> inherited = source.method_54225();
        return inherited != null ? inherited : LEGACY_DATA_SOURCE;
    }

    // ------------------------------------------------------------------ ClickEvent.Action (class_2558$class_2559)

    /** was ClickEvent.Action.getName() (method_10846): the same name, as getSerializedName() since 1.20.3 */
    public static String method_10846(net.minecraft.class_2558.class_2559 action) {
        return action.method_15434();
    }

    /** was ClickEvent.Action.getByName(String) (method_10848): the action of that name, or null */
    public static net.minecraft.class_2558.class_2559 method_10848(String name) {
        for (net.minecraft.class_2558.class_2559 a : net.minecraft.class_2558.class_2559.values()) {
            if (a.method_15434().equals(name)) {
                return a;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ HoverEvent (class_2568)

    private static final net.minecraft.class_2568.class_5247<?>[] HOVER_ACTIONS = {
        net.minecraft.class_2568.class_5247.field_24342, net.minecraft.class_2568.class_5247.field_24343,
        net.minecraft.class_2568.class_5247.field_24344};

    /** was HoverEvent.Action.getByName(String) (method_27670): one of the three vanilla actions, or null */
    public static net.minecraft.class_2568.class_5247<?> method_27670(String name) {
        for (net.minecraft.class_2568.class_5247<?> a : HOVER_ACTIONS) {
            if (a.method_15434().equals(name)) {
                return a;
            }
        }
        return null;
    }

    /**
     * was HoverEvent.Action.getName() (method_27674): getSerializedName() since 1.20.3. Virtual: a mod's
     * own action class overriding the old name answers through the override hook that reconnects it.
     */
    public static String method_27674(net.minecraft.class_2568.class_5247<?> action) {
        return action.method_15434();
    }

    /**
     * was HoverEvent.deserialize(JsonObject) (method_27664), 1.20.2's body: the action by name (null when
     * missing or unknown), then "contents" through the action, else the legacy "value" text.
     */
    public static net.minecraft.class_2568 method_27664(JsonObject json) {
        String name = net.minecraft.class_3518.method_15253(json, "action", null);
        if (name == null) {
            return null;
        }
        net.minecraft.class_2568.class_5247<?> action = method_27670(name);
        if (action == null) {
            return null;
        }
        JsonElement contents = json.get("contents");
        if (contents != null) {
            return ((CbHoverAction) action).method_27668(contents);
        }
        net.minecraft.class_2561 value = net.minecraft.class_2561.class_2562.method_10872(json.get("value"));
        return value != null ? ((CbHoverAction) action).method_27671(value) : null;
    }

    /**
     * HoverEvent.Action methods 1.20.2 had and 1.20.3 dropped, given back to the class by a mixin under
     * their 1.20.2 names; declared here so a call reaches whatever the action object answers (a mod's
     * own action class may override them, as it could in 1.20.2).
     */
    public interface CbHoverAction {
        /** deserialize(JsonElement): the event for these "contents" */
        net.minecraft.class_2568 method_27668(JsonElement contents);

        /** deserializeFromLegacy(Component): the event for this legacy "value" text, null when unreadable */
        net.minecraft.class_2568 method_27671(net.minecraft.class_2561 text);

        /** serializeArg(Object): the JSON "contents" for the value */
        JsonElement method_27669(Object value);
    }

    /** HoverEvent.serialize() under its 1.20.2 name, given back by a mixin (see {@link CbHoverAction}) */
    public interface CbHoverEvent {
        JsonObject method_27665();
    }

    /**
     * The HoverEvent a 1.20.2 Action.deserialize / deserializeFromLegacy returned, from what the action's
     * own 1.20.3 codec decoded (a TypedHoverEvent): its value, paired with the action again. A decode
     * failure throws when {@code strict} (malformed contents threw in 1.20.2) and is null otherwise (an
     * unreadable legacy value was null).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_2568 cbHoverFromTyped(net.minecraft.class_2568.class_5247<?> action,
                                                            DataResult<?> decoded, boolean strict) {
        Optional<?> typed = decoded.result();
        if (typed.isEmpty()) {
            if (strict) {
                throw new JsonParseException(decoded.error().map(DataResult.PartialResult::message)
                    .orElse("Invalid hover event contents for " + action.method_15434()));
            }
            return null;
        }
        Object value = ((top.dext.centurybridge.step.v1_20_3.mixin.CbTypedHoverEventAccess) typed.get()).cb$value();
        return new net.minecraft.class_2568((net.minecraft.class_2568.class_5247) action, value);
    }

    /**
     * was HoverEvent.serialize() (method_27665): {"action": name, "contents": ...} -- what 1.20.3's hover
     * codec writes for the event.
     */
    public static JsonObject cbSerializeHover(net.minecraft.class_2568 event) {
        JsonElement json = net.minecraft.class_2568.field_46601.encodeStart(JsonOps.INSTANCE, event)
            .getOrThrow(false, msg -> { });
        return json.getAsJsonObject();
    }

    /**
     * was HoverEvent.Action.serializeArg(Object) (method_27669): the value's JSON form, the "contents" the
     * action's own codec writes for it.
     */
    public static JsonElement cbSerializeHoverArg(net.minecraft.class_2568.class_5247<?> action, Object value) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        net.minecraft.class_2568 event = new net.minecraft.class_2568((net.minecraft.class_2568.class_5247) action, value);
        return cbSerializeHover(event).get("contents");
    }

    /**
     * The value codec a 1.20.2 mod's own hover action passes: 1.20.2 took two JSON functions
     * (argDeserializer / argSerializer), 1.20.3 takes a codec. Through JSON, as the functions worked;
     * 1.20.2's deserialize gave no event when the function answered null, which is an error here.
     */
    public static <T> Codec<T> cbHoverValueCodec(Function<JsonElement, T> fromJson, Function<T, JsonElement> toJson) {
        return net.minecraft.class_5699.field_40721.flatXmap(json -> {
            try {
                T value = fromJson.apply(json);
                return value != null ? DataResult.success(value) : DataResult.error(() -> "No value read from " + json);
            } catch (RuntimeException e) {
                return DataResult.error(e::getMessage);
            }
        }, value -> {
            try {
                return DataResult.success(toJson.apply(value));
            } catch (RuntimeException e) {
                return DataResult.error(e::getMessage);
            }
        });
    }

    /** the legacy-text reader of a 1.20.2 mod's hover action: null was "no event", an error in 1.20.3's form */
    public static <T> Function<net.minecraft.class_2561, DataResult<T>> cbHoverLegacy(Function<net.minecraft.class_2561, T> legacy) {
        return text -> {
            T value = legacy.apply(text);
            return value != null ? DataResult.success(value) : DataResult.error(() -> "No value read from " + text);
        };
    }

    // ------------------------------------------------------------------ HoverEvent.EntityTooltipInfo (class_2568$class_5248)

    /** was EntityTooltipInfo.name (field_24353): the optional name of 1.20.3, null when absent as in 1.20.2 */
    public static net.minecraft.class_2561 field_24353(net.minecraft.class_2568.class_5248 info) {
        return info.field_24353.orElse(null);
    }

    /** was EntityTooltipInfo.create(JsonElement) (method_27680), 1.20.2's body */
    public static net.minecraft.class_2568.class_5248 method_27680(JsonElement element) {
        if (!element.isJsonObject()) {
            return null;
        }
        JsonObject json = element.getAsJsonObject();
        net.minecraft.class_1299<?> type = net.minecraft.class_7923.field_41177.method_10223(
            new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, "type")));
        UUID id = UUID.fromString(net.minecraft.class_3518.method_15265(json, "id"));
        net.minecraft.class_2561 name = net.minecraft.class_2561.class_2562.method_10872(json.get("name"));
        return new net.minecraft.class_2568.class_5248(type, id, name);
    }

    /** was EntityTooltipInfo.create(Component) (method_27681), 1.20.2's body: the legacy SNBT form, null when unreadable */
    public static net.minecraft.class_2568.class_5248 method_27681(net.minecraft.class_2561 text) {
        try {
            net.minecraft.class_2487 tag = net.minecraft.class_2522.method_10718(text.getString());
            net.minecraft.class_2561 name = net.minecraft.class_2561.class_2562.method_10877(tag.method_10558("name"));
            net.minecraft.class_1299<?> type = net.minecraft.class_7923.field_41177.method_10223(
                new net.minecraft.class_2960(tag.method_10558("type")));
            UUID id = UUID.fromString(tag.method_10558("id"));
            return new net.minecraft.class_2568.class_5248(type, id, name);
        } catch (Exception e) {
            return null;
        }
    }

    /** was EntityTooltipInfo.serialize() (method_27679), 1.20.2's body */
    public static JsonElement cbSerializeEntityTooltip(net.minecraft.class_2568.class_5248 info) {
        JsonObject json = new JsonObject();
        json.addProperty("type", net.minecraft.class_7923.field_41177.method_10221(info.field_24351).toString());
        json.addProperty("id", info.field_24352.toString());
        info.field_24353.ifPresent(name -> json.add("name", net.minecraft.class_2561.class_2562.method_10868(name)));
        return json;
    }

    // ------------------------------------------------------------------ DataSource (class_7419) lambdas

    /** a 1.20.2 DataSource lambda: getData(source) only (1.20.3 added type(), so a lambda no longer fits) */
    @FunctionalInterface
    public interface CbLegacyDataSource {
        java.util.stream.Stream<net.minecraft.class_2487> method_10916(net.minecraft.class_2168 source)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException;
    }

    /** the lambda as a 1.20.3 DataSource of the legacy type, as a 1.20.2 data source class gets it (adapterHooks) */
    public static net.minecraft.class_7419 cbDataSource(CbLegacyDataSource data) {
        return new net.minecraft.class_7419() {
            @Override
            public java.util.stream.Stream<net.minecraft.class_2487> method_10916(net.minecraft.class_2168 source)
                    throws com.mojang.brigadier.exceptions.CommandSyntaxException {
                return data.method_10916(source);
            }

            @Override
            public net.minecraft.class_7419.class_8827<?> method_54225() {
                return LEGACY_DATA_SOURCE;
            }
        };
    }

    private CbText() {
    }
}
