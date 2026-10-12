package top.dext.centurybridge.rt.v1_20_2;

import net.minecraft.class_4050;

/**
 * 1.20.1 pose fitting on 1.20.2's player (s1misc pose bridge). 1.20.1's Player asked
 * canEnterPose(pose), which tested getBoundingBoxForPose(pose); 1.20.2's player asks
 * canPlayerFitWithinBlocksAndEntitiesWhen(pose) and builds the box itself. A converted player
 * class overriding canEnterPose has the new method forward to it (a plain override hook); one
 * overriding only getBoundingBoxForPose gets the new method synthesized to call this, so the box
 * it answers is the one tested -- 1.20.1's canEnterPose, virtually, so an override of it further
 * up the converted hierarchy still decides.
 */
public final class CbEntityPose {

    public static boolean canEnterPose(CbLegacyPose entity, class_4050 pose) {
        return entity.cb$legacyCanEnterPose(pose);
    }

    private CbEntityPose() {
    }
}
