package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import top.dext.centurybridge.step.v1_20_3.facade.DeserializationContext;

/**
 * The 1.20.2 criterion API (CriterionTrigger, CriterionTriggerInstance, SimpleCriterionTrigger,
 * Criterion, CriteriaTriggers) on 1.20.3's codec-based one.
 *
 * A 1.20.2 trigger defines its conditions by two methods: its createInstance reads them from JSON,
 * its instances' serializeToJson writes them back. 1.20.3 asks a trigger for codec() instead, and
 * an instance for validate(validator). CenturyBridge synthesizes codec() into a 1.20.2 trigger
 * class (adapterHooks) as a codec made of exactly those two methods ({@link #legacyCodec}), and
 * validate into a 1.20.2 instance class; the rest of 1.20.2's API -- the JSON entry points, the
 * id-keyed trigger table -- is bridged here over 1.20.3's registry and codecs.
 */
public final class CbAdvCriteria {

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    // ---------------------------------------------------------------- codecs made of 1.20.2 methods

    /**
     * A codec whose decoding is a 1.20.2 createInstance (handed the conditions object; 1.20.3
     * hands an absent "conditions" as an empty object, as 1.20.2's criterionFromJson did) and
     * whose encoding is the instance's 1.20.2 serializeToJson ({@link #instanceJson}). What the
     * createInstance throws propagates, as it did out of 1.20.2's advancement loading (which
     * logged it as a parsing error, as 1.20.3's does).
     */
    public static <T extends net.minecraft.class_184> Codec<T> legacyCodec(Function<JsonObject, T> decode) {
        return net.minecraft.class_5699.field_40721.flatXmap(
            json -> json.isJsonObject()
                ? DataResult.success(decode.apply(json.getAsJsonObject()))
                : DataResult.error(() -> "Criterion conditions must be a JSON object: " + json),
            instance -> DataResult.success(instanceJson(instance)));
    }

    /**
     * codec() synthesized into a 1.20.2 SimpleCriterionTrigger subclass: 1.20.2's final
     * createInstance(json, context) -- the "player" predicate read through the context, then the
     * subclass's own createInstance(json, player, context).
     */
    public static Codec<net.minecraft.class_4558.class_8788> simpleCodec(CbAdvLegacy.SimpleTrigger trigger) {
        return legacyCodec(json -> {
            DeserializationContext context = CbAdvContext.current();
            return trigger.method_27854(json, CbAdvPredicates.method_51705(json, "player", context), context);
        });
    }

    /** codec() synthesized into a 1.20.2 CriterionTrigger implementation: its createInstance(json, context) */
    public static Codec<net.minecraft.class_184> triggerCodec(CbAdvLegacy.Trigger trigger) {
        return legacyCodec(json -> trigger.method_795(json, CbAdvContext.current()));
    }

    /**
     * validate(validator) synthesized into a 1.20.2 trigger instance class (adapterHooks, guarded):
     * whatever the class's superclass does -- calling validate again re-enters the synthesized
     * method, which the guard sends on to super. A subclass of the AbstractCriterionTriggerInstance
     * facade gets the facade's (nothing: a 1.20.2 instance is validated while it is deserialized),
     * a subclass of a vanilla 1.20.3 instance vanilla's validation of the parts it inherits, a class
     * implementing the interface directly nothing (1.20.2 had no validation pass for it).
     */
    public static void validateLegacy(CbAdvLegacy.Instance instance, net.minecraft.class_8944 validator) {
        ((net.minecraft.class_184) instance).method_54938(validator);
    }

    // ---------------------------------------------------------------- CriterionTriggerInstance (class_184)

    /**
     * A 1.20.2 lambda (or method reference) implementing CriterionTriggerInstance, whose single
     * method was serializeToJson (lambdaAdapters: the converter builds it as CbAdvLegacy.Instance and
     * passes it here): an instance answering that method with the lambda's, and validate -- new in
     * 1.20.3, which such an instance never had -- with nothing.
     */
    public static net.minecraft.class_184 fromLambda(CbAdvLegacy.Instance lambda) {
        return new LambdaInstance(lambda);
    }

    /** see {@link #fromLambda} */
    public static final class LambdaInstance implements net.minecraft.class_184, CbAdvLegacy.Instance {
        private final CbAdvLegacy.Instance lambda;

        LambdaInstance(CbAdvLegacy.Instance lambda) {
            this.lambda = lambda;
        }

        @Override
        public JsonObject method_807() {
            return lambda.method_807();
        }

        @Override
        public void method_54938(net.minecraft.class_8944 validator) {
        }
    }

    /** CriterionTriggerInstance.serializeToJson() */
    public static JsonObject serializeToJson(net.minecraft.class_184 instance) {
        return instanceJson(instance);
    }

    /**
     * An instance's 1.20.2 JSON: a 1.20.2 instance's own serializeToJson (virtual, as 1.20.2
     * called it), a vanilla 1.20.3 instance's codec. An instance of neither kind (a 1.20.3 mod's)
     * has no form this bridge could know.
     */
    static JsonObject instanceJson(net.minecraft.class_184 instance) {
        if (instance instanceof CbAdvLegacy.Instance legacy) {
            return legacy.method_807();
        }
        JsonElement json = CbAdvTriggers.vanillaJson(instance);
        if (json == null) {
            throw new IllegalArgumentException("criterion instance " + instance.getClass().getName()
                + " is neither a 1.20.2 instance nor vanilla's: its JSON form is its own trigger's codec");
        }
        return json.getAsJsonObject();
    }

    // ---------------------------------------------------------------- SimpleCriterionTrigger.SimpleInstance (class_8788)

    /** SimpleInstance.playerPredicate(): 1.20.3's player() */
    public static Optional<net.minecraft.class_5258> playerPredicate(net.minecraft.class_4558.class_8788 instance) {
        return instance.comp_2029();
    }

    // ---------------------------------------------------------------- CriterionTrigger / SimpleCriterionTrigger

    /**
     * CriterionTrigger.createInstance(json, context): a 1.20.2 implementation's own; otherwise the
     * trigger's codec (for a 1.20.2 SimpleCriterionTrigger subclass, the one synthesized from its
     * createInstance) decodes the conditions inside the caller's context. 1.20.2's
     * createInstance validated what it read against that context; the result is validated the
     * same way (a 1.20.2 instance's validate is nothing: its own reading validated).
     */
    public static net.minecraft.class_184 createInstance(net.minecraft.class_179<?> trigger, JsonObject json,
            DeserializationContext context) {
        net.minecraft.class_184 instance = trigger instanceof CbAdvLegacy.Trigger legacy
            ? legacy.method_795(json, context)
            : decode(trigger, json, context);
        validateExplicit(instance, context);
        return instance;
    }

    /** SimpleCriterionTrigger.createInstance(json, context) (final in 1.20.2) */
    public static net.minecraft.class_4558.class_8788 createSimpleInstance(net.minecraft.class_4558<?> trigger,
            JsonObject json, DeserializationContext context) {
        return (net.minecraft.class_4558.class_8788) createInstance(trigger, json, context);
    }

    private static <T extends net.minecraft.class_184> T decode(net.minecraft.class_179<T> trigger, JsonObject json,
            DeserializationContext context) {
        return CbAdvContext.with(context, () -> CbAdvPredicates.parse(trigger.method_54937(), json));
    }

    /**
     * What 1.20.2's createInstance did with the context besides reading: the conditions it read
     * validated against the context's loot data, each problem a warning naming the trigger. A
     * context without loot data (a decode outside an advancement load) has nothing to check against.
     */
    static void validateExplicit(net.minecraft.class_184 instance, DeserializationContext context) {
        if (context.field_44474 == null) {
            return;
        }
        net.minecraft.class_8942.class_8943 problems = new net.minecraft.class_8942.class_8943();
        instance.method_54938(new net.minecraft.class_8944(problems, context.field_44474));
        problems.method_54948().forEach((path, problem) -> LOGGER.warn(
            "Found validation problem in advancement trigger {}/{}: {}", context.method_27795(), path, problem));
    }

    // ---------------------------------------------------------------- Criterion (class_175)

    /** Criterion.criterionFromJson(json, context) */
    public static net.minecraft.class_175<?> criterionFromJson(JsonObject json, DeserializationContext context) {
        net.minecraft.class_2960 id = new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, "trigger"));
        net.minecraft.class_179<?> trigger = getCriterion(id);
        if (trigger == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + id);
        }
        return criterion(json, context, trigger);
    }

    private static <T extends net.minecraft.class_184> net.minecraft.class_175<T> criterion(JsonObject json,
            DeserializationContext context, net.minecraft.class_179<T> trigger) {
        @SuppressWarnings("unchecked")
        T instance = (T) createInstance(trigger, net.minecraft.class_3518.method_15281(json, "conditions", new JsonObject()), context);
        return new net.minecraft.class_175<>(trigger, instance);
    }

    /** Criterion.criteriaFromJson(json, context): each member a criterion object */
    public static Map<String, net.minecraft.class_175<?>> criteriaFromJson(JsonObject json, DeserializationContext context) {
        Map<String, net.minecraft.class_175<?>> out = new HashMap<>();
        for (Map.Entry<String, JsonElement> e : json.entrySet()) {
            out.put(e.getKey(), criterionFromJson(net.minecraft.class_3518.method_15295(e.getValue(), "criterion"), context));
        }
        return out;
    }

    /** Criterion.serializeToJson(): the trigger's id, and the instance's JSON under "conditions" unless empty */
    public static JsonElement criterionToJson(net.minecraft.class_175<?> criterion) {
        JsonObject json = new JsonObject();
        net.minecraft.class_2960 id = java.util.Objects.requireNonNull(getId(criterion.comp_1923()), "Unregistered trigger");
        json.addProperty("trigger", id.toString());
        JsonObject conditions = instanceJson(criterion.comp_1924());
        if (conditions.size() != 0) {
            json.add("conditions", conditions);
        }
        return json;
    }

    /**
     * The codec 1.20.3 builds for a trigger's criteria (Criterion.criterionCodec), with 1.20.2's
     * encoding where an instance still speaks it: 1.20.2 wrote a criterion's conditions with the
     * instance's serializeToJson, a virtual call -- an instance class that overrides it (a mod's
     * subclass of a vanilla instance) wrote its own form, which 1.20.3's per-trigger codec never
     * asks for. Decoding is the trigger's codec, unchanged.
     */
    public static <T extends net.minecraft.class_184> Codec<net.minecraft.class_175<T>> criterionCodec(
            Codec<net.minecraft.class_175<T>> codec) {
        return Codec.of(new com.mojang.serialization.Encoder<net.minecraft.class_175<T>>() {
            @Override
            public <O> DataResult<O> encode(net.minecraft.class_175<T> criterion, DynamicOps<O> ops, O prefix) {
                if (criterion.comp_1924() instanceof CbAdvLegacy.Instance legacy) {
                    return net.minecraft.class_5699.field_40721.encode(legacy.method_807(), ops, prefix);
                }
                return codec.encode(criterion, ops, prefix);
            }
        }, new com.mojang.serialization.Decoder<net.minecraft.class_175<T>>() {
            @Override
            public <I> DataResult<Pair<net.minecraft.class_175<T>, I>> decode(DynamicOps<I> ops, I input) {
                return codec.decode(ops, input);
            }
        });
    }

    // ---------------------------------------------------------------- CriteriaTriggers (class_174)

    /** CriteriaTriggers.getCriterion(id): the trigger registered under the id, or null */
    @SuppressWarnings("unchecked")
    public static <T extends net.minecraft.class_184> net.minecraft.class_179<T> getCriterion(net.minecraft.class_2960 id) {
        return (net.minecraft.class_179<T>) net.minecraft.class_7923.field_47496.method_10223(id);
    }

    /** CriteriaTriggers.getId(trigger): the id it is registered under, or null */
    public static net.minecraft.class_2960 getId(net.minecraft.class_179<?> trigger) {
        return net.minecraft.class_7923.field_47496.method_10221(trigger);
    }

    /** CriteriaTriggers.all(): every registered trigger */
    public static Iterable<? extends net.minecraft.class_179<?>> all() {
        return net.minecraft.class_7923.field_47496;
    }

    private CbAdvCriteria() {
    }
}
