package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3558;

/**
 * 1.21.1's LightEngine members on 1.21.2.
 *
 * <p>1.21.1's engine asked a block's light properties with the chunk source's level and the position (getOpacity,
 * getOcclusionShape, shapeOccludes, and the static helpers taking a BlockGetter and positions); 1.21.2 asks the state
 * alone, as BlockStateBase's cache always answered for every block without a dynamic shape (computed at
 * EmptyBlockGetter and BlockPos.ZERO -- the shape family of this layer, CbLevelShapes, gives a 1.21.1 block's
 * level-and-position overrides the same questions). A 1.21.1 call drops the level and position; a 1.21.1 engine
 * subclass's override, asked in 1.21.2's state-only shape, gets the origin as its position, and its super call
 * reaches 1.21.2's. getOcclusionShape(state, pos, direction) is 1.21.2's static getOcclusionShape(state,
 * direction); 1.21.2's engine no longer calls an instance method for it, so a 1.21.1 override informs only its
 * mod's own calls (they reach it).
 */
public final class CbLevelLight {

    private static final long ORIGIN = 0L;

    // ---- getOpacity(state, pos) ----

    public interface LegacyOpacity {
        int method_50017(class_2680 state, class_2338 pos);
    }

    public interface SuperOpacity {
        int cb$super$method_50017(class_2680 state);
    }

    public static int opacity(LegacyOpacity self, class_2680 state) {
        return self.method_50017(state, class_2338.field_10980);
    }

    public static int superOpacity(SuperOpacity self, class_2680 state, class_2338 pos) {
        return self.cb$super$method_50017(state);
    }

    // ---- shapeOccludes(fromPos, fromState, toPos, toState, direction) ----

    public interface LegacyShapeOccludes {
        boolean method_50016(long from, class_2680 fromState, long to, class_2680 toState, class_2350 direction);
    }

    public interface SuperShapeOccludes {
        boolean cb$super$method_50016(class_2680 fromState, class_2680 toState, class_2350 direction);
    }

    public static boolean shapeOccludes(LegacyShapeOccludes self, class_2680 fromState, class_2680 toState,
                                        class_2350 direction) {
        return self.method_50016(ORIGIN, fromState, class_2338.field_10980.method_10093(direction).method_10063(), toState,
            direction);
    }

    public static boolean superShapeOccludes(SuperShapeOccludes self, long from, class_2680 fromState, long to,
                                             class_2680 toState, class_2350 direction) {
        return self.cb$super$method_50016(fromState, toState, direction);
    }

    // ---- getOcclusionShape(state, pos, direction) ----

    public interface LegacyOcclusionShape {
        class_265 method_20710(class_2680 state, long pos, class_2350 direction);
    }

    /** engine.getOcclusionShape(state, pos, direction): a 1.21.1 engine's own, else 1.21.2's static one */
    public static class_265 method_20710(class_3558 self, class_2680 state, long pos, class_2350 direction) {
        if (self instanceof LegacyOcclusionShape l) {
            return l.method_20710(state, pos, direction);
        }
        return occlusionShape(self, state, pos, direction);
    }

    /** super.getOcclusionShape(..): LightEngine's body, the state's static occlusion shape */
    public static class_265 occlusionShape(class_3558 self, class_2680 state, long pos, class_2350 direction) {
        return class_3558.method_51562(state, direction);
    }

    private CbLevelLight() {
    }
}
