package top.dext.centurybridge.compat.fapi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.class_2960;
import net.minecraft.class_6903;

/**
 * The 1.20.1 ResourceConditions statics the 1.20.5 codec rework removed (staticRedirects). A legacy
 * condition was an id plus a {@code Predicate<JsonObject>} over the raw condition object; here it becomes a
 * ResourceConditionType whose MapCodec keeps the whole object verbatim, so the predicate still sees exactly
 * the JSON the data file carries. {@code get} and {@code objectMatchesConditions} run the registered
 * (legacy or built-in) types through their codecs; built-in types get no registry lookup, which the ones
 * the 1.20.1 corpus reaches this way (mod-loaded, tags) do not need.
 */
public final class CbResourceConditions {

    private static final Map<class_2960, Predicate<JsonObject>> LEGACY = new ConcurrentHashMap<>();

    /** a condition whose parameters are kept as the raw JSON object the file carried */
    record Legacy(ResourceConditionType<Legacy> type, Predicate<JsonObject> predicate, JsonObject json) implements ResourceCondition {
        @Override
        public ResourceConditionType<?> getType() {
            return type;
        }

        @Override
        public boolean test(class_6903.class_7863 registries) {
            return predicate.test(json);
        }
    }

    private static final class LegacyCodec extends MapCodec<Legacy> {
        private final Predicate<JsonObject> predicate;
        ResourceConditionType<Legacy> type;

        LegacyCodec(Predicate<JsonObject> predicate) {
            this.predicate = predicate;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.empty();
        }

        @Override
        public <T> DataResult<Legacy> decode(DynamicOps<T> ops, MapLike<T> input) {
            JsonObject json = new JsonObject();
            input.entries().forEach(e -> {
                String key = ops.getStringValue(e.getFirst()).result().orElse(null);
                if (key != null) {
                    json.add(key, ops.convertTo(JsonOps.INSTANCE, e.getSecond()));
                }
            });
            return DataResult.success(new Legacy(type, predicate, json));
        }

        @Override
        public <T> RecordBuilder<T> encode(Legacy input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            for (Map.Entry<String, JsonElement> e : input.json().entrySet()) {
                if (!e.getKey().equals("condition")) {
                    prefix.add(e.getKey(), JsonOps.INSTANCE.convertTo(ops, e.getValue()));
                }
            }
            return prefix;
        }
    }

    /** was ResourceConditions.register(Identifier, Predicate&lt;JsonObject&gt;) */
    public static void register(class_2960 id, Predicate<JsonObject> predicate) {
        LegacyCodec codec = new LegacyCodec(predicate);
        ResourceConditionType<Legacy> type = ResourceConditionType.create(id, codec);
        codec.type = type;
        LEGACY.put(id, predicate);
        ResourceConditions.register(type);
    }

    /** was ResourceConditions.get(Identifier): the predicate over a raw condition object */
    public static Predicate<JsonObject> get(class_2960 id) {
        Predicate<JsonObject> legacy = LEGACY.get(id);
        if (legacy != null) {
            return legacy;
        }
        ResourceConditionType<?> type = ResourceConditions.getConditionType(id);
        if (type == null) {
            return null;
        }
        return CbResourceConditions::conditionMatches;
    }

    /** was ResourceConditions.objectMatchesConditions(JsonObject): every entry of "fabric:load_conditions" holds */
    public static boolean objectMatchesConditions(JsonObject object) {
        JsonElement conditions = object.get(ResourceConditions.CONDITIONS_KEY);
        if (conditions == null) {
            return true;
        }
        if (!conditions.isJsonArray()) {
            throw new RuntimeException("Expected " + ResourceConditions.CONDITIONS_KEY + " to be an array");
        }
        for (JsonElement e : (JsonArray) conditions) {
            if (!e.isJsonObject() || !conditionMatches(e.getAsJsonObject())) {
                return false;
            }
        }
        return true;
    }

    static boolean conditionMatches(JsonObject json) {
        ResourceCondition condition = ResourceCondition.CONDITION_CODEC.parse(JsonOps.INSTANCE, json)
            .getOrThrow(msg -> new RuntimeException("Invalid resource condition " + json + ": " + msg));
        return condition.test(null);
    }

    private CbResourceConditions() {
    }
}
