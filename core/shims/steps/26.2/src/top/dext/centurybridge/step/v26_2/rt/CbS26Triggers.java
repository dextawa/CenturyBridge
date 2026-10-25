package top.dext.centurybridge.step.v26_2.rt;

import java.util.Map;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.advancements.triggers.ImpossibleTrigger;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.PlayerAdvancements;
import top.dext.centurybridge.step.v26_2.facade.CriterionTriggerListener;

/**
 * 26.1.2's trigger listener API on 26.2. 26.1.2's CriterionTrigger kept its own listeners
 * (addPlayerListener / removePlayerListener / removePlayerListeners, called by PlayerAdvancements); 26.2 keeps
 * them in PlayerAdvancements.activeTriggers (addListener / removeListener / clearTriggers) and the trigger has no
 * listener methods. A 26.1.2 call on a trigger lands in 26.2's map; a 26.1.2 trigger that keeps its own listeners
 * (it declares the three methods: {@link Legacy}, added by adapterHooks) is handed them by
 * mixin/CbS26PlayerAdvancementsMixin where 26.2's PlayerAdvancements registers, removes and clears them.
 */
public final class CbS26Triggers {
    private CbS26Triggers() {}

    /** a 26.1.2 trigger that declares the listener methods itself (adapterHooks adds this to it) */
    public interface Legacy {
        void addPlayerListener(PlayerAdvancements advancements, CriterionTriggerListener listener);

        void removePlayerListener(PlayerAdvancements advancements, CriterionTriggerListener listener);

        void removePlayerListeners(PlayerAdvancements advancements);
    }

    // ---- calls (26.1.2: interface / virtual calls on the trigger) ----

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addPlayerListener(CriterionTrigger trigger, PlayerAdvancements advancements, CriterionTriggerListener listener) {
        if (trigger instanceof Legacy l) {
            l.addPlayerListener(advancements, listener);
            return;
        }
        advancements.addListener(new Criterion(trigger, listener.trigger()),
            new PlayerAdvancements.TriggerInstanceKey(listener.advancement(), listener.criterion()));
    }

    @SuppressWarnings("rawtypes")
    public static void removePlayerListener(CriterionTrigger trigger, PlayerAdvancements advancements, CriterionTriggerListener listener) {
        if (trigger instanceof Legacy l) {
            l.removePlayerListener(advancements, listener);
            return;
        }
        advancements.removeListener(trigger, new PlayerAdvancements.TriggerInstanceKey(listener.advancement(), listener.criterion()));
    }

    @SuppressWarnings("rawtypes")
    public static void removePlayerListeners(CriterionTrigger trigger, PlayerAdvancements advancements) {
        if (trigger instanceof Legacy l) {
            l.removePlayerListeners(advancements);
            return;
        }
        // 26.1.2's SimpleCriterionTrigger.removePlayerListeners: players.remove(advancements)
        advancements.activeTriggers.remove(trigger);
    }

    // exact receivers of the open call sites (wiring-check matches the descriptor the converter emits)

    @SuppressWarnings("rawtypes")
    public static void addPlayerListener(SimpleCriterionTrigger trigger, PlayerAdvancements advancements, CriterionTriggerListener listener) {
        addPlayerListener((CriterionTrigger) trigger, advancements, listener);
    }

    @SuppressWarnings("rawtypes")
    public static void removePlayerListener(SimpleCriterionTrigger trigger, PlayerAdvancements advancements, CriterionTriggerListener listener) {
        removePlayerListener((CriterionTrigger) trigger, advancements, listener);
    }

    @SuppressWarnings("rawtypes")
    public static void removePlayerListeners(SimpleCriterionTrigger trigger, PlayerAdvancements advancements) {
        removePlayerListeners((CriterionTrigger) trigger, advancements);
    }

    @SuppressWarnings("rawtypes")
    public static void addPlayerListener(ImpossibleTrigger trigger, PlayerAdvancements advancements, CriterionTriggerListener listener) {
        addPlayerListener((CriterionTrigger) trigger, advancements, listener);
    }

    @SuppressWarnings("rawtypes")
    public static void removePlayerListener(ImpossibleTrigger trigger, PlayerAdvancements advancements, CriterionTriggerListener listener) {
        removePlayerListener((CriterionTrigger) trigger, advancements, listener);
    }

    @SuppressWarnings("rawtypes")
    public static void removePlayerListeners(ImpossibleTrigger trigger, PlayerAdvancements advancements) {
        removePlayerListeners((CriterionTrigger) trigger, advancements);
    }

    // ---- a mod's super call into 26.1.2's ImpossibleTrigger (its three bodies were empty) ----

    @SuppressWarnings("rawtypes")
    public static void impossibleAddSuper(ImpossibleTrigger trigger, PlayerAdvancements advancements, CriterionTriggerListener listener) {
    }

    @SuppressWarnings("rawtypes")
    public static void impossibleRemoveSuper(ImpossibleTrigger trigger, PlayerAdvancements advancements, CriterionTriggerListener listener) {
    }

    public static void impossibleRemoveAllSuper(ImpossibleTrigger trigger, PlayerAdvancements advancements) {
    }

    // ---- 26.2's PlayerAdvancements handing a Legacy trigger its listeners (mixin/CbS26PlayerAdvancementsMixin) ----

    /** addListener(criterion, key): 26.1.2's registerListener handed the trigger Listener(instance, advancement, name) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean added(PlayerAdvancements advancements, Criterion criterion, PlayerAdvancements.TriggerInstanceKey key) {
        if (criterion.trigger() instanceof Legacy l) {
            l.addPlayerListener(advancements, new CriterionTriggerListener(criterion.triggerInstance(), key.advancement(), key.criterion()));
            return true;
        }
        return false;
    }

    /**
     * removeListener(trigger, key): 26.1.2's removeListener handed the trigger the same Listener, built from the
     * advancement's criterion of that name (unregisterListeners walked advancement.criteria())
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean removed(PlayerAdvancements advancements, CriterionTrigger trigger, PlayerAdvancements.TriggerInstanceKey key) {
        if (!(trigger instanceof Legacy l)) {
            return false;
        }
        Map<String, Criterion<?>> criteria = key.advancement().value().criteria();
        Criterion<?> criterion = criteria.get(key.criterion());
        CriterionTriggerInstance instance = criterion != null ? criterion.triggerInstance() : null;
        l.removePlayerListener(advancements, new CriterionTriggerListener(instance, key.advancement(), key.criterion()));
        return true;
    }

    /** clearTriggers(): 26.1.2's stopListening called removePlayerListeners on every registered trigger type */
    @SuppressWarnings("rawtypes")
    public static void cleared(PlayerAdvancements advancements) {
        for (Object t : net.minecraft.core.registries.BuiltInRegistries.TRIGGER_TYPES) {
            if (t instanceof Legacy l) {
                l.removePlayerListeners(advancements);
            }
        }
    }
}
