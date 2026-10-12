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
     * SharedConstants.DATA_FIX_TYPES_TO_OPTIMIZE: the data fixer types the fixer is optimized for. 1.20.6's
     * client Main set it to DataFixTypes.TYPES_FOR_LEVEL_LIST (enableDataFixerOptimizations) before the fixer was
     * built, a dedicated server left it empty; 1.21 does the same thing explicitly, the client Main calling
     * DataFixers.optimize(TYPES_FOR_LEVEL_LIST) and the server nothing. So the 1.20.6 value is that set on a
     * client and the empty set on a dedicated server.
     */
    public static Set<DSL.TypeReference> DATA_FIX_TYPES_TO_OPTIMIZE =
        FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? class_4284.field_42975 : Set.of();

    /**
     * 1.20.6 SharedConstants.enableDataFixerOptimizations(): optimize the fixer for the level-list types. 1.21's
     * DataFixers.optimize is the same request; it runs only when the set is not already the one requested (the
     * client's Main made it at start-up).
     */
    public static void enableDataFixerOptimizations() {
        if (!DATA_FIX_TYPES_TO_OPTIMIZE.equals(class_4284.field_42975)) {
            DATA_FIX_TYPES_TO_OPTIMIZE = class_4284.field_42975;
            class_3551.method_60487(class_4284.field_42975);
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
