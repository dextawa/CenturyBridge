package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;

/**
 * 1.21.11's protected SENSOR_TYPES / MEMORY_TYPES lists of the brained mobs 26.1 folds into a BRAIN_PROVIDER (sensor
 * types given, memory types derived), 1.21.11's lists as its static initializers built them (read from the 1.21.11
 * bytecode; every element still exists in 26.1). A subclass reads them here (fieldGetters), as with CbWallGoats.
 */
public final class CbWallBrainLists {

    private CbWallBrainLists() {
    }

    private static final ImmutableList<SensorType<?>> ALLAY_SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY,
        SensorType.NEAREST_ITEMS);

    /** Allay.SENSOR_TYPES (class_7298.field_38398) */
    public static ImmutableList<SensorType<?>> field_38398() {
        return ALLAY_SENSOR_TYPES;
    }

    private static final ImmutableList<MemoryModuleType<?>> ALLAY_MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.PATH, MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
        MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.HURT_BY,
        MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.LIKED_PLAYER, MemoryModuleType.LIKED_NOTEBLOCK_POSITION,
        MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleType.IS_PANICKING);

    /** Allay.MEMORY_TYPES (class_7298.field_38403) */
    public static ImmutableList<MemoryModuleType<?>> field_38403() {
        return ALLAY_MEMORY_TYPES;
    }

    private static final ImmutableList<SensorType<?>> AXOLOTL_SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY,
        SensorType.AXOLOTL_ATTACKABLES, SensorType.FOOD_TEMPTATIONS);

    /** Axolotl.SENSOR_TYPES (class_5762.field_28333) */
    public static ImmutableList<SensorType<?>> field_28333() {
        return AXOLOTL_SENSOR_TYPES;
    }

    private static final ImmutableList<MemoryModuleType<?>> AXOLOTL_MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
        MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH,
        MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT,
        MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleType.NEAREST_ATTACKABLE,
        MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED,
        MemoryModuleType.HAS_HUNTING_COOLDOWN, MemoryModuleType.IS_PANICKING);

    /** Axolotl.MEMORY_TYPES (class_5762.field_28334) */
    public static ImmutableList<MemoryModuleType<?>> field_28334() {
        return AXOLOTL_MEMORY_TYPES;
    }

    private static final ImmutableList<SensorType<?>> FROG_SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.FROG_ATTACKABLES,
        SensorType.FROG_TEMPTATIONS, SensorType.IS_IN_WATER);

    /** Frog.SENSOR_TYPES (class_7102.field_37449) */
    public static ImmutableList<SensorType<?>> field_37449() {
        return FROG_SENSOR_TYPES;
    }

    private static final ImmutableList<MemoryModuleType<?>> FROG_MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
        MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH,
        MemoryModuleType.BREED_TARGET, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleType.LONG_JUMP_MID_JUMP,
        MemoryModuleType.ATTACK_TARGET, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
        MemoryModuleType.IS_TEMPTED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY,
        MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.IS_IN_WATER, MemoryModuleType.IS_PREGNANT,
        MemoryModuleType.IS_PANICKING, MemoryModuleType.UNREACHABLE_TONGUE_TARGETS);

    /** Frog.MEMORY_TYPES (class_7102.field_37450) */
    public static ImmutableList<MemoryModuleType<?>> field_37450() {
        return FROG_MEMORY_TYPES;
    }

    private static final ImmutableList<SensorType<?>> TADPOLE_SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY,
        SensorType.FROG_TEMPTATIONS);

    /** Tadpole.SENSOR_TYPES (class_7110.field_37497) */
    public static ImmutableList<SensorType<?>> field_37497() {
        return TADPOLE_SENSOR_TYPES;
    }

    private static final ImmutableList<MemoryModuleType<?>> TADPOLE_MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.NEAREST_VISIBLE_ADULT,
        MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.TEMPTING_PLAYER,
        MemoryModuleType.BREED_TARGET, MemoryModuleType.IS_PANICKING);

    /** Tadpole.MEMORY_TYPES (class_7110.field_37498) */
    public static ImmutableList<MemoryModuleType<?>> field_37498() {
        return TADPOLE_MEMORY_TYPES;
    }

    private static final ImmutableList<SensorType<?>> NAUTILUSAI_SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.NEAREST_PLAYERS,
        SensorType.HURT_BY, SensorType.NAUTILUS_TEMPTATIONS);

    /** NautilusAi.SENSOR_TYPES (class_12118.field_63350) */
    public static ImmutableList<SensorType<?>> field_63350() {
        return NAUTILUSAI_SENSOR_TYPES;
    }

    private static final ImmutableList<MemoryModuleType<?>> NAUTILUSAI_MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.NEAREST_VISIBLE_ADULT,
        MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.TEMPTING_PLAYER,
        MemoryModuleType.BREED_TARGET, MemoryModuleType.IS_PANICKING, MemoryModuleType.ATTACK_TARGET,
        MemoryModuleType.CHARGE_COOLDOWN_TICKS, MemoryModuleType.HURT_BY, MemoryModuleType.ANGRY_AT,
        MemoryModuleType.ATTACK_TARGET_COOLDOWN);

    /** NautilusAi.MEMORY_TYPES (class_12118.field_63351) */
    public static ImmutableList<MemoryModuleType<?>> field_63351() {
        return NAUTILUSAI_MEMORY_TYPES;
    }

    private static final ImmutableList<SensorType<?>> ZOMBIENAUTILUSAI_SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.NEAREST_PLAYERS,
        SensorType.HURT_BY, SensorType.NAUTILUS_TEMPTATIONS);

    /** ZombieNautilusAi.SENSOR_TYPES (class_12120.field_63364) */
    public static ImmutableList<SensorType<?>> field_63364() {
        return ZOMBIENAUTILUSAI_SENSOR_TYPES;
    }

    private static final ImmutableList<MemoryModuleType<?>> ZOMBIENAUTILUSAI_MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.NEAREST_VISIBLE_ADULT,
        MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.TEMPTING_PLAYER,
        MemoryModuleType.BREED_TARGET, MemoryModuleType.IS_PANICKING, MemoryModuleType.ATTACK_TARGET,
        MemoryModuleType.CHARGE_COOLDOWN_TICKS, MemoryModuleType.HURT_BY, MemoryModuleType.ANGRY_AT,
        MemoryModuleType.ATTACK_TARGET_COOLDOWN);

    /** ZombieNautilusAi.MEMORY_TYPES (class_12120.field_63365) */
    public static ImmutableList<MemoryModuleType<?>> field_63365() {
        return ZOMBIENAUTILUSAI_MEMORY_TYPES;
    }

    private static final ImmutableList<SensorType<?>> ZOGLIN_SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS);

    /** Zoglin.SENSOR_TYPES (class_5136.field_23731) */
    public static ImmutableList<SensorType<?>> field_23731() {
        return ZOGLIN_SENSOR_TYPES;
    }

    private static final ImmutableList<MemoryModuleType<?>> ZOGLIN_MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER,
        MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET,
        MemoryModuleType.ATTACK_COOLING_DOWN);

    /** Zoglin.MEMORY_TYPES (class_5136.field_23733) */
    public static ImmutableList<MemoryModuleType<?>> field_23733() {
        return ZOGLIN_MEMORY_TYPES;
    }

    private static final ImmutableList<SensorType<?>> CREAKINGAI_SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS);

    /** CreakingAi.SENSOR_TYPES (class_10280.field_54585) */
    public static ImmutableList<SensorType<?>> field_54585() {
        return CREAKINGAI_SENSOR_TYPES;
    }

    private static final ImmutableList<MemoryModuleType<?>> CREAKINGAI_MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER,
        MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYERS, MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH,
        MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN);

    /** CreakingAi.MEMORY_TYPES (class_10280.field_54586) */
    public static ImmutableList<MemoryModuleType<?>> field_54586() {
        return CREAKINGAI_MEMORY_TYPES;
    }

    private static final ImmutableList<SensorType<?>> HOGLIN_SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ADULT,
        SensorType.HOGLIN_SPECIFIC_SENSOR);

    /** Hoglin.SENSOR_TYPES (class_4760.field_22365) */
    public static ImmutableList<SensorType<?>> field_22365() {
        return HOGLIN_SENSOR_TYPES;
    }

    private static final ImmutableList<MemoryModuleType<?>> HOGLIN_MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
        MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH,
        MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN,
        MemoryModuleType.AVOID_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT,
        MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_REPELLENT,
        MemoryModuleType.PACIFIED, MemoryModuleType.IS_PANICKING);

    /** Hoglin.MEMORY_TYPES (class_4760.field_22366) */
    public static ImmutableList<MemoryModuleType<?>> field_22366() {
        return HOGLIN_MEMORY_TYPES;
    }

    private static final ImmutableList<SensorType<?>> PIGLIN_SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS,
        SensorType.HURT_BY, SensorType.PIGLIN_SPECIFIC_SENSOR);

    /** Piglin.SENSOR_TYPES (class_4836.field_22376) */
    public static ImmutableList<SensorType<?>> field_22376() {
        return PIGLIN_SENSOR_TYPES;
    }

    private static final ImmutableList<MemoryModuleType<?>> PIGLIN_MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_LIVING_ENTITIES,
        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
        MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
        MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY,
        MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET,
        MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH,
        MemoryModuleType.ANGRY_AT, MemoryModuleType.UNIVERSAL_ANGER, MemoryModuleType.AVOID_TARGET,
        MemoryModuleType.ADMIRING_ITEM, MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, MemoryModuleType.ADMIRING_DISABLED,
        MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryModuleType.CELEBRATE_LOCATION, MemoryModuleType.DANCING,
        MemoryModuleType.HUNTED_RECENTLY, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
        MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.RIDE_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT,
        MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD,
        MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.NEAREST_REPELLENT,
        MemoryModuleType.SPEAR_FLEEING_TIME, MemoryModuleType.SPEAR_FLEEING_POSITION, MemoryModuleType.SPEAR_CHARGE_POSITION,
        MemoryModuleType.SPEAR_ENGAGE_TIME, MemoryModuleType.SPEAR_STATUS);

    /** Piglin.MEMORY_TYPES (class_4836.field_22381) */
    public static ImmutableList<MemoryModuleType<?>> field_22381() {
        return PIGLIN_MEMORY_TYPES;
    }

    private static final ImmutableList<SensorType<?>> PIGLINBRUTE_SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS,
        SensorType.HURT_BY, SensorType.PIGLIN_BRUTE_SPECIFIC_SENSOR);

    /** PiglinBrute.SENSOR_TYPES (class_5419.field_25761) */
    public static ImmutableList<SensorType<?>> field_25761() {
        return PIGLINBRUTE_SENSOR_TYPES;
    }

    private static final ImmutableList<MemoryModuleType<?>> PIGLINBRUTE_MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_LIVING_ENTITIES,
        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
        MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.HURT_BY,
        MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
        MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET,
        MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
        MemoryModuleType.HOME);

    /** PiglinBrute.MEMORY_TYPES (class_5419.field_25760) */
    public static ImmutableList<MemoryModuleType<?>> field_25760() {
        return PIGLINBRUTE_MEMORY_TYPES;
    }
}
