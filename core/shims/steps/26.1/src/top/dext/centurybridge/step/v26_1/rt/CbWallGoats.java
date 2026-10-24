package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;

/** 1.21.11 Goat.SENSOR_TYPES / MEMORY_TYPES, 1.21.11's lists (26.1 folds them into Goat's BRAIN_PROVIDER) */
public final class CbWallGoats {

    private CbWallGoats() {
    }

    private static final ImmutableList<SensorType<?>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS,
        SensorType.NEAREST_ITEMS, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.FOOD_TEMPTATIONS);

    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
        MemoryModuleType.PATH, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.BREED_TARGET, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS,
        MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT,
        MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleType.RAM_TARGET,
        MemoryModuleType.IS_PANICKING);

    /** Goat.SENSOR_TYPES */
    public static ImmutableList<SensorType<?>> field_30400() {
        return SENSOR_TYPES;
    }

    /** Goat.MEMORY_TYPES */
    public static ImmutableList<MemoryModuleType<?>> field_30401() {
        return MEMORY_TYPES;
    }
}
