package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_3609;
import net.minecraft.class_3610;
import net.minecraft.class_3611;
import net.minecraft.class_4538;

/**
 * 1.21.1's FlowingFluid spreading API on 1.21.2.
 * <ul>
 * <li>canSpreadTo(level, pos, state, direction, toPos, toState, toFluidState, fluid), protected: 1.21.1 asked it before
 *     spreading down and to each side; its body is the target fluid state's canBeReplacedWith, canPassThroughWall and
 *     canHoldFluid. 1.21.2 dropped the method and asks the same checks inline (canMaybePassThrough, canBeReplacedWith,
 *     canHoldSpecificFluid). A 1.21.1 override (LegacyCanSpreadTo, adapterHooks) decides where 1.21.1 asked it:
 *     CbLevelFlowingFluidMixin hands it 1.21.2's downward decision and the side check getSpread makes before keeping a
 *     direction. Its super call and a 1.21.1 call are 1.21.1's body, on 1.21.2's two static checks (the layer's access
 *     widener) -- 1.21.2's canHoldFluid is 1.21.1's, split in two.</li>
 * <li>getSlopeDistance(level, pos, distance, from, state, sourcePos, stateCache, holeCache), protected: 1.21.2 keeps
 *     the source position and the caches in a SpreadContext. A 1.21.1 override is asked with the context's origin and
 *     caches of its own (the caches only memoize what the level answers, keyed relative to the source, so fresh ones
 *     answer the same); its super call and a 1.21.1 call ask 1.21.2's method with a context at the source position --
 *     1.21.1's caches hold nothing 1.21.2's would not compute again.</li>
 * </ul>
 */
public final class CbLevelFlowing {
    private CbLevelFlowing() {
    }

    // ---- canSpreadTo ----

    /** a 1.21.1 FlowingFluid subclass declaring canSpreadTo (adapterHooks) */
    public interface LegacyCanSpreadTo {
        boolean method_15738(class_1922 level, class_2338 pos, class_2680 state, class_2350 direction, class_2338 toPos,
                             class_2680 toState, class_3610 toFluidState, class_3611 fluid);
    }

    /** 1.21.1's canSpreadTo body (also its super call) */
    public static boolean canSpreadTo(class_1922 level, class_2338 pos, class_2680 state, class_2350 direction, class_2338 toPos,
                                      class_2680 toState, class_3610 toFluidState, class_3611 fluid) {
        return toFluidState.method_15764(level, toPos, fluid, direction)
            && class_3609.method_15732(direction, level, pos, state, toPos, toState)
            && class_3609.method_15754(level, toPos, toState, fluid);
    }

    /** fluid.canSpreadTo(...): a 1.21.1 override where the fluid has one */
    public static boolean method_15738(class_3609 self, class_1922 level, class_2338 pos, class_2680 state, class_2350 direction,
                                       class_2338 toPos, class_2680 toState, class_3610 toFluidState, class_3611 fluid) {
        return self instanceof LegacyCanSpreadTo l
            ? l.method_15738(level, pos, state, direction, toPos, toState, toFluidState, fluid)
            : canSpreadTo(level, pos, state, direction, toPos, toState, toFluidState, fluid);
    }

    /** super.canSpreadTo(...) */
    public static boolean superCanSpreadTo(class_3609 self, class_1922 level, class_2338 pos, class_2680 state, class_2350 direction,
                                           class_2338 toPos, class_2680 toState, class_3610 toFluidState, class_3611 fluid) {
        return canSpreadTo(level, pos, state, direction, toPos, toState, toFluidState, fluid);
    }

    // ---- getSlopeDistance ----

    /** a 1.21.1 FlowingFluid subclass declaring getSlopeDistance (the override hook's adapter) */
    public interface LegacySlopeDistance {
        int method_15742(class_4538 level, class_2338 pos, int distance, class_2350 from, class_2680 state, class_2338 sourcePos,
                         Short2ObjectMap<Pair<class_2680, class_3610>> stateCache, Short2BooleanMap holeCache);
    }

    /** the 1.21.2 method, reached past a 1.21.1 override (the engine implements it with a super call) */
    public interface SuperSlopeDistance {
        int cb$super$method_15742(class_4538 level, class_2338 pos, int distance, class_2350 from, class_2680 state,
                                  class_3609.class_9900 context);
    }

    /** FlowingFluid's protected getSlopeDistance, for a 1.21.1 call on a fluid without an override (CbLevelFlowingFluidMixin) */
    public interface FlowingAccess {
        int cb$slopeDistance(class_4538 level, class_2338 pos, int distance, class_2350 from, class_2680 state,
                             class_3609.class_9900 context);
    }

    /** 1.21.2 asking a 1.21.1 override: the context's source position, caches of the override's own */
    public static int slopeDistanceIn(LegacySlopeDistance self, class_4538 level, class_2338 pos, int distance, class_2350 from,
                                      class_2680 state, class_3609.class_9900 context) {
        return self.method_15742(level, pos, distance, from, state, context.field_52661, new Short2ObjectOpenHashMap<>(),
            new Short2BooleanOpenHashMap());
    }

    /** the override's super.getSlopeDistance(..): 1.21.2's, with a context at the source position */
    public static int superSlopeDistance(SuperSlopeDistance self, class_4538 level, class_2338 pos, int distance, class_2350 from,
                                         class_2680 state, class_2338 sourcePos,
                                         Short2ObjectMap<Pair<class_2680, class_3610>> stateCache, Short2BooleanMap holeCache) {
        return self.cb$super$method_15742(level, pos, distance, from, state, context((class_3609) self, level, sourcePos));
    }

    /** fluid.getSlopeDistance(..) in 1.21.1's shape */
    public static int method_15742(class_3609 self, class_4538 level, class_2338 pos, int distance, class_2350 from,
                                   class_2680 state, class_2338 sourcePos,
                                   Short2ObjectMap<Pair<class_2680, class_3610>> stateCache, Short2BooleanMap holeCache) {
        if (self instanceof LegacySlopeDistance l) {
            return l.method_15742(level, pos, distance, from, state, sourcePos, stateCache, holeCache);
        }
        return ((FlowingAccess) self).cb$slopeDistance(level, pos, distance, from, state, context(self, level, sourcePos));
    }

    private static class_3609.class_9900 context(class_3609 fluid, class_1922 level, class_2338 sourcePos) {
        return fluid.new class_9900(level, sourcePos);
    }
}
