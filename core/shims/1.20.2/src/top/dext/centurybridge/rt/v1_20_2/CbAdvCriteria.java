package top.dext.centurybridge.rt.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 1.20.1's Criterion, CriterionTrigger.Listener, ContextAwarePredicate and DeserializationContext
 * API on 1.20.2.
 *
 * A 1.20.1 Criterion held a trigger instance only; the instance named its trigger by id
 * (getCriterion), and 1.20.1 looked the trigger up whenever it needed one. A 1.20.2 Criterion is the
 * pair. A bare 1.20.1 instance becomes the pair of itself and the trigger its id names
 * ({@link #criterionOf}), exactly the trigger 1.20.1's PlayerAdvancements registered its listener
 * with; a pair 1.20.1 code took apart (getTrigger, a factory's answer) is remembered by its instance
 * and comes back whole. A 1.20.1 client's criteria carried no instance at all (the network sent only
 * the names): such a criterion is the pair of {@link #MISSING} -- a trigger no listener is ever
 * registered with, as 1.20.1 registered none for a criterion without an instance -- and null.
 */
public final class CbAdvCriteria {

    /** criteria this layer took apart, by their instance: putting one back together is exact */
    private static final Map<Object, net.minecraft.class_175<?>> CRITERIA =
        Collections.synchronizedMap(new IdentityHashMap<>());

    /**
     * The trigger of a 1.20.1 criterion without an instance (the client's, and new Criterion()):
     * registered nowhere, takes no listeners and decodes nothing.
     */
    public static final net.minecraft.class_179<net.minecraft.class_184> MISSING = new net.minecraft.class_179<>() {
        @Override
        public void method_792(net.minecraft.class_2985 advancements, net.minecraft.class_179.class_180<net.minecraft.class_184> l) {
        }

        @Override
        public void method_793(net.minecraft.class_2985 advancements, net.minecraft.class_179.class_180<net.minecraft.class_184> l) {
        }

        @Override
        public void method_791(net.minecraft.class_2985 advancements) {
        }

        @Override
        public net.minecraft.class_184 method_795(JsonObject json, net.minecraft.class_5257 context) {
            throw new com.google.gson.JsonSyntaxException("Missing trigger");
        }

        @Override
        public String toString() {
            return "CenturyBridge 1.20.1 criterion without a trigger instance";
        }
    };

    // ------------------------------------------------------------------ Criterion

    /** a 1.20.2 criterion handed to 1.20.1 code as its bare instance; remembered for the way back */
    public static net.minecraft.class_184 instanceOf(net.minecraft.class_175<?> criterion) {
        if (criterion == null) {
            return null;
        }
        net.minecraft.class_184 instance = criterion.comp_1924();
        if (instance != null) {
            CRITERIA.put(instance, criterion);
            CbAdvInstances.belongs(instance, criterion.comp_1923());
        }
        return instance;
    }

    /**
     * 1.20.1 criterion conditions as the criterion 1.20.2 wants: one this layer took apart comes
     * back whole; any other is paired with the trigger its id names (CbAdvInstances.triggerOf).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_175<?> criterionOf(net.minecraft.class_184 instance) {
        if (instance == null) {
            return null;
        }
        net.minecraft.class_175<?> known = CRITERIA.get(instance);
        if (known != null) {
            return known;
        }
        return new net.minecraft.class_175(CbAdvInstances.triggerOf(instance), instance);
    }

    /** new Criterion(instance) */
    public static net.minecraft.class_175<?> cbNewCriterion(net.minecraft.class_184 instance) {
        return instance == null ? missing() : criterionOf(instance);
    }

    /** new Criterion(): a criterion without an instance (1.20.1's client side) */
    public static net.minecraft.class_175<?> cbNewCriterion() {
        return missing();
    }

    private static net.minecraft.class_175<?> missing() {
        return new net.minecraft.class_175<>(MISSING, null);
    }

    /** super(instance) of a 1.20.1 Criterion subclass: the trigger the instance names (superCtorReshapes conv) */
    public static net.minecraft.class_179<?> triggerFor(net.minecraft.class_184 instance) {
        return instance == null ? MISSING : CbAdvInstances.triggerOf(instance);
    }

    /** super() of a 1.20.1 Criterion subclass: no trigger */
    public static net.minecraft.class_179<?> missingTrigger() {
        return MISSING;
    }

    /** super() of a 1.20.1 Criterion subclass: no instance */
    public static net.minecraft.class_184 noInstance() {
        return null;
    }

    /** Criterion.getTrigger(): the instance, dispatched as 1.20.1 did (a subclass's own override first) */
    public static net.minecraft.class_184 method_774(net.minecraft.class_175<?> self) {
        if ((Object) self instanceof CbAdvLegacy.CriterionInstance own) {
            return own.method_774();
        }
        return instanceOf(self);
    }

    /** super.getTrigger() from a 1.20.1 Criterion subclass: the instance field (not the overridden accessor) */
    public static net.minecraft.class_184 superGetTrigger(net.minecraft.class_175<?> self) {
        top.dext.centurybridge.shims.v1_20_2.CbAdvCriterionAccess fields =
            (top.dext.centurybridge.shims.v1_20_2.CbAdvCriterionAccess) (Object) self;
        net.minecraft.class_184 instance = fields.cb$instance();
        if (instance != null) {
            CRITERIA.put(instance, self);
            CbAdvInstances.belongs(instance, fields.cb$trigger());
        }
        return instance;
    }

    /**
     * triggerInstance() synthesized into a 1.20.1 Criterion subclass that overrides getTrigger()
     * (adapterHooks): what 1.20.2 reads where 1.20.1 read getTrigger() -- the subclass's answer.
     */
    public static net.minecraft.class_184 legacyInstance(CbAdvLegacy.CriterionInstance self) {
        return self.method_774();
    }

    /** super.serializeToNetwork(buf) from a 1.20.1 Criterion subclass: 1.20.1's body wrote nothing */
    public static void superSerializeToNetwork(net.minecraft.class_175<?> self, net.minecraft.class_2540 buf) {
        // Criterion.serializeToNetwork(FriendlyByteBuf) was an empty method in 1.20.1: the network
        // carried criterion names only
    }

    /** Criterion.serializeToNetwork(buf): 1.20.1 wrote nothing for a criterion (a subclass may) */
    public static void method_771(net.minecraft.class_175<?> self, net.minecraft.class_2540 buf) {
        if ((Object) self instanceof CbAdvLegacy.CriterionNetwork own) {
            own.method_771(buf);
        }
    }

    /** Criterion.criterionFromNetwork(buf): 1.20.1 read nothing -- a criterion without an instance */
    public static net.minecraft.class_175<?> method_769(net.minecraft.class_2540 buf) {
        return missing();
    }

    /** Criterion.criteriaFromNetwork(buf): the names, each a criterion without an instance */
    public static Map<String, net.minecraft.class_175<?>> method_768(net.minecraft.class_2540 buf) {
        return buf.method_34067(b -> b.method_19772(), CbAdvCriteria::method_769);
    }

    /** Criterion.serializeToNetwork(criteria, buf): the names, then what each criterion writes (nothing in 1.20.1) */
    public static void method_775(Map<String, net.minecraft.class_175<?>> criteria, net.minecraft.class_2540 buf) {
        buf.method_34063(criteria, (b, name) -> b.method_10814(name), (b, c) -> method_771(c, b));
    }

    // ------------------------------------------------------------------ CriterionTrigger.Listener

    /** new Listener(instance, advancement, criterion): 1.20.2's names the advancement by its holder */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_179.class_180<?> cbNewListener(net.minecraft.class_184 instance,
            net.minecraft.class_161 advancement, String criterion) {
        return new net.minecraft.class_179.class_180(instance, CbAdvancements.holderOf(advancement), criterion);
    }

    /** the advancement argument of a 1.20.1 Listener constructor, as the holder 1.20.2's takes (superCtorReshapes conv) */
    public static net.minecraft.class_8779 listenerAdvancement(net.minecraft.class_161 advancement) {
        return CbAdvancements.holderOf(advancement);
    }

    /** Listener.getTriggerInstance(), dispatched as 1.20.1 did (a subclass's own override first) */
    public static net.minecraft.class_184 method_797(net.minecraft.class_179.class_180<?> self) {
        if ((Object) self instanceof CbAdvLegacy.ListenerInstance own) {
            return own.method_797();
        }
        return self.comp_1925();
    }

    /** super.getTriggerInstance() from a 1.20.1 Listener subclass: the instance field (not the overridden accessor) */
    public static net.minecraft.class_184 superGetTriggerInstance(net.minecraft.class_179.class_180<?> self) {
        return ((top.dext.centurybridge.shims.v1_20_2.CbAdvListenerAccess) (Object) self).cb$instance();
    }

    /**
     * trigger() synthesized into a 1.20.1 Listener subclass that overrides getTriggerInstance()
     * (adapterHooks): what 1.20.2's SimpleCriterionTrigger reads where 1.20.1's read it.
     */
    public static net.minecraft.class_184 legacyListenerInstance(CbAdvLegacy.ListenerInstance self) {
        return self.method_797();
    }

    // ------------------------------------------------------------------ ContextAwarePredicate

    /**
     * ContextAwarePredicate.fromElement(key, context, json, params), 1.20.1: null unless the JSON is
     * an array of loot conditions; an empty array matches anything (the ANY predicate).
     */
    public static net.minecraft.class_5258 method_27807(String key, net.minecraft.class_5257 context, JsonElement json,
            net.minecraft.class_176 params) {
        Optional<Optional<net.minecraft.class_5258>> read = net.minecraft.class_5258.method_27807(key, context, json, params);
        return read.isEmpty() ? null : CbAdvAny.contextOrAny(read.get());
    }

    /** ContextAwarePredicate.toJson(predicates, context): JsonNull for none, else each (ANY as JsonNull) */
    public static JsonElement method_27808(net.minecraft.class_5258[] predicates,
            top.dext.centurybridge.facade.v1_20_2.class_5267 context) {
        return net.minecraft.class_5258.method_27808(CbAdvAny.contexts(predicates));
    }

    // ------------------------------------------------------------------ EntityPredicate's player-predicate readers

    /**
     * EntityPredicate.fromJson(json, key, context) for one element, 1.20.1: a loot-condition array
     * (an empty one matches anything), else an entity predicate wrapped (absent: ANY).
     */
    public static net.minecraft.class_5258 contextFromJson(String key, net.minecraft.class_5257 context, JsonElement json) {
        Optional<Optional<net.minecraft.class_5258>> conditions = net.minecraft.class_5258.method_27807(key, context, json,
            net.minecraft.class_173.field_24423);
        if (conditions.isPresent()) {
            return CbAdvAny.contextOrAny(conditions.get());
        }
        return CbAdvAny.method_51704(CbAdvAny.entityOrAny(net.minecraft.class_2048.method_8913(json)));
    }

    /**
     * EntityPredicate.fromJsonArray(json, key, context), 1.20.1: one player predicate per element
     * (an absent one is ANY, kept in its place), none when the member is missing or null.
     */
    public static net.minecraft.class_5258[] method_51707(JsonObject json, String key, net.minecraft.class_5257 context) {
        JsonElement e = json.get(key);
        if (e == null || e.isJsonNull()) {
            return new net.minecraft.class_5258[0];
        }
        com.google.gson.JsonArray array = net.minecraft.class_3518.method_15252(e, key);
        net.minecraft.class_5258[] out = new net.minecraft.class_5258[array.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = contextFromJson(key + "[" + i + "]", context, array.get(i));
        }
        return out;
    }

    // ------------------------------------------------------------------ DeserializationContext

    /** DeserializationContext.deserializeConditions: 1.20.1 answered the array */
    public static net.minecraft.class_5341[] conditionArray(List<net.minecraft.class_5341> conditions) {
        return conditions.toArray(new net.minecraft.class_5341[0]);
    }

    private CbAdvCriteria() {
    }
}
