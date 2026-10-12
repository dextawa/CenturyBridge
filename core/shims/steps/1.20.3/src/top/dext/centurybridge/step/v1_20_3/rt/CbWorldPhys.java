package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_2338;

/**
 * phys: 1.20.2's AABB(BlockPos a, BlockPos b) was AABB(a.x, a.y, a.z, b.x, b.y, b.z) -- the corners themselves,
 * not 1.20.3's encapsulatingFullBlocks (which reaches the far faces of the blocks). The constructor reshape
 * passes the six coordinates.
 */
public final class CbWorldPhys {

    public static double x(class_2338 pos) {
        return pos.method_10263();
    }

    public static double y(class_2338 pos) {
        return pos.method_10264();
    }

    public static double z(class_2338 pos) {
        return pos.method_10260();
    }

    private CbWorldPhys() {
    }
}
