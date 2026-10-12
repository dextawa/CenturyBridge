package top.dext.centurybridge.rt.v1_20_2;

import com.google.common.collect.MapMaker;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * 1.20.1 criterion trigger instances on 1.20.2.
 *
 * A 1.20.1 instance knew the id of the trigger it belonged to: AbstractCriterionTriggerInstance
 * took it as its first constructor argument and getCriterion() returned it; 1.20.1 found the
 * trigger by that id whenever it needed one (PlayerAdvancements registering a criterion's
 * listener, an advancement written to JSON). 1.20.2 instances know no trigger at all -- a Criterion
 * pairs the two. This keeps the id with the instance again, from every place a 1.20.1 world had it:
 * <ul>
 * <li>the id a 1.20.1 constructor call passed (super(id, player), new KilledTrigger.TriggerInstance(
 *     id, ...)): the reshaped call drops it into {@link #stash} right before the 1.20.2 constructor
 *     runs, and AbstractCriterionTriggerInstance's constructor claims it as it returns
 *     (CbAdvInstanceIdClaim);</li>
 * <li>the trigger that decoded an instance (SimpleCriterionTrigger.createInstance, the vanilla
 *     triggers registered under several ids: CbAdvDecodedBy*) or that a 1.20.2 Criterion pairs it
 *     with ({@link #belongs});</li>
 * <li>otherwise the one registered trigger whose instances are of the instance's class -- which is
 *     how 1.20.1's vanilla instances named the trigger: every class registered once carried its
 *     trigger's id as a constant; the classes registered under several ids took it as an argument.</li>
 * </ul>
 * Instances are keys by identity, held weakly.
 */
public final class CbAdvInstances {

    private static final ConcurrentMap<Object, net.minecraft.class_2960> IDS = new MapMaker().weakKeys().makeMap();
    private static final ConcurrentMap<Object, net.minecraft.class_179<?>> TRIGGERS = new MapMaker().weakKeys().makeMap();

    /** the id a 1.20.1 constructor call passed, waiting for the instance's base constructor to finish */
    private static final ThreadLocal<net.minecraft.class_2960> PENDING = new ThreadLocal<>();

    // ------------------------------------------------------------------ recording

    /** the id argument of a 1.20.1 instance constructor whose 1.20.2 shape has none (superCtorReshapes drop) */
    public static void stash(net.minecraft.class_2960 id) {
        PENDING.set(id);
    }

    /**
     * Called as AbstractCriterionTriggerInstance's constructor returns: the instance under
     * construction takes the stashed id, if a 1.20.1 constructor call left one. Between the stash
     * (just before the constructor invocation) and the base constructor's return only constructor
     * bodies run that assign fields.
     */
    public static void claim(Object instance) {
        net.minecraft.class_2960 id = PENDING.get();
        if (id != null) {
            PENDING.remove();
            IDS.put(instance, id);
        }
    }

    /** records the trigger an instance belongs to (it decoded it, or a criterion pairs them); returns the instance */
    public static <T> T belongs(T instance, net.minecraft.class_179<?> trigger) {
        if (instance != null && trigger != null) {
            TRIGGERS.put(instance, trigger);
        }
        return instance;
    }

    /** records that {@code instance} carries {@code id}, as a 1.20.1 constructor argument would have */
    public static <T> T withId(T instance, net.minecraft.class_2960 id) {
        if (instance != null && id != null) {
            IDS.put(instance, id);
        }
        return instance;
    }

    // ------------------------------------------------------------------ getCriterion

    /**
     * The vanilla instance classes whose 1.20.1 constructor took the trigger id as an argument --
     * their triggers are registered under several ids (KilledTrigger, PlayerTrigger, DistanceTrigger,
     * ItemUsedOnLocationTrigger, PickedUpItemTrigger). Every other vanilla class passed its trigger
     * class's ID constant (gen_s1adv.py reads the same split off the 1.20.1 constructors).
     */
    private static final java.util.Set<Class<?>> ID_ARGUMENT = java.util.Set.of(
        net.minecraft.class_2080.class_2083.class, net.minecraft.class_2135.class_2137.class,
        net.minecraft.class_2108.class_2110.class, net.minecraft.class_4711.class_4712.class,
        net.minecraft.class_5279.class_5280.class);

    /**
     * What 1.20.1's own getCriterion() answered for the instance (no override consulted):
     * <ul>
     * <li>the id a 1.20.1 constructor call gave it;</li>
     * <li>an instance of a class taking the id as an argument that 1.20.2 code built (decoded it, a
     *     factory): the id of the trigger that built it, as 1.20.1's createInstance passed this.id;</li>
     * <li>any other: its class's constant -- the id of the one trigger registered for the nearest
     *     vanilla class of the instance.</li>
     * </ul>
     */
    public static net.minecraft.class_2960 idOf(net.minecraft.class_184 instance) {
        net.minecraft.class_2960 id = IDS.get(instance);
        if (id != null) {
            return id;
        }
        for (Class<?> k = instance.getClass(); k != null && k != Object.class && k != net.minecraft.class_195.class;
                k = k.getSuperclass()) {
            if (ID_ARGUMENT.contains(k)) {
                net.minecraft.class_179<?> trigger = TRIGGERS.get(instance);
                if (trigger == null) {
                    throw new IllegalStateException("CenturyBridge: criterion instance " + instance.getClass().getName()
                        + " was built without its trigger id, which 1.20.1 always passed to " + k.getName());
                }
                return CbAdvTriggers.storedId(trigger);
            }
            List<net.minecraft.class_179<?>> found = triggersTaking(k);
            if (found.size() == 1) {
                return net.minecraft.class_174.method_53697(found.get(0));
            }
            if (found.size() > 1) {
                break;
            }
        }
        net.minecraft.class_179<?> trigger = TRIGGERS.get(instance);
        if (trigger != null) {
            return CbAdvTriggers.storedId(trigger);
        }
        throw new IllegalStateException("CenturyBridge: criterion instance " + instance.getClass().getName()
            + " names no trigger: it was not built with a 1.20.1 trigger id, and no single registered trigger takes"
            + " instances of its class");
    }

    /** CriterionTriggerInstance.getCriterion(), dispatched as 1.20.1 did: an instance declaring its own answers itself */
    public static net.minecraft.class_2960 method_806(net.minecraft.class_184 self) {
        if (self instanceof CbAdvLegacy.CriterionId own) {
            return own.method_806();
        }
        return idOf(self);
    }

    /** super.getCriterion() from a subclass of AbstractCriterionTriggerInstance: the id field */
    public static net.minecraft.class_2960 method_806(net.minecraft.class_195 self) {
        return idOf(self);
    }

    /** super.getCriterion() from a subclass of ImpossibleTrigger.TriggerInstance: ImpossibleTrigger.ID */
    public static net.minecraft.class_2960 method_806(net.minecraft.class_2062.class_2063 self) {
        return new net.minecraft.class_2960("impossible");
    }

    /**
     * The trigger an instance belongs to, as 1.20.1 found it: CriteriaTriggers.getCriterion(
     * instance.getCriterion()) (PlayerAdvancements' lookup) -- else the trigger it was recorded with,
     * for one no registry entry names (an unregistered trigger's instance in data generation).
     */
    public static net.minecraft.class_179<?> triggerOf(net.minecraft.class_184 instance) {
        net.minecraft.class_2960 id = method_806(instance);
        net.minecraft.class_179<?> trigger = net.minecraft.class_174.method_765(id);
        if (trigger == null) {
            trigger = TRIGGERS.get(instance);
        }
        if (trigger == null) {
            throw new IllegalStateException("CenturyBridge: no criterion trigger is registered under " + id
                + ", the id 1.20.1 criterion instance " + instance.getClass().getName() + " names");
        }
        return trigger;
    }

    /** the registered triggers whose instances are of exactly this class */
    private static List<net.minecraft.class_179<?>> triggersTaking(Class<?> instanceClass) {
        List<net.minecraft.class_179<?>> found = new ArrayList<>();
        for (net.minecraft.class_179<?> trigger : net.minecraft.class_174.method_766()) {
            if (instanceType(trigger.getClass()) == instanceClass) {
                found.add(trigger);
            }
        }
        return found;
    }

    /** the instance class a trigger class takes: SimpleCriterionTrigger&lt;T&gt; / CriterionTrigger&lt;T&gt;'s argument */
    static Class<?> instanceType(Class<?> c) {
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
            Class<?> t = argOf(k.getGenericSuperclass(), net.minecraft.class_4558.class);
            if (t != null) {
                return t;
            }
            for (Type i : k.getGenericInterfaces()) {
                t = argOf(i, net.minecraft.class_179.class);
                if (t != null) {
                    return t;
                }
            }
        }
        return null;
    }

    private static Class<?> argOf(Type t, Class<?> raw) {
        if (t instanceof ParameterizedType p && p.getRawType() == raw && p.getActualTypeArguments().length == 1) {
            Type a = p.getActualTypeArguments()[0];
            if (a instanceof Class<?> k) {
                return k;
            }
            if (a instanceof ParameterizedType pa && pa.getRawType() instanceof Class<?> k) {
                return k;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ the rest of 1.20.1's instance API

    /**
     * serializeToJson() synthesized into a 1.20.1 instance class that declares serializeToJson(
     * SerializationContext) (adapterHooks): its own method, handed the one context 1.20.1 ever passed.
     */
    public static com.google.gson.JsonObject serializeToJson(CbAdvLegacy.InstanceJson self) {
        return self.method_807(top.dext.centurybridge.facade.v1_20_2.class_5267.field_24420);
    }

    /**
     * The value of SimpleCriterionTrigger.createInstance where 1.20.1 code expects the
     * AbstractCriterionTriggerInstance it was declared to answer: every instance a 1.20.1 or vanilla
     * 1.20.2 trigger of that kind builds is one.
     */
    public static net.minecraft.class_195 asAbstract(net.minecraft.class_4558.class_8788 instance) {
        return (net.minecraft.class_195) instance;
    }

    private CbAdvInstances() {
    }
}
