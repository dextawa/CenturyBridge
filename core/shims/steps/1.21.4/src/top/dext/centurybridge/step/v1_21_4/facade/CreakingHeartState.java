package top.dext.centurybridge.step.v1_21_4.facade;

import net.minecraft.class_3542;

/**
 * 1.21.3's CreakingHeartBlock.CreakingHeartState (net/minecraft/class_10319$class_10320, classRenames):
 * DISABLED / DORMANT / ACTIVE with 1.21.3's serialized names. The enum links as 1.21.3's did; the block state
 * property that held it (CreakingHeartBlock.CREAKING / BlockStateProperties.CREAKING) is ledgered: 1.21.4's
 * creaking heart states are ACTIVE and NATURAL booleans and no longer store the time of day that told
 * DORMANT from ACTIVE.
 */
public enum CreakingHeartState implements class_3542 {
    DISABLED("disabled"),
    DORMANT("dormant"),
    ACTIVE("active");

    /** DISABLED */
    public static final CreakingHeartState field_54741 = DISABLED;
    /** DORMANT */
    public static final CreakingHeartState field_54742 = DORMANT;
    /** ACTIVE */
    public static final CreakingHeartState field_54743 = ACTIVE;

    public final String field_54744;

    CreakingHeartState(String name) {
        this.field_54744 = name;
    }

    /** getSerializedName() */
    @Override
    public String method_15434() {
        return this.field_54744;
    }
}
