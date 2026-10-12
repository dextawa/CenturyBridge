package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 Entity.canEnterPose(pose) and getBoundingBoxForPose(pose) (both protected), answered
 * again by every entity through a mixin (1.20.1 bodies). 1.20.2 moved the pose test to
 * Player.canPlayerFitWithinBlocksAndEntitiesWhen(pose) (method_52558), which builds the box inline;
 * see {@link CbEntityPose}.
 */
public interface CbLegacyPose {

    /** 1.20.1 canEnterPose(pose) */
    boolean method_20233(net.minecraft.class_4050 pose);

    /** 1.20.1 getBoundingBoxForPose(pose) */
    net.minecraft.class_238 method_20343(net.minecraft.class_4050 pose);

    /** canEnterPose(pose) called virtually from inside Entity (a protected override is selectable there) */
    boolean cb$legacyCanEnterPose(net.minecraft.class_4050 pose);
}
