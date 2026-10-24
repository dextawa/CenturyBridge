package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * Values 1.20.4 calls did not pass and 1.20.5 takes as parameters, each what 1.20.4's body used (it
 * computed the value itself, or had no such choice); used by call reshapes and as frames defaults.
 */
public final class CbRestDefaults {

    /** false: what a 1.20.4 call meant where 1.20.5 added a boolean choice 1.20.4 always made one way */
    public static boolean no() {
        return false;
    }

    /** true: what a 1.20.4 call meant where 1.20.5 added a boolean choice 1.20.4 always made the other way */
    public static boolean yes() {
        return true;
    }

    /** 1: a scale 1.20.5 added where 1.20.4 used the value as it was (EntityType's spawn dimensions scale) */
    public static float unitScale() {
        return 1.0F;
    }

    /** 1.20.4 Raids.createOrExtendRaid(player) raided the player's block position */
    public static net.minecraft.class_2338 raidPos(net.minecraft.class_3222 player) {
        return player.method_24515();
    }

    /** the frames default of the same: the receiver and the 1.20.4 call's argument */
    public static net.minecraft.class_2338 raidPos(Object raids, net.minecraft.class_3222 player) {
        return player.method_24515();
    }

    private CbRestDefaults() {
    }
}
