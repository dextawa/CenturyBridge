package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;

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
}
