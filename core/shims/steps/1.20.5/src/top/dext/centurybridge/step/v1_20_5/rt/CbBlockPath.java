package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's isPathfindable(state, level, pos, type) (Block) and isPathfindable(level, pos, type)
 * (BlockState) on 1.20.5, which asks without the level and the position: isPathfindable(state,
 * type) / isPathfindable(type). 1.20.5's own implementations read neither; where they stood in
 * for them (the collision-shape test) they pass EmptyBlockGetter.INSTANCE and BlockPos.ZERO.
 *
 * A mod's call of the old shape still names a level and a position: it is asked of the receiver
 * in the new shape with that level and position recorded for the state being asked, and a mod
 * block whose class overrides the old shape (override hook, bridge mode) is handed them back.
 * Asked by 1.20.5 itself, which no longer has them to give, such an override is handed what
 * 1.20.5's implementations use in their place. The mod's own super.isPathfindable(..) asks
 * super's new shape (super bridge), which reads neither.
 */
public final class CbBlockPath {

    /** the 1.20.4 Block.isPathfindable a mod block class overrides (adapter of the override hook) */
    public interface Legacy {
        boolean method_9516(net.minecraft.class_2680 state, net.minecraft.class_1922 level,
            net.minecraft.class_2338 pos, net.minecraft.class_10 type);
    }

    /** super's isPathfindable(state, type), reached from a mod block's super.isPathfindable(..) */
    public interface Super {
        boolean cb$super$method_9516(net.minecraft.class_2680 state, net.minecraft.class_10 type);
    }

    /** the 1.20.4 BlockState.isPathfindable a mod's BlockStateBase subclass overrides */
    public interface LegacyState {
        boolean method_26171(net.minecraft.class_1922 level, net.minecraft.class_2338 pos, net.minecraft.class_10 type);
    }

    /** super's isPathfindable(type), reached from such a subclass's super.isPathfindable(..) */
    public interface SuperState {
        boolean cb$super$method_26171(net.minecraft.class_10 type);
    }

    /** {asked object (the state, or the BlockStateBase), level, pos}, innermost first */
    private static final ThreadLocal<java.util.ArrayDeque<Object[]>> ASKED =
        ThreadLocal.withInitial(java.util.ArrayDeque::new);

    private static Object[] where(Object asked) {
        for (Object[] f : ASKED.get()) {
            if (f[0] == asked) {
                return f;
            }
        }
        return null;
    }

    /** Block.isPathfindable(state, level, pos, type) called on any block */
    public static boolean method_9516(net.minecraft.class_4970 self, net.minecraft.class_2680 state,
            net.minecraft.class_1922 level, net.minecraft.class_2338 pos, net.minecraft.class_10 type) {
        java.util.ArrayDeque<Object[]> asked = ASKED.get();
        asked.push(new Object[] {state, level, pos});
        try {
            return ((CbBlockAccess) self).cb$isPathfindable(state, type);
        } finally {
            asked.pop();
        }
    }

    /** BlockState.isPathfindable(level, pos, type) called on any state */
    public static boolean method_26171(net.minecraft.class_4970.class_4971 self, net.minecraft.class_1922 level,
            net.minecraft.class_2338 pos, net.minecraft.class_10 type) {
        java.util.ArrayDeque<Object[]> asked = ASKED.get();
        // the state asks its block with itself: both lookups find this record
        asked.push(new Object[] {self, level, pos});
        try {
            return self.method_26171(type);
        } finally {
            asked.pop();
        }
    }

    /** isPathfindable(state, type) of a mod block whose class overrides the 1.20.4 shape */
    public static boolean isPathfindable(Legacy self, net.minecraft.class_2680 state, net.minecraft.class_10 type) {
        Object[] at = where(state);
        return at != null
            ? self.method_9516(state, (net.minecraft.class_1922) at[1], (net.minecraft.class_2338) at[2], type)
            : self.method_9516(state, net.minecraft.class_2682.field_12294, net.minecraft.class_2338.field_10980, type);
    }

    /** isPathfindable(type) of a mod's BlockStateBase subclass overriding the 1.20.4 shape */
    public static boolean stateIsPathfindable(LegacyState self, net.minecraft.class_10 type) {
        Object[] at = where(self);
        return at != null
            ? self.method_26171((net.minecraft.class_1922) at[1], (net.minecraft.class_2338) at[2], type)
            : self.method_26171(net.minecraft.class_2682.field_12294, net.minecraft.class_2338.field_10980, type);
    }

    /** a mod block's super.isPathfindable(state, level, pos, type) */
    public static boolean superIsPathfindable(Super self, net.minecraft.class_2680 state, net.minecraft.class_1922 level,
            net.minecraft.class_2338 pos, net.minecraft.class_10 type) {
        return self.cb$super$method_9516(state, type);
    }

    /** such a subclass's super.isPathfindable(level, pos, type) */
    public static boolean superStateIsPathfindable(SuperState self, net.minecraft.class_1922 level,
            net.minecraft.class_2338 pos, net.minecraft.class_10 type) {
        return self.cb$super$method_26171(type);
    }

    private CbBlockPath() {
    }
}
