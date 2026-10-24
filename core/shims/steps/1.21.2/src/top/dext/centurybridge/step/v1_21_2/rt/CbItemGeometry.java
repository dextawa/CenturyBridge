package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_2350;
import net.minecraft.class_259;
import net.minecraft.class_265;

/** 1.21.1's Direction / Shapes statics 1.21.2 dropped, with 1.21.1's bodies on 1.21.2. */
public final class CbItemGeometry {

    /**
     * Direction.fromDelta(dx, dy, dz): the direction of a unit step along one axis, null for anything else (1.21.1's
     * body: zero on two axes, the sign of the third)
     */
    public static class_2350 method_50026(int dx, int dy, int dz) {
        if (dx == 0) {
            if (dy == 0) {
                if (dz > 0) {
                    return class_2350.field_11035;
                }
                if (dz < 0) {
                    return class_2350.field_11043;
                }
            } else if (dz == 0) {
                return dy > 0 ? class_2350.field_11036 : class_2350.field_11033;
            }
        } else if (dy == 0 && dz == 0) {
            return dx > 0 ? class_2350.field_11034 : class_2350.field_11039;
        }
        return null;
    }

    /**
     * Shapes.getFaceShape(shape, direction): the empty shape for the empty shape, else the shape's face --
     * 1.21.2's VoxelShape.getFaceShape, which computes 1.21.1's body (the slice at the face when the shape reaches
     * it, else empty) and caches it
     */
    public static class_265 method_16344(class_265 shape, class_2350 direction) {
        return shape == class_259.method_1073() ? class_259.method_1073() : shape.method_20538(direction);
    }

    private CbItemGeometry() {
    }
}
