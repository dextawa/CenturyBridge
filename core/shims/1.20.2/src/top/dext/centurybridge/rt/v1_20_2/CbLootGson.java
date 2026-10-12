package top.dext.centurybridge.rt.v1_20_2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.serialization.Codec;
import java.lang.reflect.Type;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * The 1.20.1 loot Gson world on 1.20.2's codecs: the type adapters 1.20.1's registries handed out
 * (LootConditionTypes / LootFunctionTypes / LootPoolEntryTypes / LootNumberProviderTypes /
 * LootNbtProviderTypes / LootScoreProviderTypes.createGsonSerializer), the three builders of
 * LootGsons (Deserializers, the class_5270 facade) and LootDataType.getGson.
 *
 * Each adapter reads and writes through the 1.20.2 dispatch codec of its type -- the codec that
 * now stands where 1.20.1's registry-keyed JsonSerializing adapter stood, keyed by the same
 * "condition" / "function" / "type" field -- after upgrading the JSON by the shared data-format
 * rules (res/LegacyLoot). A mod's own condition or function type, registered through its 1.20.1
 * JsonSerializer, is in those dispatch codecs (CbLootJson), so it is read and written by its own
 * serializer as in 1.20.1.
 */
public final class CbLootGson {

    private CbLootGson() {
    }

    /** a Gson adapter that is a codec, upgrading 1.20.1 JSON on the way in */
    public static final class CodecAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
        private final Supplier<Codec<T>> codec;
        private final UnaryOperator<JsonObject> legacy;
        private final String what;

        CodecAdapter(Supplier<Codec<T>> codec, UnaryOperator<JsonObject> legacy, String what) {
            this.codec = codec;
            this.legacy = legacy;
            this.what = what;
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return CbLootSerializers.encode(codec.get(), src, what);
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return CbLootSerializers.decode(codec.get(), CbLootSerializers.legacy(json, legacy), what);
        }
    }

    // the codecs are read lazily: nothing here touches a loot class before a loot value is (de)serialized

    /** LootConditionTypes.createGsonSerializer() */
    public static Object conditions() {
        return new CodecAdapter<net.minecraft.class_5341>(() -> net.minecraft.class_217.field_45869,
            CbLootSerializers::legacyCondition, "loot condition");
    }

    /** LootFunctionTypes.createGsonSerializer() */
    public static Object functions() {
        return new CodecAdapter<net.minecraft.class_117>(() -> net.minecraft.class_131.field_45832,
            CbLootSerializers::legacyFunction, "loot function");
    }

    /** LootPoolEntryTypes.createGsonSerializer() */
    public static Object entries() {
        return new CodecAdapter<net.minecraft.class_79>(() -> net.minecraft.class_75.field_45802,
            CbLootSerializers::legacyEntry, "loot pool entry");
    }

    /** LootNumberProviderTypes.createGsonSerializer(): a bare number is a constant, as 1.20.1's inline serializer read it */
    public static Object numberProviders() {
        return new CodecAdapter<net.minecraft.class_5658>(() -> net.minecraft.class_5659.field_45888,
            UnaryOperator.identity(), "number provider");
    }

    /** LootNbtProviderTypes.createGsonSerializer(): a bare string is a context target, as in 1.20.1 */
    public static Object nbtProviders() {
        return new CodecAdapter<net.minecraft.class_5651>(() -> net.minecraft.class_5652.field_45882,
            UnaryOperator.identity(), "nbt provider");
    }

    /** LootScoreProviderTypes.createGsonSerializer(): a bare string is a context target, as in 1.20.1 */
    public static Object scoreProviders() {
        return new CodecAdapter<net.minecraft.class_5670>(() -> net.minecraft.class_5671.field_45895,
            UnaryOperator.identity(), "score provider");
    }

    // ------------------------------------------------------------------ LootGsons (Deserializers)

    /** createConditionSerializer(): what 1.20.1 registered, in its order */
    public static GsonBuilder conditionBuilder() {
        return new GsonBuilder()
            .registerTypeAdapter(net.minecraft.class_42.class, new top.dext.centurybridge.facade.v1_20_2.class_42$class_62())
            .registerTypeHierarchyAdapter(net.minecraft.class_5658.class, numberProviders())
            .registerTypeHierarchyAdapter(net.minecraft.class_5341.class, conditions())
            .registerTypeHierarchyAdapter(net.minecraft.class_5670.class, scoreProviders())
            .registerTypeHierarchyAdapter(net.minecraft.class_47.class_50.class,
                new top.dext.centurybridge.facade.v1_20_2.class_47$class_50$class_51());
    }

    /** createFunctionSerializer() */
    public static GsonBuilder functionBuilder() {
        return conditionBuilder()
            .registerTypeHierarchyAdapter(net.minecraft.class_79.class, entries())
            .registerTypeHierarchyAdapter(net.minecraft.class_117.class, functions())
            .registerTypeHierarchyAdapter(net.minecraft.class_5651.class, nbtProviders());
    }

    /** createLootTableSerializer() */
    public static GsonBuilder tableBuilder() {
        return functionBuilder()
            .registerTypeAdapter(net.minecraft.class_55.class, new top.dext.centurybridge.facade.v1_20_2.class_55$class_57())
            .registerTypeAdapter(net.minecraft.class_52.class, new top.dext.centurybridge.facade.v1_20_2.class_52$class_54());
    }

    private static volatile Gson predicates;
    private static volatile Gson modifiers;
    private static volatile Gson tables;

    /**
     * LootDataType.getGson(): 1.20.1 built each type's Gson once, from the LootGsons builder of its
     * kind (PREDICATE: conditions, MODIFIER: functions, TABLE: tables).
     */
    public static Gson method_51203(net.minecraft.class_8490<?> type) {
        if (type == net.minecraft.class_8490.field_44496) {
            Gson g = predicates;
            if (g == null) {
                predicates = g = conditionBuilder().create();
            }
            return g;
        }
        if (type == net.minecraft.class_8490.field_44497) {
            Gson g = modifiers;
            if (g == null) {
                modifiers = g = functionBuilder().create();
            }
            return g;
        }
        if (type == net.minecraft.class_8490.field_44498) {
            Gson g = tables;
            if (g == null) {
                tables = g = tableBuilder().create();
            }
            return g;
        }
        throw new IllegalArgumentException("CenturyBridge: unknown loot data type " + type);
    }
}
