package top.dext.centurybridge.step.v26_2.rt;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * 26.1.2 world-side members 26.2 dropped whose 26.1.2 body is one call 26.2 still has: each answers with that
 * call (javap of 26.1.2's body).
 */
public final class CbS26World {
    private CbS26World() {}

    /** 26.1.2 BlockPos.getCenter(): Vec3.atCenterOf(this) */
    public static Vec3 getCenter(BlockPos pos) {
        return Vec3.atCenterOf(pos);
    }

    /** 26.1.2 BlockPos.getBottomCenter(): Vec3.atBottomCenterOf(this) */
    public static Vec3 getBottomCenter(BlockPos pos) {
        return Vec3.atBottomCenterOf(pos);
    }
}
