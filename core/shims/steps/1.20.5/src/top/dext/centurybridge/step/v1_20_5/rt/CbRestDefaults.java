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

    /**
     * GameType.SURVIVAL: 1.20.4 GameTestHelper.makeMockPlayer() made a player that is neither creative
     * nor spectator (its anonymous Player answered false to both); 1.20.5 builds that player for the
     * game type it is given
     */
    public static net.minecraft.class_1934 survival() {
        return net.minecraft.class_1934.field_9215;
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
