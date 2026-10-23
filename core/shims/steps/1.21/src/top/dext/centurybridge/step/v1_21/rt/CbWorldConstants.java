package top.dext.centurybridge.step.v1_21.rt;

import com.mojang.datafixers.DSL;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_3551;
import net.minecraft.class_4284;
import net.minecraft.class_7696;
import net.minecraft.class_7701;

/**
 * Constants and switches 1.21 dropped, with the values and effects 1.20.6 gave them. Wired from s6world.json
 * (fieldRedirects / fieldGetters / staticRedirects).
 */
public final class CbWorldConstants {

    // SharedConstants: compile-time switches that were false in 1.20.6 and whose features 1.21 does not have
    public static final boolean USE_NEW_RENDERSYSTEM = false;
    public static final boolean MULTITHREADED_RENDERING = false;
    public static final boolean DEBUG_STORE_CHUNK_STACKTRACES = false;

    /**
     * SharedConstants.DATA_FIX_TYPES_TO_OPTIMIZE: the types the data fixer is optimized for. 1.20.6 started it
     * empty; the client's Main set it to DataFixTypes.TYPES_FOR_LEVEL_LIST as its first act
     * (enableDataFixerOptimizations), a dedicated server left it alone; DataFixers read it once, in its static
     * initializer (reached from Bootstrap, through Util.fetchChoiceType, before any mod initializer runs), and
     * built the fixer optimized for it, unoptimized when empty. 1.21 builds the fixer unoptimized and optimizes
     * on request: the client's Main calls DataFixers.optimize(TYPES_FOR_LEVEL_LIST) where 1.20.6's Main set the
     * field, the server nothing. Reads and writes of a 1.20.6 mod land here (fieldRedirects, staticFieldSetters);
     * CbWorldDataFixersMixin gives the value its 1.20.6 effect when 1.21's DataFixers is initialized
     * ({@link #dataFixerBuilt}).
     */
    public static Set<DSL.TypeReference> DATA_FIX_TYPES_TO_OPTIMIZE = Set.of();

    /** a 1.20.6 mod's write of SharedConstants.DATA_FIX_TYPES_TO_OPTIMIZE */
    public static void setDataFixTypesToOptimize(Set<DSL.TypeReference> types) {
        DATA_FIX_TYPES_TO_OPTIMIZE = types;
    }

    /** 1.20.6 SharedConstants.enableDataFixerOptimizations(): DATA_FIX_TYPES_TO_OPTIMIZE = TYPES_FOR_LEVEL_LIST */
    public static void enableDataFixerOptimizations() {
        setDataFixTypesToOptimize(class_4284.field_42975);
    }

    /**
     * 1.21's DataFixers has been initialized: the moment 1.20.6's DataFixers read DATA_FIX_TYPES_TO_OPTIMIZE.
     * On a client that is inside Main's DataFixers.optimize(TYPES_FOR_LEVEL_LIST), the call standing where
     * 1.20.6's Main set the field to that set: the field takes it (a value written before, by a pre-launch
     * entrypoint, was overwritten there in 1.20.6 too) and Main's own call does the optimizing. Elsewhere the
     * fixer is optimized for what was written before it existed, as 1.20.6 built it; empty, nothing. A write
     * after this changes only what later reads answer, as in 1.20.6.
     */
    public static void dataFixerBuilt() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            DATA_FIX_TYPES_TO_OPTIMIZE = class_4284.field_42975;
        } else if (!DATA_FIX_TYPES_TO_OPTIMIZE.isEmpty()) {
            class_3551.method_60487(DATA_FIX_TYPES_TO_OPTIMIZE);
        }
    }

    /**
     * FeatureFlags.UPDATE_1_21: the experiment 1.20.6 shipped the 1.21 content behind. 1.21 made that content
     * vanilla and removed the flag; what was gated on it is enabled wherever vanilla is, which is what
     * FeatureFlags.VANILLA stands for.
     */
    public static class_7696 field_46779() {
        return class_7701.field_40177;
    }

    /** LevelEvent.SOUND_ENDER_EYE_LAUNCH (1.21 dropped the event; CbWorldLevelEventClientMixin plays it again) */
    public static final int SOUND_ENDER_EYE_LAUNCH = 1003;

    /** Rabbit.EVIL_ATTACK_POWER / EVIL_ARMOR_VALUE, 1.20.6's values (1.21 dropped the one; the other is private) */
    public static final int RABBIT_EVIL_ATTACK_POWER = 8;
    public static final int RABBIT_EVIL_ARMOR_VALUE = 8;

    /**
     * AbstractChestedHorse.INV_CHEST_COUNT: the 15 chest slots of 1.20.6. 1.21 sizes the chest from
     * getInventoryColumns() instead (five columns of three slots when the horse has a chest).
     */
    public static final int INV_CHEST_COUNT = 15;

    /**
     * FollowOwnerGoal.TELEPORT_WHEN_DISTANCE_IS: 12 blocks. 1.21 moved the teleport to
     * TamableAnimal.shouldTryTeleportToOwner, which compares the squared distance with 144.
     */
    public static final int TELEPORT_WHEN_DISTANCE_IS = 12;

    /** Mob.LEASH_TAG: the NBT key of the leash, "leash" -- 1.21's Leashable.LEASH_TAG is the same key */
    public static final String LEASH_TAG = "leash";

    /** AbstractHurtingProjectile.ATTACK_DEFLECTION_SCALE / BOUNCE_DEFLECTION_SCALE (1.20.6 values) */
    public static final double ATTACK_DEFLECTION_SCALE = 0.1;
    public static final double BOUNCE_DEFLECTION_SCALE = 0.05;

    private CbWorldConstants() {
    }
}
