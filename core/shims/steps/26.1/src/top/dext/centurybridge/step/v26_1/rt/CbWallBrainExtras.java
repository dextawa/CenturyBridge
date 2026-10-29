package top.dext.centurybridge.step.v26_1.rt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * What 1.21.11 mods add to a brained mob's SENSOR_TYPES / MEMORY_TYPES, added to the brains 26.1 builds for that mob.
 *
 * 1.21.11 built a mob's brain from those two static lists (brainProvider(): Brain.provider(MEMORY_TYPES, SENSOR_TYPES)),
 * so a mod appending a memory there had it registered on every brain of the mob. 26.1 keeps no such list: a brain
 * registers its provider's own memory list, each sensor's requires() and each behaviour's getRequiredMemories()
 * (javap of 26.1 Brain.<init> and Brain.addActivity), and getting an unregistered memory throws IllegalStateException
 * (Brain.getMemorySlot) as in 1.21.11. The given-back lists (rt/brainlists, generated) register here with 1.21.11's
 * own lists as their base; when 26.1's Brain.Provider.makeBrain builds a brain for an entity of the mob class (the
 * nearest registered superclass), the memories and sensors the lists now hold beyond the base are added to the ones
 * the provider passes (mixin/CbWallBrainExtrasMixin): a union with 26.1's own, never a replacement, so a brain with no
 * mod change is exactly 26.1's.
 *
 * Known loss (release_notes_known): an element a mod takes out of a list cannot take effect -- 26.1 re-registers what
 * its sensors and behaviours require -- and is logged once per list owner.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbWallBrainExtras {
    private static final Logger LOGGER = LoggerFactory.getLogger("centurybridge");

    private CbWallBrainExtras() {
    }

    private record Entry(Class<?> holder, List<?> baseMemories, List<?> baseSensors, Supplier<List<?>> memories,
                         Supplier<List<?>> sensors) {
    }

    /** mob class -> the given-back lists of its 1.21.11 brain */
    private static final Map<Class<?>, Entry> BY_MOB = new LinkedHashMap<>();
    private static final Set<String> REPORTED = new HashSet<>();
    /** the entity Brain.Provider.makeBrain is building a brain for (set at its head, CbWallBrainExtrasMixin) */
    private static final ThreadLocal<LivingEntity> BUILDING = new ThreadLocal<>();

    /**
     * a generated list holder, at its class initialization: 1.21.11's lists (base) and the lists as they stand (what
     * mods' writes and injectors left), for the brains of {@code mob}
     */
    public static synchronized void register(Class<?> holder, List<?> baseMemories, List<?> baseSensors, Class<?> mob,
                                             Supplier<List<?>> memories, Supplier<List<?>> sensors) {
        BY_MOB.put(mob, new Entry(holder, baseMemories, baseSensors, memories, sensors));
    }

    /** the same lists also serve the brains of {@code mob} (1.21.11 read them in several mobs' brainProvider()) */
    public static synchronized void alsoFor(Class<?> holder, Class<?> mob) {
        for (Entry e : BY_MOB.values()) {
            if (e.holder == holder) {
                BY_MOB.put(mob, e);
                return;
            }
        }
    }

    public static void building(LivingEntity entity) {
        BUILDING.set(entity);
    }

    /** the provider's memory types, with what a mod added to the mob's 1.21.11 MEMORY_TYPES */
    public static Collection<?> memories(Collection<?> own) {
        return union(own, true);
    }

    /** the provider's sensor types, with what a mod added to the mob's 1.21.11 SENSOR_TYPES */
    public static Collection<?> sensors(Collection<?> own) {
        return union(own, false);
    }

    private static Collection<?> union(Collection<?> own, boolean memories) {
        LivingEntity entity = BUILDING.get();
        if (entity == null) {
            return own;
        }
        Entry e = entry(entity.getClass());
        if (e == null) {
            return own;
        }
        List<?> current = memories ? e.memories.get() : e.sensors.get();
        List<?> base = memories ? e.baseMemories : e.baseSensors;
        if (current == null || current == base) {
            return own;
        }
        List<Object> added = new ArrayList<>();
        for (Object o : current) {
            if (!base.contains(o) && !own.contains(o)) {
                added.add(o);
            }
        }
        report(e, base, current, memories);
        if (added.isEmpty()) {
            return own;
        }
        List<Object> out = new ArrayList<>(own);
        out.addAll(added);
        return out;
    }

    private static Entry entry(Class<?> c) {
        // the generated holders register at their class initialization: touch each once (1.21.11 ran the lists'
        // initializers with the mob class, before its first brain)
        Holders.loadAll();
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
            Entry e;
            synchronized (CbWallBrainExtras.class) {
                e = BY_MOB.get(k);
            }
            if (e != null) {
                return e;
            }
        }
        return null;
    }

    private static void report(Entry e, List<?> base, List<?> current, boolean memories) {
        List<Object> removed = new ArrayList<>();
        for (Object o : base) {
            if (!current.contains(o)) {
                removed.add(o);
            }
        }
        if (removed.isEmpty()) {
            return;
        }
        synchronized (REPORTED) {
            if (!REPORTED.add(e.holder.getName() + (memories ? " memories" : " sensors"))) {
                return;
            }
        }
        LOGGER.warn("[CenturyBridge] a 1.21.11 mod took {} out of {}'s {}: 26.1 builds the brain from its sensors' and "
            + "behaviours' requirements and keeps them registered (not carried)", removed,
            e.holder.getSimpleName().replace("CbWallBrainList", ""), memories ? "MEMORY_TYPES" : "SENSOR_TYPES");
    }

    /** the generated holders (rt/brainlists), loaded on the first brain built */
    private static final class Holders {
        private static volatile boolean loaded;

        static void loadAll() {
            if (loaded) {
                return;
            }
            for (String n : top.dext.centurybridge.step.v26_1.rt.brainlists.CbWallBrainListIndex.HOLDERS) {
                try {
                    Class.forName(n, true, CbWallBrainExtras.class.getClassLoader());
                } catch (ClassNotFoundException ex) {
                    throw new IllegalStateException(n, ex);
                }
            }
            loaded = true;
        }
    }
}
