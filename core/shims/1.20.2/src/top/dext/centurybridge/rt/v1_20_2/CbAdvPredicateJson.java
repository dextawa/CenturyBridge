package top.dext.centurybridge.rt.v1_20_2;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import top.dext.centurybridge.shims.v1_20_2.CbAdvDistancePredicateAccess;
import top.dext.centurybridge.shims.v1_20_2.CbAdvEntityPredicateAccess;
import top.dext.centurybridge.shims.v1_20_2.CbAdvItemPredicateAccess;
import top.dext.centurybridge.shims.v1_20_2.CbAdvLocationPredicateAccess;
import top.dext.centurybridge.shims.v1_20_2.CbAdvMobEffectsPredicateAccess;
import top.dext.centurybridge.shims.v1_20_2.CbAdvStatePropertiesAccess;

/**
 * The route that consults the advancement predicates' restored 1.20.1 JSON readers (CbAdv*PredicateJson, private statics
 * on the predicate classes) inside 1.20.2's own readers: 1.20.2's fromJson runs and answers; then the restored reader
 * (which a converted mod's mixin may change) and its unmixable copy here (the same 1.20.1 body, CenturyBridge names)
 * each read the same JSON, and only where the two differ -- the restored one was mixed -- does 1.20.2's caller get
 * 1.20.1's answer (or its exception). Unmixed, the answer is exactly 1.20.2's. Two answers are the same when both throw
 * the same exception or both write the same JSON (the predicates' serializeToJson, ANY writing JsonNull).
 *
 * -Dcenturybridge.legacyJsonRoute=false (the recipe and advancement readers' switch): never consulted.
 */
public final class CbAdvPredicateJson {

    /** LocationPredicate's 1.20.1 LOGGER (gone with its fromJson): where the restored reader logs an unparsable id */
    public static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    private CbAdvPredicateJson() {
    }

    // ------------------------------------------------------------------ the consult

    private static Object attempt(Supplier<?> read) {
        try {
            return read.get();
        } catch (RuntimeException e) {
            return e;
        }
    }

    /** null: keep 1.20.2's answer; otherwise 1.20.1's (thrown if it threw) */
    private static <T, R> R consult(Supplier<T> mixed, Supplier<T> golden, Function<T, JsonElement> json, Function<T, R> answer) {
        if (!CbRecipeJson.ROUTE) {
            return null;
        }
        Object m = attempt(mixed);
        Object g = attempt(golden);
        if (m instanceof RuntimeException a || g instanceof RuntimeException) {
            if (m instanceof RuntimeException a && g instanceof RuntimeException b && a.getClass() == b.getClass()
                    && Objects.equals(a.getMessage(), b.getMessage())) {
                return null;
            }
            if (m instanceof RuntimeException a) {
                throw a;
            }
        } else {
            @SuppressWarnings("unchecked")
            T mt = (T) m;
            @SuppressWarnings("unchecked")
            T gt = (T) g;
            if (Objects.equals(json.apply(mt), json.apply(gt))) {
                return null;
            }
        }
        @SuppressWarnings("unchecked")
        T mt = (T) m;
        return answer.apply(mt);
    }

    private static JsonElement items(net.minecraft.class_2073[] p) {
        JsonArray out = new JsonArray();
        for (net.minecraft.class_2073 e : p) {
            out.add(e.method_8971());
        }
        return out;
    }

    /** 1.20.2 ItemPredicate.fromJson(json) returned: 1.20.1's answer where the restored reader was mixed, else null */
    public static Optional<net.minecraft.class_2073> consultItem(JsonElement json) {
        return consult(() -> CbAdvItemPredicateAccess.cb$fromJson(json), () -> itemFromJson(json),
            net.minecraft.class_2073::method_8971, CbAdvAny::item);
    }

    /** 1.20.2 ItemPredicate.fromJsonArray(json) returned: 1.20.1's answer where the restored reader was mixed, else null */
    public static List<net.minecraft.class_2073> consultItems(JsonElement json) {
        return consult(() -> CbAdvItemPredicateAccess.cb$fromJsonArray(json), () -> itemsFromJson(json),
            CbAdvPredicateJson::items, a -> List.copyOf(Arrays.asList(a)));
    }

    /** 1.20.2 LocationPredicate.fromJson(json) returned: 1.20.1's answer where the restored reader was mixed, else null */
    public static Optional<net.minecraft.class_2090> consultLocation(JsonElement json) {
        return consult(() -> CbAdvLocationPredicateAccess.cb$fromJson(json), () -> locationFromJson(json),
            net.minecraft.class_2090::method_9019, CbAdvAny::location);
    }

    /** 1.20.2 StatePropertiesPredicate.fromJson(json) returned: 1.20.1's answer where the restored reader was mixed, else null */
    public static Optional<net.minecraft.class_4559> consultState(JsonElement json) {
        return consult(() -> CbAdvStatePropertiesAccess.cb$fromJson(json), () -> stateFromJson(json),
            net.minecraft.class_4559::method_22513, CbAdvAny::state);
    }

    /** 1.20.2 EntityPredicate.fromJson(json) returned: 1.20.1's answer where the restored reader was mixed, else null */
    public static Optional<net.minecraft.class_2048> consultEntity(JsonElement json) {
        return consult(() -> CbAdvEntityPredicateAccess.cb$fromJson(json), () -> entityFromJson(json),
            net.minecraft.class_2048::method_8912, CbAdvAny::entity);
    }

    /** 1.20.2 EntityPredicate.fromJson(json, name, context) returned: 1.20.1's answer where the restored reader was mixed, else null */
    public static Optional<net.minecraft.class_5258> consultEntityContext(JsonObject json, String name, net.minecraft.class_5257 context) {
        return consult(() -> CbAdvEntityPredicateAccess.cb$fromJsonNamed(json, name, context), () -> entityContextFromJson(json, name, context),
            net.minecraft.class_5258::method_27804, CbAdvAny::context);
    }

    /** 1.20.2 EntityPredicate.fromJsonArray(json, name, context) returned: 1.20.1's answer where the restored reader was mixed, else null */
    public static List<net.minecraft.class_5258> consultEntityContexts(JsonObject json, String name, net.minecraft.class_5257 context) {
        return consult(() -> CbAdvEntityPredicateAccess.cb$fromJsonArray(json, name, context), () -> entityContextsFromJson(json, name, context),
            CbAdvPredicateJson::contexts, CbAdvAny::contexts);
    }

    /** 1.20.2 DistancePredicate.fromJson(json) returned: 1.20.1's answer where the restored reader was mixed, else null */
    public static Optional<net.minecraft.class_2025> consultDistance(JsonElement json) {
        return consult(() -> CbAdvDistancePredicateAccess.cb$fromJson(json), () -> distanceFromJson(json),
            net.minecraft.class_2025::method_8858, CbAdvAny::distance);
    }

    /** 1.20.2 MobEffectsPredicate.fromJson(json) returned: 1.20.1's answer where the restored reader was mixed, else null */
    public static Optional<net.minecraft.class_2102> consultEffects(JsonElement json) {
        return consult(() -> CbAdvMobEffectsPredicateAccess.cb$fromJson(json), () -> effectsFromJson(json),
            net.minecraft.class_2102::method_9068, CbAdvAny::effects);
    }

    private static JsonElement contexts(net.minecraft.class_5258[] p) {
        JsonArray out = new JsonArray();
        for (net.minecraft.class_5258 e : p) {
            out.add(e.method_27804());
        }
        return out;
    }

    // ------------------------------------------------------------------ the unmixable copies (CbAdv*PredicateJson's bodies)

    /** copy of CbAdvItemPredicateJson.method_8969: 1.20.1 ItemPredicate.fromJson(json) */
    public static net.minecraft.class_2073 itemFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9640;
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "item");
        net.minecraft.class_2096.class_2100 count = net.minecraft.class_2096.class_2100.method_9056(json.get("count"));
        net.minecraft.class_2096.class_2100 durability = net.minecraft.class_2096.class_2100.method_9056(json.get("durability"));
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        net.minecraft.class_2105 nbt = nbtFromJson(json.get("nbt"));
        Set<net.minecraft.class_1792> items = null;
        JsonArray array = net.minecraft.class_3518.method_15292(json, "items", null);
        if (array != null) {
            ImmutableSet.Builder<net.minecraft.class_1792> builder = ImmutableSet.builder();
            for (JsonElement e : array) {
                net.minecraft.class_2960 id = new net.minecraft.class_2960(net.minecraft.class_3518.method_15287(e, "item"));
                builder.add(net.minecraft.class_7923.field_41178.method_17966(id)
                    .orElseThrow(() -> new JsonSyntaxException("Unknown item id '" + id + "'")));
            }
            items = builder.build();
        }
        net.minecraft.class_6862<net.minecraft.class_1792> tag = null;
        if (json.has("tag")) {
            net.minecraft.class_2960 id = new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, "tag"));
            tag = net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41197, id);
        }
        net.minecraft.class_1842 potion = null;
        if (json.has("potion")) {
            net.minecraft.class_2960 id = new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, "potion"));
            potion = net.minecraft.class_7923.field_41179.method_17966(id)
                .orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + id + "'"));
        }
        net.minecraft.class_2035[] enchantments = enchantmentsFromJson(json.get("enchantments"));
        net.minecraft.class_2035[] stored = enchantmentsFromJson(json.get("stored_enchantments"));
        return CbAdvRecords.itemPredicate(tag, items, count, durability, enchantments, stored, potion, nbt);
    }

    /** copy of CbAdvItemPredicateJson.method_8972: 1.20.1 ItemPredicate.fromJsonArray(json) */
    public static net.minecraft.class_2073[] itemsFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return new net.minecraft.class_2073[0];
        }
        JsonArray array = net.minecraft.class_3518.method_15252(element, "items");
        net.minecraft.class_2073[] out = new net.minecraft.class_2073[array.size()];
        for (int i = 0; i < out.length; ++i) {
            out[i] = itemFromJson(array.get(i));
        }
        return out;
    }

    /** copy of CbAdvNbtPredicateJson.method_9073: 1.20.1 NbtPredicate.fromJson(json) */
    public static net.minecraft.class_2105 nbtFromJson(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return CbAdvAny.field_9716;
        }
        net.minecraft.class_2487 tag;
        try {
            tag = net.minecraft.class_2522.method_10718(net.minecraft.class_3518.method_15287(json, "nbt"));
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            throw new JsonSyntaxException("Invalid nbt tag: " + e.getMessage());
        }
        return new net.minecraft.class_2105(tag);
    }

    /** copy of CbAdvEnchantmentPredicateJson.method_8882: 1.20.1 EnchantmentPredicate.fromJson(json) */
    public static net.minecraft.class_2035 enchantmentFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9571;
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "enchantment");
        net.minecraft.class_1887 enchantment = null;
        if (json.has("enchantment")) {
            net.minecraft.class_2960 id = new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, "enchantment"));
            enchantment = net.minecraft.class_7923.field_41176.method_17966(id)
                .orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + id + "'"));
        }
        net.minecraft.class_2096.class_2100 levels = net.minecraft.class_2096.class_2100.method_9056(json.get("levels"));
        return CbAdvRecords.enchantmentPredicate(enchantment, levels);
    }

    /** copy of CbAdvEnchantmentPredicateJson.method_8879: 1.20.1 EnchantmentPredicate.fromJsonArray(json) */
    public static net.minecraft.class_2035[] enchantmentsFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvJson.field_20687;
        }
        JsonArray array = net.minecraft.class_3518.method_15252(element, "enchantments");
        net.minecraft.class_2035[] out = new net.minecraft.class_2035[array.size()];
        for (int i = 0; i < out.length; ++i) {
            out[i] = enchantmentFromJson(array.get(i));
        }
        return out;
    }

    /** copy of CbAdvLocationPredicateJson.method_9021: 1.20.1 LocationPredicate.fromJson(json) */
    public static net.minecraft.class_2090 locationFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9685;
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "location");
        JsonObject position = net.minecraft.class_3518.method_15281(json, "position", new JsonObject());
        net.minecraft.class_2096.class_2099 x = net.minecraft.class_2096.class_2099.method_9051(position.get("x"));
        net.minecraft.class_2096.class_2099 y = net.minecraft.class_2096.class_2099.method_9051(position.get("y"));
        net.minecraft.class_2096.class_2099 z = net.minecraft.class_2096.class_2099.method_9051(position.get("z"));
        net.minecraft.class_5321<net.minecraft.class_1937> dimension = json.has("dimension")
            ? net.minecraft.class_2960.field_25139.parse(JsonOps.INSTANCE, json.get("dimension")).resultOrPartial(LOGGER::error)
                .map(id -> net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41223, id)).orElse(null)
            : null;
        net.minecraft.class_5321<net.minecraft.class_3195> structure = json.has("structure")
            ? net.minecraft.class_2960.field_25139.parse(JsonOps.INSTANCE, json.get("structure")).resultOrPartial(LOGGER::error)
                .map(id -> net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41246, id)).orElse(null)
            : null;
        net.minecraft.class_5321<net.minecraft.class_1959> biome = null;
        if (json.has("biome")) {
            net.minecraft.class_2960 id = new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, "biome"));
            biome = net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41236, id);
        }
        Boolean smokey = json.has("smokey") ? json.get("smokey").getAsBoolean() : null;
        net.minecraft.class_4552 light = lightFromJson(json.get("light"));
        net.minecraft.class_4550 block = blockFromJson(json.get("block"));
        net.minecraft.class_4551 fluid = fluidFromJson(json.get("fluid"));
        return CbAdvRecords.locationPredicate(x, y, z, biome, structure, dimension, smokey, light, block, fluid);
    }

    /** copy of CbAdvBlockPredicateJson.method_22453: 1.20.1 BlockPredicate.fromJson(json) */
    public static net.minecraft.class_4550 blockFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_20692;
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "block");
        net.minecraft.class_2105 nbt = nbtFromJson(json.get("nbt"));
        Set<net.minecraft.class_2248> blocks = null;
        JsonArray array = net.minecraft.class_3518.method_15292(json, "blocks", null);
        if (array != null) {
            ImmutableSet.Builder<net.minecraft.class_2248> builder = ImmutableSet.builder();
            for (JsonElement e : array) {
                net.minecraft.class_2960 id = new net.minecraft.class_2960(net.minecraft.class_3518.method_15287(e, "block"));
                builder.add(net.minecraft.class_7923.field_41175.method_17966(id)
                    .orElseThrow(() -> new JsonSyntaxException("Unknown block id '" + id + "'")));
            }
            blocks = builder.build();
        }
        net.minecraft.class_6862<net.minecraft.class_2248> tag = null;
        if (json.has("tag")) {
            net.minecraft.class_2960 id = new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, "tag"));
            tag = net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41254, id);
        }
        net.minecraft.class_4559 state = stateFromJson(json.get("state"));
        return CbAdvRecords.blockPredicate(tag, blocks, state, nbt);
    }

    /** copy of CbAdvFluidPredicateJson.method_22474: 1.20.1 FluidPredicate.fromJson(json) */
    public static net.minecraft.class_4551 fluidFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_20708;
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "fluid");
        net.minecraft.class_3611 fluid = null;
        if (json.has("fluid")) {
            net.minecraft.class_2960 id = new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, "fluid"));
            fluid = net.minecraft.class_7923.field_41173.method_10223(id);
        }
        net.minecraft.class_6862<net.minecraft.class_3611> tag = null;
        if (json.has("tag")) {
            net.minecraft.class_2960 id = new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, "tag"));
            tag = net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41270, id);
        }
        net.minecraft.class_4559 state = stateFromJson(json.get("state"));
        return CbAdvRecords.fluidPredicate(tag, fluid, state);
    }

    /** copy of CbAdvLightPredicateJson.method_22482: 1.20.1 LightPredicate.fromJson(json) */
    public static net.minecraft.class_4552 lightFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_20712;
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "light");
        return new net.minecraft.class_4552(net.minecraft.class_2096.class_2100.method_9056(json.get("light")));
    }

    /** copy of CbAdvStatePropertiesJson.method_22519: 1.20.1 StatePropertiesPredicate.fromJson(json) */
    public static net.minecraft.class_4559 stateFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_20736;
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "properties");
        List<net.minecraft.class_4559.class_4562> matchers = com.google.common.collect.Lists.newArrayList();
        for (Map.Entry<String, JsonElement> e : json.entrySet()) {
            matchers.add(stateMatcherFromJson(e.getKey(), e.getValue()));
        }
        return new net.minecraft.class_4559(matchers);
    }

    /** copy of CbAdvStatePropertiesJson.method_22521: 1.20.1 StatePropertiesPredicate.fromJson(name, json) */
    public static net.minecraft.class_4559.class_4562 stateMatcherFromJson(String name, JsonElement element) {
        if (element.isJsonPrimitive()) {
            return CbAdvRecords.exactProperty(name, element.getAsString());
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "value");
        String min = json.has("min") ? stringOrNull(json.get("min")) : null;
        String max = json.has("max") ? stringOrNull(json.get("max")) : null;
        return min != null && min.equals(max) ? CbAdvRecords.exactProperty(name, min) : CbAdvRecords.rangedProperty(name, min, max);
    }

    /** copy of CbAdvStatePropertiesJson.method_22522: 1.20.1 StatePropertiesPredicate.getStringOrNull(json) */
    public static String stringOrNull(JsonElement element) {
        return element.isJsonNull() ? null : element.getAsString();
    }

    /** copy of CbAdvEntityPredicateJson.method_8913: 1.20.1 EntityPredicate.fromJson(json) */
    public static net.minecraft.class_2048 entityFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9599;
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "entity");
        net.minecraft.class_2050 type = entityTypeFromJson(json.get("type"));
        net.minecraft.class_2025 distance = distanceFromJson(json.get("distance"));
        net.minecraft.class_2090 location = locationFromJson(json.get("location"));
        net.minecraft.class_2090 steppingOn = locationFromJson(json.get("stepping_on"));
        net.minecraft.class_2102 effects = effectsFromJson(json.get("effects"));
        net.minecraft.class_2105 nbt = nbtFromJson(json.get("nbt"));
        net.minecraft.class_2040 flags = flagsFromJson(json.get("flags"));
        net.minecraft.class_3735 equipment = equipmentFromJson(json.get("equipment"));
        net.minecraft.class_7376 typeSpecific = CbAdvJson.method_43097(json.get("type_specific"));
        net.minecraft.class_2048 vehicle = entityFromJson(json.get("vehicle"));
        net.minecraft.class_2048 passenger = entityFromJson(json.get("passenger"));
        net.minecraft.class_2048 target = entityFromJson(json.get("targeted_entity"));
        String team = net.minecraft.class_3518.method_15253(json, "team", null);
        return CbAdvRecords.entityPredicate(type, distance, location, steppingOn, effects, nbt, flags, equipment, typeSpecific,
            vehicle, passenger, target, team);
    }

    /** copy of CbAdvEntityPredicateJson.method_51705: 1.20.1 EntityPredicate.fromJson(json, name, context) */
    public static net.minecraft.class_5258 entityContextFromJson(JsonObject json, String name, net.minecraft.class_5257 context) {
        return entityFromElement(name, context, json.get(name));
    }

    /** copy of CbAdvEntityPredicateJson.method_51707: 1.20.1 EntityPredicate.fromJsonArray(json, name, context) */
    public static net.minecraft.class_5258[] entityContextsFromJson(JsonObject json, String name, net.minecraft.class_5257 context) {
        JsonElement element = json.get(name);
        if (element == null || element.isJsonNull()) {
            return new net.minecraft.class_5258[0];
        }
        JsonArray array = net.minecraft.class_3518.method_15252(element, name);
        net.minecraft.class_5258[] out = new net.minecraft.class_5258[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            out[i] = entityFromElement(name + "[" + i + "]", context, array.get(i));
        }
        return out;
    }

    /** copy of CbAdvEntityPredicateJson.method_51706: 1.20.1 EntityPredicate.fromElement(name, context, json) */
    public static net.minecraft.class_5258 entityFromElement(String name, net.minecraft.class_5257 context, JsonElement json) {
        net.minecraft.class_5258 conditions = contextFromElement(name, context, json, net.minecraft.class_173.field_24423);
        if (conditions != null) {
            return conditions;
        }
        return CbAdvAny.method_51704(entityFromJson(json));
    }

    /** copy of CbAdvContextPredicateJson.method_27807: 1.20.1 ContextAwarePredicate.fromElement(name, context, json, params) */
    public static net.minecraft.class_5258 contextFromElement(String name, net.minecraft.class_5257 context, JsonElement json,
            net.minecraft.class_176 params) {
        if (json != null && json.isJsonArray()) {
            List<net.minecraft.class_5341> conditions = context.method_27796(json.getAsJsonArray(), context.method_27795() + "/" + name, params);
            return new net.minecraft.class_5258(conditions);
        }
        return null;
    }

    /** copy of CbAdvEntityTypePredicateJson.method_8928: 1.20.1 EntityTypePredicate.fromJson(json) */
    public static net.minecraft.class_2050 entityTypeFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9609;
        }
        String type = net.minecraft.class_3518.method_15287(element, "type");
        if (type.startsWith("#")) {
            net.minecraft.class_2960 id = new net.minecraft.class_2960(type.substring(1));
            return net.minecraft.class_2050.method_8926(net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41266, id));
        }
        net.minecraft.class_2960 id = new net.minecraft.class_2960(type);
        net.minecraft.class_1299<?> entityType = net.minecraft.class_7923.field_41177.method_17966(id).orElseThrow(() -> new JsonSyntaxException(
            "Unknown entity type '" + id + "', valid types are: "
                + com.google.common.base.Joiner.on(",").join(net.minecraft.class_7923.field_41177.method_10235())));
        return net.minecraft.class_2050.method_8929(entityType);
    }

    /** copy of CbAdvDistancePredicateJson.method_8857: 1.20.1 DistancePredicate.fromJson(json) */
    public static net.minecraft.class_2025 distanceFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9553;
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "distance");
        net.minecraft.class_2096.class_2099 x = net.minecraft.class_2096.class_2099.method_9051(json.get("x"));
        net.minecraft.class_2096.class_2099 y = net.minecraft.class_2096.class_2099.method_9051(json.get("y"));
        net.minecraft.class_2096.class_2099 z = net.minecraft.class_2096.class_2099.method_9051(json.get("z"));
        net.minecraft.class_2096.class_2099 horizontal = net.minecraft.class_2096.class_2099.method_9051(json.get("horizontal"));
        net.minecraft.class_2096.class_2099 absolute = net.minecraft.class_2096.class_2099.method_9051(json.get("absolute"));
        return new net.minecraft.class_2025(x, y, z, horizontal, absolute);
    }

    /** copy of CbAdvMobEffectsPredicateJson.method_9064: 1.20.1 MobEffectsPredicate.fromJson(json) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_2102 effectsFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9709;
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "effects");
        Map<net.minecraft.class_1291, net.minecraft.class_2102.class_2103> map = com.google.common.collect.Maps.newLinkedHashMap();
        for (Map.Entry<String, JsonElement> e : json.entrySet()) {
            net.minecraft.class_2960 id = new net.minecraft.class_2960(e.getKey());
            net.minecraft.class_1291 effect = net.minecraft.class_7923.field_41174.method_17966(id)
                .orElseThrow(() -> new JsonSyntaxException("Unknown effect '" + id + "'"));
            map.put(effect, effectInstanceFromJson(net.minecraft.class_3518.method_15295(e.getValue(), e.getKey())));
        }
        return new net.minecraft.class_2102((Map) CbAdvRecords.effectMap(map));
    }

    /** copy of CbAdvMobEffectInstanceJson.method_9070: 1.20.1 MobEffectInstancePredicate.fromJson(json) */
    public static net.minecraft.class_2102.class_2103 effectInstanceFromJson(JsonObject json) {
        net.minecraft.class_2096.class_2100 amplifier = net.minecraft.class_2096.class_2100.method_9056(json.get("amplifier"));
        net.minecraft.class_2096.class_2100 duration = net.minecraft.class_2096.class_2100.method_9056(json.get("duration"));
        Boolean ambient = json.has("ambient") ? net.minecraft.class_3518.method_15270(json, "ambient") : null;
        Boolean visible = json.has("visible") ? net.minecraft.class_3518.method_15270(json, "visible") : null;
        return new net.minecraft.class_2102.class_2103(amplifier, duration, CbAdvRecords.bool(ambient), CbAdvRecords.bool(visible));
    }

    /** copy of CbAdvEntityFlagsPredicateJson.method_8893: 1.20.1 EntityFlagsPredicate.fromJson(json) */
    public static net.minecraft.class_2040 flagsFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9581;
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "entity flags");
        Boolean onFire = optionalBoolean(json, "is_on_fire");
        Boolean sneaking = optionalBoolean(json, "is_sneaking");
        Boolean sprinting = optionalBoolean(json, "is_sprinting");
        Boolean swimming = optionalBoolean(json, "is_swimming");
        Boolean baby = optionalBoolean(json, "is_baby");
        return new net.minecraft.class_2040(CbAdvRecords.bool(onFire), CbAdvRecords.bool(sneaking), CbAdvRecords.bool(sprinting),
            CbAdvRecords.bool(swimming), CbAdvRecords.bool(baby));
    }

    /** copy of CbAdvEntityFlagsPredicateJson.method_8895: 1.20.1 EntityFlagsPredicate.getOptionalBoolean(json, name) */
    public static Boolean optionalBoolean(JsonObject json, String name) {
        return json.has(name) ? net.minecraft.class_3518.method_15270(json, name) : null;
    }

    /** copy of CbAdvEquipmentPredicateJson.method_16224: 1.20.1 EntityEquipmentPredicate.fromJson(json) */
    public static net.minecraft.class_3735 equipmentFromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_16485;
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "equipment");
        net.minecraft.class_2073 head = itemFromJson(json.get("head"));
        net.minecraft.class_2073 chest = itemFromJson(json.get("chest"));
        net.minecraft.class_2073 legs = itemFromJson(json.get("legs"));
        net.minecraft.class_2073 feet = itemFromJson(json.get("feet"));
        net.minecraft.class_2073 mainhand = itemFromJson(json.get("mainhand"));
        net.minecraft.class_2073 offhand = itemFromJson(json.get("offhand"));
        return new net.minecraft.class_3735(CbAdvAny.item(head), CbAdvAny.item(chest), CbAdvAny.item(legs), CbAdvAny.item(feet),
            CbAdvAny.item(mainhand), CbAdvAny.item(offhand));
    }
}
