package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.resource.conditions;

import com.google.gson.JsonArray;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.resource.conditions.v1.ConditionJsonProvider;

/**
 * Fabric API 1.20.4's resource conditions on 1.20.5's. 1.20.4's conditions were JSON predicates registered
 * by id and tested on the condition's JSON object; 1.20.5's are codec-defined types, read from the same JSON
 * ({"condition": id, parameters...} under "fabric:load_conditions").
 * <ul>
 * <li>{@code ResourceConditions.register(id, predicate)} (1.20.4): the id becomes a 1.20.5 type too, whose
 *     condition is the JSON object it was written as (every field, "condition" included, as 1.20.4 handed
 *     its predicates) and tests the predicate -- so data files naming the id load under 1.20.5's own
 *     condition handling, and 1.20.4 code testing the id gets its predicate back;</li>
 * <li>{@code get(id)}: that predicate, or -- for an id 1.20.5 knows as a type (Fabric's defaults, a 1.20.5
 *     mod's) -- a predicate reading the JSON with the type's codec and testing it with the reload's
 *     registries, which 1.20.4's registry_contains read the same way (1.20.4's CURRENT_REGISTRIES:
 *     {@link #reloadStarted} / {@link #reloadEnded}, CbFapiConditionReloadMixin);</li>
 * <li>{@code objectMatchesConditions} / {@code conditionsMatch} / {@code conditionMatches}: 1.20.4's
 *     bodies over {@code get};</li>
 * <li>1.20.4's default ids that 1.20.5 dropped -- any_mod_loaded (1.20.5: any_mods_loaded) and
 *     block / fluid / item_tags_populated (1.20.5: tags_populated with a registry) -- are registered as
 *     1.20.5 types reading 1.20.4's parameters ({@link #registerLegacyDefaults}, from CbFapiInit), tested
 *     as 1.20.4 tested them: a tags_populated id passes when every listed tag was loaded with at least one
 *     entry (1.20.4's LOADED_TAGS: the reload's tags as TagManagerLoader read them, captured by
 *     CbFapiConditionTagsMixin where Fabric API's own mixin captures 1.20.5's);</li>
 * <li>a data generator's {@code ConditionJsonProvider}s become 1.20.5 conditions that write exactly their
 *     JSON ({@link #resourceConditions}).</li>
 * </ul>
 * 1.20.5's own fabric:tags_populated (an id 1.20.4 had too) is Fabric API 1.20.5's type: it passes for a
 * loaded tag with no entries, where 1.20.4's failed. Data a 1.20.4 mod ships naming that id is read by that
 * type (the id cannot be registered twice); the 1.20.4-only ids above are this layer's and keep 1.20.4's test.
 */
public final class CbLegacyConditions {

    static final Logger LOGGER = LoggerFactory.getLogger("centurybridge/fabric-resource-conditions-api-v1");

    /** 1.20.4's REGISTERED_CONDITIONS: the predicates 1.20.4 code registered */
    private static final Map<net.minecraft.class_2960, Predicate<JsonObject>> PREDICATES = new ConcurrentHashMap<>();
    /** the 1.20.5 type carrying a JSON condition of an id (registered for 1.20.4 predicates, unregistered for datagen) */
    private static final Map<net.minecraft.class_2960, ResourceConditionType<JsonCondition>> JSON_TYPES = new ConcurrentHashMap<>();

    /*
     * 1.20.4's LOADED_TAGS and CURRENT_REGISTRIES were thread-locals: 1.20.4 (and 1.20.5 to 1.21.1) set them and tested
     * conditions on the reload's game executor. Here they are held for the whole process, as Fabric API 1.21.2 holds
     * its own LOADED_TAGS (an AtomicReference): from 1.21.2 on Fabric API tests conditions while JSON loaders prepare,
     * on the background executor, which a thread-local set on the reload's thread does not reach. One reload runs at a
     * time, so each reload's conditions read that reload's values either way.
     */
    /** 1.20.4's ResourceConditionsImpl.LOADED_TAGS: the tags the current reload read, by registry */
    private static final AtomicReference<Map<net.minecraft.class_5321<?>, Map<net.minecraft.class_2960, Collection<?>>>> LOADED_TAGS = new AtomicReference<>();
    /** 1.20.4's ResourceConditionsImpl.CURRENT_REGISTRIES: the current reload's registries */
    private static final AtomicReference<net.minecraft.class_7225.class_7874> CURRENT_REGISTRIES = new AtomicReference<>();

    private CbLegacyConditions() {
    }

    // ---------------------------------------------------------------- 1.20.4's ResourceConditions statics

    public static void register(net.minecraft.class_2960 identifier, Predicate<JsonObject> condition) {
        Objects.requireNonNull(identifier, "Identifier may not be null.");
        Objects.requireNonNull(condition, "Condition may not be null.");
        if (PREDICATES.put(identifier, condition) != null) {
            throw new IllegalArgumentException("Duplicate JSON condition registration with id " + identifier);
        }
        try {
            ResourceConditions.register(jsonType(identifier));
        } catch (RuntimeException e) {
            // 1.20.5's Fabric API (or a 1.20.5 mod) has a type of this id: it stays the id's reader
            PREDICATES.remove(identifier, condition);
            throw e;
        }
    }

    public static Predicate<JsonObject> get(net.minecraft.class_2960 identifier) {
        Predicate<JsonObject> own = PREDICATES.get(identifier);
        if (own != null) {
            return own;
        }
        ResourceConditionType<?> type = ResourceConditions.getConditionType(identifier);
        if (type == null) {
            return null;
        }
        return json -> type.codec().codec().parse(JsonOps.INSTANCE, json)
            .getOrThrow(error -> new JsonParseException("Invalid " + identifier + " condition " + json + ": " + error))
            .test(CURRENT_REGISTRIES.get());
    }

    public static boolean objectMatchesConditions(JsonObject object) {
        try {
            JsonArray conditions = net.minecraft.class_3518.method_15292(object, ResourceConditions.CONDITIONS_KEY, null);
            if (conditions == null) {
                return true;
            }
            return conditionsMatch(conditions, true);
        } catch (RuntimeException exception) {
            LOGGER.warn("Skipping object %s. Failed to parse resource conditions".formatted(object), exception);
            return false;
        }
    }

    public static boolean conditionsMatch(JsonArray conditions, boolean and) throws RuntimeException {
        for (JsonElement element : conditions) {
            if (element.isJsonObject()) {
                if (conditionMatches(element.getAsJsonObject()) != and) {
                    return !and;
                }
            } else {
                throw new JsonParseException("Invalid condition entry: " + element);
            }
        }
        return and;
    }

    public static boolean conditionMatches(JsonObject condition) throws RuntimeException {
        net.minecraft.class_2960 conditionId = new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(condition, CONDITION_ID_KEY()));
        Predicate<JsonObject> jrc = get(conditionId);
        if (jrc == null) {
            throw new JsonParseException("Unknown recipe condition: " + conditionId);
        }
        return jrc.test(condition);
    }

    /** 1.20.4's ResourceConditions.CONDITION_ID_KEY (fieldGetters) */
    public static String CONDITION_ID_KEY() {
        return "condition";
    }

    // ---------------------------------------------------------------- a JSON condition as a 1.20.5 condition

    /** a condition as 1.20.4 wrote it: its whole JSON object, "condition" included */
    record JsonCondition(ResourceConditionType<JsonCondition> type, JsonObject json, ConditionJsonProvider provider) implements ResourceCondition {

        @Override
        public ResourceConditionType<?> getType() {
            return type;
        }

        @Override
        public boolean test(net.minecraft.class_7225.class_7874 registryLookup) {
            Predicate<JsonObject> own = PREDICATES.get(type.id());
            if (own != null) {
                return own.test(json);
            }
            // a data generator's condition of an id this layer only carries: tested as 1.20.5 reads its JSON
            return ResourceCondition.CODEC.parse(JsonOps.INSTANCE, json)
                .getOrThrow(error -> new JsonParseException("Invalid condition " + json + ": " + error))
                .test(registryLookup);
        }
    }

    private static ResourceConditionType<JsonCondition> jsonType(net.minecraft.class_2960 id) {
        return JSON_TYPES.computeIfAbsent(id, CbLegacyConditions::newJsonType);
    }

    @SuppressWarnings("unchecked")
    private static ResourceConditionType<JsonCondition> newJsonType(net.minecraft.class_2960 id) {
        ResourceConditionType<JsonCondition>[] self = new ResourceConditionType[1];
        MapCodec<JsonCondition> codec = new MapCodec<>() {
            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.empty();
            }

            @Override
            public <T> DataResult<JsonCondition> decode(DynamicOps<T> ops, MapLike<T> input) {
                JsonObject json = new JsonObject();
                json.addProperty(CONDITION_ID_KEY(), id.toString());
                input.entries().forEach(e -> {
                    String key = ops.getStringValue(e.getFirst()).result().orElse(null);
                    if (key != null && !key.equals(CONDITION_ID_KEY())) {
                        json.add(key, ops.convertTo(JsonOps.INSTANCE, e.getSecond()));
                    }
                });
                return DataResult.success(new JsonCondition(self[0], json, null));
            }

            @Override
            public <T> RecordBuilder<T> encode(JsonCondition input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                for (Map.Entry<String, JsonElement> e : input.json().entrySet()) {
                    if (!e.getKey().equals(CONDITION_ID_KEY())) {
                        prefix.add(e.getKey(), JsonOps.INSTANCE.convertTo(ops, e.getValue()));
                    }
                }
                return prefix;
            }
        };
        self[0] = ResourceConditionType.create(id, codec);
        return self[0];
    }

    /**
     * A data generator's 1.20.4 conditions as 1.20.5's datagen takes them (the withConditions bridges):
     * conditions that write each provider's JSON as 1.20.4 wrote it ({@code toJson()}), whether or not this
     * process registered its id -- 1.20.4's datagen wrote a condition's JSON without looking it up either.
     */
    public static ResourceCondition[] resourceConditions(ConditionJsonProvider[] providers) {
        ResourceCondition[] out = new ResourceCondition[providers.length];
        for (int i = 0; i < providers.length; i++) {
            ConditionJsonProvider provider = Objects.requireNonNull(providers[i], "condition cannot be null");
            out[i] = new JsonCondition(jsonType(provider.getConditionId()), provider.toJson(), provider);
        }
        return out;
    }

    /**
     * 1.20.5 conditions as the 1.20.4 providers a 1.20.4 override of a data generator's withConditions takes
     * (the override hooks' wrap): a condition made from a provider gives that provider back, any other writes
     * the JSON 1.20.5 writes for it.
     */
    public static ConditionJsonProvider[] providers(ResourceCondition[] conditions) {
        ConditionJsonProvider[] out = new ConditionJsonProvider[conditions.length];
        for (int i = 0; i < conditions.length; i++) {
            ResourceCondition c = conditions[i];
            if (c instanceof JsonCondition j && j.provider() != null) {
                out[i] = j.provider();
                continue;
            }
            JsonObject json = ResourceCondition.CODEC.encodeStart(JsonOps.INSTANCE, c)
                .getOrThrow(error -> new IllegalStateException("Cannot write condition " + c + ": " + error)).getAsJsonObject();
            net.minecraft.class_2960 id = new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, CONDITION_ID_KEY()));
            out[i] = new ConditionJsonProvider() {
                @Override
                public net.minecraft.class_2960 getConditionId() {
                    return id;
                }

                @Override
                public void writeParameters(JsonObject object) {
                    for (Map.Entry<String, JsonElement> e : json.entrySet()) {
                        if (!e.getKey().equals(CONDITION_ID_KEY())) {
                            object.add(e.getKey(), e.getValue().deepCopy());
                        }
                    }
                }
            };
        }
        return out;
    }

    // ---------------------------------------------------------------- the reload, as 1.20.4's impl captured it

    /** CbFapiConditionReloadMixin: DataPackContents.reload began (1.20.4 set CURRENT_REGISTRIES here) */
    public static void reloadStarted(net.minecraft.class_7225.class_7874 registries) {
        CURRENT_REGISTRIES.set(registries);
    }

    /** CbFapiConditionReloadMixin: DataPackContents.refresh, the reload's end (1.20.4 cleared both here) */
    public static void reloadEnded() {
        LOADED_TAGS.set(null);
        CURRENT_REGISTRIES.set(null);
    }

    /** CbFapiConditionTagsMixin: the tags TagManagerLoader read, at the end of its apply step */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void setTags(List<net.minecraft.class_3505.class_6863<?>> tags) {
        Map<net.minecraft.class_5321<?>, Map<net.minecraft.class_2960, Collection<?>>> tagMap = new HashMap<>();
        for (net.minecraft.class_3505.class_6863<?> registryTags : tags) {
            tagMap.put(registryTags.comp_328(), (Map) registryTags.comp_329());
        }
        LOADED_TAGS.set(tagMap);
    }

    /** 1.20.4's ResourceConditionsImpl.tagsPopulatedMatch(object, registryKey) over its parameters */
    static boolean tagsPopulated(net.minecraft.class_5321<?> registryKey, List<net.minecraft.class_2960> values) {
        Map<net.minecraft.class_5321<?>, Map<net.minecraft.class_2960, Collection<?>>> allTags = LOADED_TAGS.get();
        if (allTags == null) {
            LOGGER.warn("Can't retrieve deserialized tags. Failing tags_populated resource condition check.");
            return false;
        }
        Map<net.minecraft.class_2960, Collection<?>> registryTags = allTags.get(registryKey);
        if (registryTags == null) {
            return values.isEmpty();
        }
        for (net.minecraft.class_2960 id : values) {
            Collection<?> entries = registryTags.get(id);
            if (entries == null || entries.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // ---------------------------------------------------------------- 1.20.4's default ids 1.20.5 dropped

    /** a 1.20.4 default condition 1.20.5 has no type for: its "values", tested as 1.20.4 tested them */
    record Legacy<V>(ResourceConditionType<Legacy<V>> type, List<V> values, Predicate<List<V>> test) implements ResourceCondition {

        @Override
        public ResourceConditionType<?> getType() {
            return type;
        }

        @Override
        public boolean test(net.minecraft.class_7225.class_7874 registryLookup) {
            return test.test(values);
        }
    }

    @SuppressWarnings("unchecked")
    private static <V> void legacy(String id, Codec<V> value, Predicate<List<V>> test) {
        ResourceConditionType<Legacy<V>>[] self = new ResourceConditionType[1];
        MapCodec<Legacy<V>> codec = value.listOf().fieldOf("values").xmap(values -> new Legacy<>(self[0], values, test), Legacy::values);
        self[0] = ResourceConditionType.create(new net.minecraft.class_2960(id), codec);
        ResourceConditions.register(self[0]);
    }

    /** 1.20.4's any_mod_loaded and block / fluid / item_tags_populated; called once, from CbFapiInit */
    public static void registerLegacyDefaults() {
        legacy("fabric:any_mod_loaded", Codec.STRING, modIds -> {
            for (String modId : modIds) {
                if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded(modId)) {
                    return true;
                }
            }
            return false;
        });
        legacy("fabric:block_tags_populated", net.minecraft.class_2960.field_25139,
            tags -> tagsPopulated(net.minecraft.class_7924.field_41254, tags));
        legacy("fabric:fluid_tags_populated", net.minecraft.class_2960.field_25139,
            tags -> tagsPopulated(net.minecraft.class_7924.field_41270, tags));
        legacy("fabric:item_tags_populated", net.minecraft.class_2960.field_25139,
            tags -> tagsPopulated(net.minecraft.class_7924.field_41197, tags));
    }
}
