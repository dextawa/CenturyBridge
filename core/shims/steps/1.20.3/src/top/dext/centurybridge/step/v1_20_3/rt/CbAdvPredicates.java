package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import top.dext.centurybridge.step.v1_20_3.facade.DeserializationContext;

/**
 * The 1.20.2 JSON methods of the advancement predicates, gone in 1.20.3 (predicates are read and
 * written by their codecs there). In 1.20.2 most of them were already thin wrappers over those
 * same codecs -- fromJson: null or JSON null is "no predicate", else the codec's parse with a
 * JsonParseException on failure; serializeToJson: the codec's encoding, an IllegalStateException
 * on failure -- and each bridge here is that 1.20.2 body on 1.20.3's codec. The ones 1.20.2 wrote
 * out by hand (DamagePredicate, ContextAwarePredicate's element / list forms, EntityPredicate's
 * player / list fields) are ported as they were, over the bridges for the pieces they used.
 */
public final class CbAdvPredicates {

    static <T> T parse(Codec<T> codec, JsonElement json) {
        return net.minecraft.class_156.method_47526(codec.parse(JsonOps.INSTANCE, json), JsonParseException::new);
    }

    static <T> JsonElement encode(Codec<T> codec, T value) {
        return net.minecraft.class_156.method_47526(codec.encodeStart(JsonOps.INSTANCE, value), IllegalStateException::new);
    }

    private static <T> Optional<T> parseOptional(Codec<T> codec, JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return Optional.empty();
        }
        return Optional.of(parse(codec, json));
    }

    // ---------------------------------------------------------------- ContextAwarePredicate (class_5258)

    /** ContextAwarePredicate.toJson(): its conditions as a JSON list (1.20.3's codec writes the same list) */
    public static JsonElement method_27804(net.minecraft.class_5258 predicate) {
        return encode(net.minecraft.class_5258.field_47234, predicate);
    }

    /**
     * ContextAwarePredicate.fromElement(key, context, json, params): a JSON array is a list of loot
     * conditions, deserialized (and validated) by the context under "id/key" -- present-but-empty
     * when the list is; anything else is not this form (empty), for the caller to read as an
     * entity predicate.
     */
    public static Optional<Optional<net.minecraft.class_5258>> method_27807(String key, DeserializationContext context,
            JsonElement json, net.minecraft.class_176 params) {
        if (json != null && json.isJsonArray()) {
            List<net.minecraft.class_5341> conditions = context.method_27796(json.getAsJsonArray(),
                context.method_27795() + "/" + key, params);
            if (conditions.isEmpty()) {
                return Optional.of(Optional.empty());
            }
            // 1.20.2's constructor is package-private on 1.20.3; create(...) builds the same predicate
            return Optional.of(Optional.of(net.minecraft.class_5258.method_27973(
                conditions.toArray(new net.minecraft.class_5341[0]))));
        }
        return Optional.empty();
    }

    /** ContextAwarePredicate.toJson(list): JSON null for no predicates, else the array of each one's JSON */
    public static JsonElement method_27808(List<net.minecraft.class_5258> predicates) {
        if (predicates.isEmpty()) {
            return JsonNull.INSTANCE;
        }
        JsonArray array = new JsonArray();
        for (net.minecraft.class_5258 p : predicates) {
            array.add(method_27804(p));
        }
        return array;
    }

    // ---------------------------------------------------------------- EntityPredicate (class_2048)

    /** EntityPredicate.fromJson(json, key, context): the field read as a context-aware (player-side) predicate */
    public static Optional<net.minecraft.class_5258> method_51705(JsonObject json, String key, DeserializationContext context) {
        return entityFromElement(key, context, json.get(key));
    }

    /** EntityPredicate.fromJsonArray(json, key, context): a list field, each element read like fromJson's value */
    public static List<net.minecraft.class_5258> method_51707(JsonObject json, String key, DeserializationContext context) {
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) {
            return List.of();
        }
        JsonArray array = net.minecraft.class_3518.method_15252(element, key);
        List<net.minecraft.class_5258> out = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); i++) {
            entityFromElement(key + "[" + i + "]", context, array.get(i)).ifPresent(out::add);
        }
        return List.copyOf(out);
    }

    /**
     * 1.20.2 EntityPredicate.fromElement (private): a condition list (validated with the
     * ADVANCEMENT_ENTITY parameters), else an entity predicate wrapped as a context-aware one.
     */
    private static Optional<net.minecraft.class_5258> entityFromElement(String key, DeserializationContext context,
            JsonElement json) {
        Optional<Optional<net.minecraft.class_5258>> conditions = method_27807(key, context, json,
            net.minecraft.class_173.field_24423);
        if (conditions.isPresent()) {
            return conditions.get();
        }
        return net.minecraft.class_2048.method_53137(method_8913(json));
    }

    /** EntityPredicate.fromJson(json) */
    public static Optional<net.minecraft.class_2048> method_8913(JsonElement json) {
        return parseOptional(net.minecraft.class_2048.field_45746, json);
    }

    /** EntityPredicate.serializeToJson() */
    public static JsonElement method_8912(net.minecraft.class_2048 predicate) {
        return encode(net.minecraft.class_2048.field_45746, predicate);
    }

    // ---------------------------------------------------------------- ItemPredicate (class_2073)

    /** ItemPredicate.fromJson(json) */
    public static Optional<net.minecraft.class_2073> method_8969(JsonElement json) {
        return parseOptional(net.minecraft.class_2073.field_45754, json);
    }

    /** ItemPredicate.serializeToJson() */
    public static JsonElement method_8971(net.minecraft.class_2073 predicate) {
        return encode(net.minecraft.class_2073.field_45754, predicate);
    }

    /** ItemPredicate.serializeToJsonArray(list) */
    public static JsonElement method_53164(List<net.minecraft.class_2073> predicates) {
        return encode(net.minecraft.class_2073.field_45754.listOf(), predicates);
    }

    /** ItemPredicate.fromJsonArray(json): null or JSON null is the empty list */
    public static List<net.minecraft.class_2073> method_8972(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return List.of();
        }
        return parse(net.minecraft.class_2073.field_45754.listOf(), json);
    }

    // ---------------------------------------------------------------- DamagePredicate (class_2019)

    /**
     * DamagePredicate.fromJson(json), as 1.20.2 read it field by field: "dealt" / "taken" bounds,
     * "blocked", "source_entity", "type"; a predicate with nothing in it is no predicate.
     */
    public static Optional<net.minecraft.class_2019> method_8839(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return Optional.empty();
        }
        JsonObject object = net.minecraft.class_3518.method_15295(json, "damage");
        net.minecraft.class_2096.class_2099 dealt = method_9051(object.get("dealt"));
        net.minecraft.class_2096.class_2099 taken = method_9051(object.get("taken"));
        Optional<Boolean> blocked = object.has("blocked")
            ? Optional.of(net.minecraft.class_3518.method_15270(object, "blocked")) : Optional.empty();
        Optional<net.minecraft.class_2048> source = method_8913(object.get("source_entity"));
        Optional<net.minecraft.class_2022> type = method_8846(object.get("type"));
        if (dealt.method_9041() && taken.method_9041() && source.isEmpty() && blocked.isEmpty() && type.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new net.minecraft.class_2019(dealt, taken, source, blocked, type));
    }

    /** DamagePredicate.serializeToJson(), as 1.20.2 wrote it */
    public static JsonElement method_8840(net.minecraft.class_2019 predicate) {
        JsonObject json = new JsonObject();
        json.add("dealt", method_53196(predicate.comp_1735()));
        json.add("taken", method_53196(predicate.comp_1736()));
        predicate.comp_1737().ifPresent(e -> json.add("source_entity", method_8912(e)));
        predicate.comp_1739().ifPresent(t -> json.add("type", method_8848(t)));
        predicate.comp_1738().ifPresent(b -> json.addProperty("blocked", b));
        return json;
    }

    // ---------------------------------------------------------------- DamageSourcePredicate (class_2022)

    /** DamageSourcePredicate.fromJson(json) */
    public static Optional<net.minecraft.class_2022> method_8846(JsonElement json) {
        return parseOptional(net.minecraft.class_2022.field_45727, json);
    }

    /** DamageSourcePredicate.serializeToJson() */
    public static JsonElement method_8848(net.minecraft.class_2022 predicate) {
        return encode(net.minecraft.class_2022.field_45727, predicate);
    }

    // ---------------------------------------------------------------- DistancePredicate (class_2025)

    /** DistancePredicate.fromJson(json) */
    public static Optional<net.minecraft.class_2025> method_8857(JsonElement json) {
        return parseOptional(net.minecraft.class_2025.field_45728, json);
    }

    /** DistancePredicate.serializeToJson() */
    public static JsonElement method_8858(net.minecraft.class_2025 predicate) {
        return encode(net.minecraft.class_2025.field_45728, predicate);
    }

    // ---------------------------------------------------------------- LocationPredicate (class_2090)

    /** LocationPredicate.fromJson(json) */
    public static Optional<net.minecraft.class_2090> method_9021(JsonElement json) {
        return parseOptional(net.minecraft.class_2090.field_45760, json);
    }

    /** LocationPredicate.serializeToJson() */
    public static JsonElement method_9019(net.minecraft.class_2090 predicate) {
        return encode(net.minecraft.class_2090.field_45760, predicate);
    }

    // ---------------------------------------------------------------- MinMaxBounds (class_2096)

    /** MinMaxBounds.Doubles.fromJson(json): null or JSON null is ANY */
    public static net.minecraft.class_2096.class_2099 method_9051(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return net.minecraft.class_2096.class_2099.field_9705;
        }
        return parse(net.minecraft.class_2096.class_2099.field_45762, json);
    }

    /** MinMaxBounds.Doubles.serializeToJson(): JSON null for ANY */
    public static JsonElement method_53196(net.minecraft.class_2096.class_2099 bounds) {
        if (bounds.method_9041()) {
            return JsonNull.INSTANCE;
        }
        return encode(net.minecraft.class_2096.class_2099.field_45762, bounds);
    }

    /** MinMaxBounds.Ints.fromJson(json): null or JSON null is ANY */
    public static net.minecraft.class_2096.class_2100 method_9056(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return net.minecraft.class_2096.class_2100.field_9708;
        }
        return parse(net.minecraft.class_2096.class_2100.field_45763, json);
    }

    /** MinMaxBounds.Ints.serializeToJson(): JSON null for ANY */
    public static JsonElement method_53199(net.minecraft.class_2096.class_2100 bounds) {
        if (bounds.method_9041()) {
            return JsonNull.INSTANCE;
        }
        return encode(net.minecraft.class_2096.class_2100.field_45763, bounds);
    }

    // ---------------------------------------------------------------- MobEffectsPredicate (class_2102)

    /** MobEffectsPredicate.fromJson(json) */
    public static Optional<net.minecraft.class_2102> method_9064(JsonElement json) {
        return parseOptional(net.minecraft.class_2102.field_45764, json);
    }

    /** MobEffectsPredicate.serializeToJson() */
    public static JsonElement method_9068(net.minecraft.class_2102 predicate) {
        return encode(net.minecraft.class_2102.field_45764, predicate);
    }

    // ---------------------------------------------------------------- StatePropertiesPredicate (class_4559)

    /** StatePropertiesPredicate.fromJson(json) */
    public static Optional<net.minecraft.class_4559> method_22519(JsonElement json) {
        return parseOptional(net.minecraft.class_4559.field_45777, json);
    }

    /** StatePropertiesPredicate.serializeToJson() */
    public static JsonElement method_22513(net.minecraft.class_4559 predicate) {
        return encode(net.minecraft.class_4559.field_45777, predicate);
    }

    /**
     * StatePropertiesPredicate.checkState(definition, sink): every property the definition lacks is
     * handed to the sink, in order (1.20.3's checkState(definition) answers only the first). Each
     * matcher is checked through a predicate of that one matcher -- 1.20.3's checkState of it is
     * that matcher's own check -- since the matcher type itself is package-private.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void method_22516(net.minecraft.class_4559 predicate, net.minecraft.class_2689<?, ?> definition,
            Consumer<String> sink) {
        for (Object matcher : (List<?>) predicate.comp_1830()) {
            new net.minecraft.class_4559((List) List.of(matcher)).method_53235(definition).ifPresent(sink);
        }
    }

    private CbAdvPredicates() {
    }
}
