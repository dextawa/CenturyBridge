package top.dext.centurybridge.rt.v1_20_2;

import com.google.common.collect.MapMaker;
import java.util.concurrent.ConcurrentMap;

/**
 * 1.20.1 criterion triggers on 1.20.2.
 *
 * A 1.20.1 trigger named itself: CriterionTrigger.getId(), which the vanilla triggers answered from
 * an id constant or, for the classes registered under several ids (KilledTrigger, PlayerTrigger,
 * DistanceTrigger, ItemUsedOnLocationTrigger, PickedUpItemTrigger), from their constructor
 * argument; CriteriaTriggers.register(trigger) filed it under that id. 1.20.2 triggers have no id --
 * the registry names them (CriteriaTriggers.getId). This keeps both answers:
 * <ul>
 * <li>a 1.20.1 trigger class that declares getId() is asked (CbLegacyCriterion, added by adapterHooks);</li>
 * <li>a trigger built through a 1.20.1 constructor keeps the id it was given: the reshaped call drops
 *     it into {@link #stash} and SimpleCriterionTrigger's constructor claims it as it returns
 *     (CbAdvTriggerIdClaim);</li>
 * <li>any other trigger answers with the id it is registered under.</li>
 * </ul>
 */
public final class CbAdvTriggers {

    private static final ConcurrentMap<Object, net.minecraft.class_2960> IDS = new MapMaker().weakKeys().makeMap();

    private static final ThreadLocal<net.minecraft.class_2960> PENDING = new ThreadLocal<>();

    /** the id argument of a 1.20.1 trigger constructor whose 1.20.2 shape has none (superCtorReshapes drop) */
    public static void stash(net.minecraft.class_2960 id) {
        PENDING.set(id);
    }

    /** called as SimpleCriterionTrigger's constructor returns: the trigger takes a stashed 1.20.1 id */
    public static void claim(Object trigger) {
        net.minecraft.class_2960 id = PENDING.get();
        if (id != null) {
            PENDING.remove();
            IDS.put(trigger, id);
        }
    }

    /**
     * What 1.20.1's vanilla getId() answered for the trigger (no override consulted): the id it was
     * constructed with, else the one it is registered under.
     */
    public static net.minecraft.class_2960 storedId(net.minecraft.class_179<?> trigger) {
        net.minecraft.class_2960 id = IDS.get(trigger);
        if (id != null) {
            return id;
        }
        id = net.minecraft.class_174.method_53697(trigger);
        if (id == null) {
            throw new IllegalStateException("CenturyBridge: criterion trigger " + trigger.getClass().getName()
                + " was neither built with a 1.20.1 id nor registered -- 1.20.2 triggers carry no id of their own");
        }
        return id;
    }

    /** CriterionTrigger.getId(), dispatched as 1.20.1 did: a trigger class declaring its own answers itself */
    public static net.minecraft.class_2960 idOf(net.minecraft.class_179<?> trigger) {
        if (trigger instanceof CbLegacyCriterion own) {
            return own.method_794();
        }
        return storedId(trigger);
    }

    /** CriterionTrigger.getId() (instanceRedirects) */
    public static net.minecraft.class_2960 method_794(net.minecraft.class_179<?> self) {
        return idOf(self);
    }

    /**
     * CriteriaTriggers.register(trigger), 1.20.1's shape (private in both releases; mods reached it
     * through an access widener or an invoker, Fabric API's CriterionRegistry through its own): filed
     * under the id the trigger names itself. A duplicate id throws, as in 1.20.1.
     */
    public static net.minecraft.class_179<?> method_767(net.minecraft.class_179<?> trigger) {
        return top.dext.centurybridge.shims.v1_20_2.CriteriaInvoker.cb$register(idOf(trigger).toString(), trigger);
    }

    /**
     * The player predicate a 1.20.1 createInstance override is handed where vanilla passes 1.20.2's
     * Optional (overrideHooks wrap): absent is ANY, as EntityPredicate.fromJson(json, "player",
     * context) answered in 1.20.1.
     */
    public static net.minecraft.class_5258 player(java.util.Optional<net.minecraft.class_5258> player) {
        return CbAdvAny.contextOrAny(player);
    }

    private CbAdvTriggers() {
    }
}
