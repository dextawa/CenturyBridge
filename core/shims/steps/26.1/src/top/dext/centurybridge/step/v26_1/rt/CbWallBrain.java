package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryMap;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemorySlot;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import org.slf4j.Logger;
import top.dext.centurybridge.step.v26_1.facade.ExpirableValue;
import top.dext.centurybridge.step.v26_1.mixin.CbWallBrainAccess;
import top.dext.centurybridge.step.v26_1.mixin.CbWallLivingEntityAccess;

/**
 * 1.21.11's Brain.addActivity* family on 26.1, which kept only the general form: 26.1's
 * addActivity(activity, prioritised behaviours, conditions, memories to erase when stopped) is 1.21.11's
 * addActivityAndRemoveMemoriesWhenStopped, and every other 1.21.11 variant was a wrapper of it -- kept
 * here with 1.21.11's bodies (createPriorityPairs numbering the behaviours from the given priority up, the
 * empty condition set an ImmutableSet, the empty memory set a HashSet). Brain.provider(memories, sensors)
 * is 26.1's three-argument provider with no activities, as 26.1's one-argument provider builds it.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbWallBrain {

    private static final Logger LOGGER = LogUtils.getLogger();

    private CbWallBrain() {
    }

    /** 1.21.11 Brain.createPriorityPairs(int, ImmutableList) */
    private static ImmutableList createPriorityPairs(int priorityStart, ImmutableList<? extends BehaviorControl> behaviors) {
        int i = priorityStart;
        ImmutableList.Builder builder = ImmutableList.builder();
        for (BehaviorControl b : behaviors) {
            builder.add(Pair.of(i++, b));
        }
        return builder.build();
    }

    /** 1.21.11 Brain.addActivityAndRemoveMemoriesWhenStopped(activity, pairs, conditions, memories) */
    public static void addActivityAndRemoveMemoriesWhenStopped(Brain brain, Activity activity, ImmutableList pairs, Set conditions, Set memories) {
        brain.addActivity(activity, pairs, conditions, memories);
    }

    /** 1.21.11 Brain.addActivity(activity, pairs) */
    public static void addActivity(Brain brain, Activity activity, ImmutableList pairs) {
        brain.addActivity(activity, pairs, ImmutableSet.of(), Sets.newHashSet());
    }

    /** 1.21.11 Brain.addActivity(activity, priorityStart, behaviours) */
    public static void addActivity(Brain brain, Activity activity, int priorityStart, ImmutableList behaviors) {
        addActivity(brain, activity, createPriorityPairs(priorityStart, behaviors));
    }

    /** 1.21.11 Brain.addActivityAndRemoveMemoryWhenStopped(activity, priorityStart, behaviours, memory) */
    public static void addActivityAndRemoveMemoryWhenStopped(Brain brain, Activity activity, int priorityStart,
                                                             ImmutableList behaviors, MemoryModuleType memory) {
        Set<Pair<MemoryModuleType<?>, MemoryStatus>> conditions = ImmutableSet.of(Pair.of(memory, MemoryStatus.VALUE_PRESENT));
        Set<MemoryModuleType<?>> memories = ImmutableSet.of(memory);
        brain.addActivity(activity, createPriorityPairs(priorityStart, behaviors), conditions, memories);
    }

    /** 1.21.11 Brain.addActivityWithConditions(activity, pairs, conditions) */
    public static void addActivityWithConditions(Brain brain, Activity activity, ImmutableList pairs, Set conditions) {
        brain.addActivity(activity, pairs, conditions, Sets.newHashSet());
    }

    /** 1.21.11 Brain.addActivityWithConditions(activity, priorityStart, behaviours, conditions) */
    public static void addActivityWithConditions(Brain brain, Activity activity, int priorityStart, ImmutableList behaviors, Set conditions) {
        addActivityWithConditions(brain, activity, createPriorityPairs(priorityStart, behaviors), conditions);
    }

    /** 1.21.11 Brain.provider(memories, sensors) */
    public static Brain.Provider provider(Collection memories, Collection sensors) {
        return Brain.provider(memories, sensors, e -> List.of());
    }

    /**
     * 1.21.11 Brain.getMemories(): its own map, every registered memory type to Optional.empty() or the
     * memory's ExpirableValue. Over 26.1's slots: reading answers each slot's memory (a value ticking
     * which ticks the slot, see facade/ExpirableValue), put stores a value in the type's slot (or empties
     * it; a type the brain had not registered gets a slot, as 1.21.11's map took the key), remove
     * unregisters the type, as removing the key did.
     */
    public static Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> memories(Map<MemoryModuleType<?>, MemorySlot<?>> slots) {
        return new AbstractMap<>() {
            @Override
            public Set<Map.Entry<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>>> entrySet() {
                return new AbstractSet<>() {
                    @Override
                    public Iterator<Map.Entry<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>>> iterator() {
                        Iterator<Map.Entry<MemoryModuleType<?>, MemorySlot<?>>> it = slots.entrySet().iterator();
                        return new Iterator<>() {
                            @Override
                            public boolean hasNext() {
                                return it.hasNext();
                            }

                            @Override
                            public Map.Entry<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> next() {
                                Map.Entry<MemoryModuleType<?>, MemorySlot<?>> e = it.next();
                                return new AbstractMap.SimpleImmutableEntry<>(e.getKey(), Optional.ofNullable(ExpirableValue.cb$of(e.getValue())));
                            }

                            @Override
                            public void remove() {
                                it.remove();
                            }
                        };
                    }

                    @Override
                    public int size() {
                        return slots.size();
                    }
                };
            }

            @Override
            public boolean containsKey(Object key) {
                return slots.containsKey(key);
            }

            @Override
            public Optional<? extends ExpirableValue<?>> get(Object key) {
                MemorySlot<?> slot = slots.get(key);
                return slot == null ? null : Optional.ofNullable(ExpirableValue.cb$of(slot));
            }

            @Override
            public Optional<? extends ExpirableValue<?>> put(MemoryModuleType<?> key, Optional<? extends ExpirableValue<?>> value) {
                Optional<? extends ExpirableValue<?>> old = get(key);
                MemorySlot slot = slots.get(key);
                if (slot == null) {
                    slot = MemorySlot.create();
                    slots.put(key, slot);
                }
                if (value.isPresent()) {
                    ExpirableValue<?> v = value.get();
                    slot.set(v.method_24637(), v.method_35127());
                } else {
                    slot.clear();
                }
                return old;
            }

            @Override
            public Optional<? extends ExpirableValue<?>> remove(Object key) {
                MemorySlot<?> slot = slots.remove(key);
                return slot == null ? null : Optional.ofNullable(ExpirableValue.cb$of(slot));
            }
        };
    }

    // ---------------------------------------------------------------- Brain constructor, codec, copy

    /**
     * The random source 26.1's Brain delays each sensor's first tick with (Sensor.randomlyDelayStart):
     * 1.21.11's sensors drew that delay from one thread-safe random source shared by every sensor.
     */
    private static final RandomSource SENSOR_DELAYS = RandomSource.createThreadSafe();

    public static RandomSource sensorDelays() {
        return SENSOR_DELAYS;
    }

    /** 26.1's activity list of a brain 1.21.11 code makes: none (1.21.11 added activities afterwards) */
    public static List<?> noActivities() {
        return List.of();
    }

    /**
     * 1.21.11's initial memory values (an ImmutableList of Brain.MemoryValue, a package-private class
     * 26.1 no longer has -- only Brain.codec built non-empty ones): as 26.1's MemoryMap.
     */
    public static MemoryMap memoryValues(ImmutableList<?> values) {
        if (!values.isEmpty()) {
            throw new IllegalArgumentException("1.21.11 Brain memory values " + values
                    + ": a Brain.MemoryValue cannot exist on 26.1 (the class is gone; saved brains decode through Brain.codec)");
        }
        return MemoryMap.EMPTY;
    }

    /** 1.21.11 new Brain(memories, sensors, memoryValues, codec): the codec was what serializeStart wrote with */
    public static Brain newBrain(Collection memories, Collection sensors, ImmutableList<?> memoryValues, Supplier<?> codec) {
        return CbWallBrainAccess.cb$new(memories, sensors, List.of(), memoryValues(memoryValues), SENSOR_DELAYS);
    }

    /** 1.21.11 Brain.codec(memories, sensors): 1.21.11's brain format is 26.1's Brain.Packed */
    public static Codec<Brain> codec(Collection memories, Collection sensors) {
        return Brain.Packed.CODEC.xmap(p -> CbWallBrainAccess.cb$new(memories, sensors, List.of(), p.memories(), SENSOR_DELAYS),
                Brain::pack);
    }

    /**
     * 1.21.11 Brain.copyWithoutBehaviors(): a brain of the same memory types and sensors and no activities,
     * holding this brain's memories -- the same objects, as 1.21.11 put this brain's ExpirableValues in the
     * copy's map: the copy shares each memory's slot.
     */
    public static Brain<?> copyWithoutBehaviors(Brain<?> brain) {
        CbWallBrainAccess self = (CbWallBrainAccess) brain;
        Brain<?> copy = CbWallBrainAccess.cb$new(self.cb$memories().keySet(), self.cb$sensors().keySet(), List.of(),
                MemoryMap.EMPTY, SENSOR_DELAYS);
        Map<MemoryModuleType<?>, MemorySlot<?>> to = ((CbWallBrainAccess) copy).cb$memories();
        self.cb$memories().forEach((type, slot) -> {
            if (slot.hasValue()) {
                to.put(type, slot);
            }
        });
        return copy;
    }

    /** 1.21.11 Brain.serializeStart(ops), declared by a mod's brain class (the override hook adds it) */
    public interface SerializeStart {
        <T> DataResult<T> serializeStart(DynamicOps<T> ops);
    }

    /** the synthesized pack() of such a brain: 26.1 saves what the mod's serializeStart writes */
    public static Brain.Packed pack(SerializeStart self) {
        return Brain.Packed.CODEC.parse(NbtOps.INSTANCE, self.serializeStart(NbtOps.INSTANCE).getOrThrow()).getOrThrow();
    }

    // ---------------------------------------------------------------- makeBrain(Dynamic)

    /**
     * 1.21.11 LivingEntity.makeBrain(Dynamic), declared by a mod's entity class (the override hook adds
     * it): 26.1 makes the brain through makeBrain(Brain.Packed).
     */
    public interface MakeBrain {
        Brain makeBrain(Dynamic<?> dynamic);
    }

    /** super's makeBrain(Packed), reached from a mod entity's super.makeBrain(dynamic) (the accessor keeps the intermediary name) */
    public interface SuperMakeBrain {
        Brain cb$super$method_18867(Brain.Packed packed);
    }

    /** the entities whose makeBrain is running on this thread (innermost first) */
    private static final ThreadLocal<ArrayDeque<LivingEntity>> MAKING = ThreadLocal.withInitial(ArrayDeque::new);

    /**
     * 1.21.11's saved brain ({"memories": {...}}, the same format 26.1's Brain.Packed codec writes) as the
     * Dynamic 1.21.11 handed makeBrain: the NBT 1.21.11 read from the entity's "Brain" tag.
     */
    public static Dynamic<Tag> toDynamic(Brain.Packed packed) {
        return new Dynamic<>(NbtOps.INSTANCE, Brain.Packed.CODEC.encodeStart(NbtOps.INSTANCE, packed).getOrThrow());
    }

    /** a Dynamic as 26.1's Brain.Packed (1.21.11's makeBrain: unreadable memories are dropped, logged) */
    public static Brain.Packed toPacked(Dynamic<?> dynamic) {
        return Brain.Packed.CODEC.parse(dynamic).resultOrPartial(LOGGER::error).orElse(Brain.Packed.EMPTY);
    }

    /** the synthesized makeBrain(Packed) of a mod entity: its own makeBrain(Dynamic), the entity known */
    public static Brain makeBrain(MakeBrain self, Brain.Packed packed) {
        ArrayDeque<LivingEntity> making = MAKING.get();
        making.push((LivingEntity) self);
        try {
            return self.makeBrain(toDynamic(packed));
        } finally {
            making.pop();
        }
    }

    /** a mod entity's super.makeBrain(dynamic): super's makeBrain(Packed) */
    public static Brain superMakeBrain(SuperMakeBrain self, Dynamic<?> dynamic) {
        return self.cb$super$method_18867(toPacked(dynamic));
    }

    /** a mod's call of an entity's makeBrain(dynamic) (one it does not declare itself): 26.1's makeBrain(Packed) */
    public static Brain entityMakeBrain(LivingEntity entity, Dynamic<?> dynamic) {
        return ((CbWallLivingEntityAccess) entity).cb$makeBrain(toPacked(dynamic));
    }

    /**
     * 1.21.11 Brain.Provider.makeBrain(Dynamic): 26.1's makeBrain(entity, Packed) takes the entity (its
     * activities and random source), which 1.21.11's call made inside the entity's makeBrain had no
     * place for -- it is the entity whose makeBrain is running.
     */
    public static Brain providerMakeBrain(Brain.Provider provider, Dynamic<?> dynamic) {
        LivingEntity entity = MAKING.get().peek();
        if (entity == null) {
            throw new IllegalStateException("1.21.11 Brain.Provider.makeBrain(Dynamic) outside an entity's makeBrain: "
                    + "26.1 builds a brain for an entity (its activities and random source), and none is being made");
        }
        return provider.makeBrain(entity, toPacked(dynamic));
    }
}
