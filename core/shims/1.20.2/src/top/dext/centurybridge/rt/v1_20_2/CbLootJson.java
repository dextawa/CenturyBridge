package top.dext.centurybridge.rt.v1_20_2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.lang.reflect.Type;
import java.util.stream.Stream;

/**
 * 1.20.2 replaced the loot-table JsonSerializer world (class_5335 -- our
 * facade keeps the mods' serializer classes loadable) with codecs: a
 * LootConditionType / LootFunctionType / LootPoolEntryType is now built from
 * a Codec. A legacy serializer is a codec in disguise: encode = toJson into a
 * fresh object, decode = fromJson of the object the dispatcher hands us (the
 * "condition"/"function"/"type" key is still present, exactly as in 1.20.1).
 *
 * The (de)serialization contexts the old bodies receive are backed by a Gson
 * whose adapters for the nested loot types (conditions, functions, entries,
 * number providers, the predicate family) delegate to the surviving 1.20.2
 * codecs, so JsonHelper.deserialize(json, "conditions", ctx, LootCondition[].class)
 * inside a mod serializer keeps working. The loot types' adapters are CbLootGson's,
 * which upgrade a nested value's 1.20.1 JSON by the shared data-format rules first.
 * Everything is lazy: nothing here touches a game class until a loot table is
 * actually parsed.
 */
public final class CbLootJson {

    private static volatile Gson GSON;

    private CbLootJson() {
    }

    private static Gson gson() {
        Gson g = GSON;
        if (g == null) {
            synchronized (CbLootJson.class) {
                if (GSON == null) {
                    GSON = build();
                }
                g = GSON;
            }
        }
        return g;
    }

    private static Gson build() {
        // what 1.20.1's LootGsons.createLootTableSerializer() registered (CbLootGson ports it) ...
        GsonBuilder b = CbLootGson.tableBuilder().disableHtmlEscaping();
        // ... and the predicates a mod serializer may ask its context for (1.20.1 parsed these
        // with their own fromJson; the context route is harmless and kept for mods that used it)
        hier(b, net.minecraft.class_2073.class, net.minecraft.class_2073.field_45754);     // ItemPredicate
        hier(b, net.minecraft.class_2048.class, net.minecraft.class_2048.field_45746);     // EntityPredicate
        hier(b, net.minecraft.class_2090.class, net.minecraft.class_2090.field_45760);     // LocationPredicate
        hier(b, net.minecraft.class_2105.class, net.minecraft.class_2105.field_45768);     // NbtPredicate
        hier(b, net.minecraft.class_2022.class, net.minecraft.class_2022.field_45727);     // DamageSourcePredicate
        hier(b, net.minecraft.class_2025.class, net.minecraft.class_2025.field_45728);     // DistancePredicate
        hier(b, net.minecraft.class_2102.class, net.minecraft.class_2102.field_45764);     // EntityEffectPredicate
        hier(b, net.minecraft.class_4559.class, net.minecraft.class_4559.field_45777);     // StatePredicate
        hier(b, net.minecraft.class_2035.class, net.minecraft.class_2035.field_45741);     // EnchantmentPredicate
        hier(b, net.minecraft.class_2040.class, net.minecraft.class_2040.field_45745);     // EntityFlagsPredicate
        hier(b, net.minecraft.class_2096.class_2100.class, net.minecraft.class_2096.class_2100.field_45763); // NumberRange.IntRange
        hier(b, net.minecraft.class_2096.class_2099.class, net.minecraft.class_2096.class_2099.field_45762); // NumberRange.FloatRange
        return b.create();
    }

    /** a loot value (or array of them) as 1.20.1 JSON, through the surviving codecs */
    public static JsonElement toJsonTree(Object value) {
        return gson().toJsonTree(value);
    }

    private static <T> void hier(GsonBuilder b, Class<T> type, Codec<T> codec) {
        b.registerTypeHierarchyAdapter(type, new CbLootGson.CodecAdapter<>(() -> codec, j -> j, type.getSimpleName()));
    }

    private static final JsonDeserializationContext DESERIALIZE = new JsonDeserializationContext() {
        @Override
        public <T> T deserialize(JsonElement json, Type typeOfT) throws JsonParseException {
            return gson().fromJson(json, typeOfT);
        }
    };

    private static final JsonSerializationContext SERIALIZE = new JsonSerializationContext() {
        @Override
        public JsonElement serialize(Object src) {
            return gson().toJsonTree(src);
        }

        @Override
        public JsonElement serialize(Object src, Type typeOfSrc) {
            return gson().toJsonTree(src, typeOfSrc);
        }
    };

    /**
     * The codec a 1.20.2 loot type wants, from the JsonSerializer a 1.20.1 mod wrote. A MAP
     * codec: the "function"/"condition"/"type" dispatch hands a map codec the whole object --
     * the type key included, exactly what 1.20.1's fromJson received -- while a plain codec is
     * handed only a "value" entry the 1.20.1 JSON never has (every legacy loot function of a
     * converted mod failed to parse with "Not a json array" until this was a map codec).
     * DFU 6.0.8's KeyDispatchCodec checks for exactly this MapCodecCodec on decode and encode.
     */
    public static <T> Codec<T> codec(top.dext.centurybridge.facade.v1_20_2.class_5335<T> serializer) {
        return new Legacy<>(serializer).codec();
    }

    /** the 1.20.1 serializer behind a codec {@link #codec} made, or null for any other codec */
    @SuppressWarnings("rawtypes")
    public static top.dext.centurybridge.facade.v1_20_2.class_5335 legacySerializer(Codec<?> codec) {
        if (codec instanceof MapCodec.MapCodecCodec<?> m && m.codec() instanceof Legacy<?> l) {
            return l.serializer;
        }
        return null;
    }

    private static final class Legacy<T> extends MapCodec<T> {
        final top.dext.centurybridge.facade.v1_20_2.class_5335<T> serializer;

        Legacy(top.dext.centurybridge.facade.v1_20_2.class_5335<T> serializer) {
            this.serializer = serializer;
        }

        @Override
        public <O> Stream<O> keys(DynamicOps<O> ops) {
            return Stream.empty();
        }

        @Override
        public <O> DataResult<T> decode(DynamicOps<O> ops, MapLike<O> input) {
            JsonElement el = ops.convertTo(JsonOps.INSTANCE, ops.createMap(input.entries()));
            if (!(el instanceof JsonObject)) {
                return DataResult.error(() -> "CenturyBridge: legacy JsonSerializer expects a JSON object, got " + el);
            }
            try {
                return DataResult.success(serializer.method_517((JsonObject) el, DESERIALIZE));
            } catch (RuntimeException e) {
                return DataResult.error(() -> "CenturyBridge: legacy JsonSerializer fromJson failed: " + e);
            }
        }

        @Override
        public <O> RecordBuilder<O> encode(T value, DynamicOps<O> ops, RecordBuilder<O> prefix) {
            JsonObject out = new JsonObject();
            try {
                serializer.method_516(out, value, SERIALIZE);
            } catch (RuntimeException e) {
                return prefix.withErrorsFrom(DataResult.error(() -> "CenturyBridge: legacy JsonSerializer toJson failed: " + e));
            }
            for (var e : out.entrySet()) {
                prefix.add(e.getKey(), JsonOps.INSTANCE.convertTo(ops, e.getValue()));
            }
            return prefix;
        }

        @Override
        public String toString() {
            return "CenturyBridge legacy loot serializer " + serializer.getClass().getName();
        }
    }
}
