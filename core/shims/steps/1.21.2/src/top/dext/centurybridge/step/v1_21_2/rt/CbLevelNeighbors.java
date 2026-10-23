package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayDeque;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_7165;
import net.minecraft.class_9902;
import net.minecraft.class_9904;

/**
 * 1.21.1's neighbor updates on 1.21.2.
 *
 * <p>1.21.1 told a block which neighbor changed: Block.neighborChanged(state, level, pos, block, fromPos, moved),
 * Level.neighborChanged(pos, block, fromPos), NeighborUpdater.neighborChanged(.., fromPos ..). 1.21.2 passes an
 * Orientation there instead -- null unless the redstone experiments are on -- and the position is gone from every
 * shape. It is not gone from the game: each 1.21.2 update still starts somewhere, and the places that start one are
 * where 1.21.1 named the position.
 * <ul>
 * <li>CollectingNeighborUpdater.MultiNeighborUpdate (Level.updateNeighborsAt / updateNeighborsAtExceptFromFacing)
 *     keeps its sourcePos, which 1.21.1's runNext passed as fromPos;</li>
 * <li>a single update (SimpleNeighborUpdate, FullNeighborUpdate -- Level.neighborChanged) is made by a vanilla call
 *     site that in 1.21.1 passed the position (DiodeBlock and ObserverBlock.updateNeighborsInFront: the diode,
 *     IceBlock.melt: the ice, PistonHeadBlock.neighborChanged: its own fromPos, PistonMovingBlockEntity: the moving
 *     block, CactusBlock.randomTick, BaseRailBlock.updateState, DetectorRailBlock.updatePowerToConnected and
 *     Level.updateNeighbourForOutputSignal: the block at pos), or by a converted 1.21.1 mod's call, which names
 *     it. Those sites tell this class the source before they make the update ({@link #neighborChanged} and its
 *     siblings), the update records it at construction (CbLevelNeighborRecordMixin), and hands it on when it runs
 *     ({@link #deliver}).</li>
 * </ul>
 * A 1.21.1 block's old neighborChanged, asked in 1.21.2's shape, gets that source as its fromPos ({@link #fromPos}).
 * An update this class was never told about -- one a mod written for 1.21.2 starts -- has no recorded source: the
 * Orientation then says where it came from when there is one (1.21.2's fronts point from the source to the updated
 * block: MultiNeighborUpdate's withFront(direction), DiodeBlock's initialOrientation(level, facing.getOpposite(),
 * UP)), else the block's own position stands in.
 *
 * <p>The Orientation a 1.21.1 call hands 1.21.2 is the one 1.21.2's own vanilla sites make for the same update:
 * ExperimentalRedstoneUtils.initialOrientation(level, front, null) with front the direction from the source to the
 * updated block (null when they are not neighbors) -- null itself unless the experiments are on.
 */
public final class CbLevelNeighbors {

    private static final Object NONE = new Object();

    /** the source the next update made on this thread comes from (pushed around the call that makes it) */
    private static final ThreadLocal<ArrayDeque<Object>> PENDING = ThreadLocal.withInitial(ArrayDeque::new);
    /** the source of the update being delivered */
    private static final ThreadLocal<ArrayDeque<Object>> DELIVERY = ThreadLocal.withInitial(ArrayDeque::new);
    /** the orientation each 1.21.1 override was handed in 1.21.2's shape: {block, pos, orientation} */
    private static final ThreadLocal<ArrayDeque<Object[]>> FRAMES = ThreadLocal.withInitial(ArrayDeque::new);

    /** an update record that remembers the source it was made for (CbLevelNeighborRecordMixin) */
    public interface Sourced {
        class_2338 cb$source();
    }

    // ---- sources ----

    /** the source the update being made now comes from, null when none was named */
    public static class_2338 pendingSource() {
        Object s = PENDING.get().peek();
        return s instanceof class_2338 p ? p : null;
    }

    private static class_2338 deliverySource() {
        Object s = DELIVERY.get().peek();
        return s instanceof class_2338 p ? p : null;
    }

    /** the fromPos 1.21.1 handed a block updated at pos, for an update delivered with this orientation */
    public static class_2338 fromPos(class_2338 pos, class_9904 orientation) {
        class_2338 s = deliverySource();
        return s != null ? s : guess(pos, orientation);
    }

    /** the fromPos of a Level / NeighborUpdater call about pos, made with this orientation */
    public static class_2338 callerSource(class_2338 pos, class_9904 orientation) {
        class_2338 s = pendingSource();
        return s != null ? s : guess(pos, orientation);
    }

    private static class_2338 guess(class_2338 pos, class_9904 orientation) {
        return orientation != null ? pos.method_10093(orientation.method_61849().method_10153()) : pos;
    }

    /** the orientation 1.21.2's own sites make for an update of pos coming from source */
    public static class_9904 toward(class_1937 level, class_2338 source, class_2338 pos) {
        class_2350 front = null;
        if (source != null && pos != null && source.method_19455(pos) == 1) {
            front = class_2350.method_62674(pos.method_10059(source), null);
        }
        return class_9902.method_61826(level, front, null);
    }

    // ---- making updates with a known source ----

    public static void neighborChanged(class_1937 level, class_2338 pos, class_2248 block, class_9904 orientation,
                                       class_2338 source) {
        PENDING.get().push(source == null ? NONE : source);
        try {
            level.method_8492(pos, block, orientation);
        } finally {
            PENDING.get().pop();
        }
    }

    public static void neighborChanged(class_1937 level, class_2680 state, class_2338 pos, class_2248 block,
                                       class_9904 orientation, boolean moved, class_2338 source) {
        PENDING.get().push(source == null ? NONE : source);
        try {
            level.method_41410(state, pos, block, orientation, moved);
        } finally {
            PENDING.get().pop();
        }
    }

    /** an update running (CbLevelNeighborRecordMixin): executeUpdate with its source, nothing pending inside it */
    public static void deliver(class_2338 source, class_1937 level, class_2680 state, class_2338 pos, class_2248 block,
                               class_9904 orientation, boolean moved) {
        PENDING.get().push(NONE);
        DELIVERY.get().push(source == null ? NONE : source);
        try {
            class_7165.method_41708(level, state, pos, block, orientation, moved);
        } finally {
            DELIVERY.get().pop();
            PENDING.get().pop();
        }
    }

    /** a delivery made by a direct call (BlockState.handleNeighborChanged, Block.neighborChanged) naming its source */
    private static void direct(class_2338 source, Runnable call) {
        PENDING.get().push(NONE);
        DELIVERY.get().push(source == null ? NONE : source);
        try {
            call.run();
        } finally {
            DELIVERY.get().pop();
            PENDING.get().pop();
        }
    }

    // ---- 1.21.1 calls (instance / static redirects) ----

    /** level.neighborChanged(pos, block, fromPos) */
    public static void method_8492(class_1937 self, class_2338 pos, class_2248 block, class_2338 fromPos) {
        neighborChanged(self, pos, block, toward(self, fromPos, pos), fromPos);
    }

    /** level.neighborChanged(state, pos, block, fromPos, moved) */
    public static void method_41410(class_1937 self, class_2680 state, class_2338 pos, class_2248 block, class_2338 fromPos,
                                    boolean moved) {
        neighborChanged(self, state, pos, block, toward(self, fromPos, pos), moved, fromPos);
    }

    /** updater.neighborChanged(pos, block, fromPos): the updater's level is not in reach, so no experimental orientation */
    public static void method_41704(class_7165 self, class_2338 pos, class_2248 block, class_2338 fromPos) {
        PENDING.get().push(fromPos == null ? NONE : fromPos);
        try {
            self.method_41704(pos, block, null);
        } finally {
            PENDING.get().pop();
        }
    }

    public static void method_41703(class_7165 self, class_2680 state, class_2338 pos, class_2248 block, class_2338 fromPos,
                                    boolean moved) {
        PENDING.get().push(fromPos == null ? NONE : fromPos);
        try {
            self.method_41703(state, pos, block, null, moved);
        } finally {
            PENDING.get().pop();
        }
    }

    /** NeighborUpdater.executeUpdate(level, state, pos, block, fromPos, moved) */
    public static void method_41708(class_1937 level, class_2680 state, class_2338 pos, class_2248 block, class_2338 fromPos,
                                    boolean moved) {
        deliver(fromPos, level, state, pos, block, toward(level, fromPos, pos), moved);
    }

    /** state.handleNeighborChanged(level, pos, block, fromPos, moved) */
    public static void method_26181(net.minecraft.class_4970.class_4971 self, class_1937 level, class_2338 pos,
                                    class_2248 block, class_2338 fromPos, boolean moved) {
        direct(fromPos, () -> self.method_26181(level, pos, block, toward(level, fromPos, pos), moved));
    }

    /** block.neighborChanged(state, level, pos, block, fromPos, moved), a protected call */
    public static void method_9612(net.minecraft.class_4970 self, class_2680 state, class_1937 level, class_2338 pos,
                                   class_2248 block, class_2338 fromPos, boolean moved) {
        direct(fromPos, () -> ((Access) self).cb$neighborChanged(state, level, pos, block, toward(level, fromPos, pos), moved));
    }

    /** BlockBehaviour's protected 1.21.2 neighborChanged (CbLevelBlockInvoker) */
    public interface Access {
        void cb$neighborChanged(class_2680 state, class_1937 level, class_2338 pos, class_2248 block, class_9904 orientation,
                                boolean moved);
    }

    // ---- 1.21.1 overrides asked in 1.21.2's shape (override hook bridges) ----

    public interface Legacy {
        void method_9612(class_2680 state, class_1937 level, class_2338 pos, class_2248 block, class_2338 fromPos, boolean moved);
    }

    public interface Super {
        void cb$super$method_9612(class_2680 state, class_1937 level, class_2338 pos, class_2248 block, class_9904 orientation,
                                  boolean moved);
    }

    public static void neighborChanged(Legacy self, class_2680 state, class_1937 level, class_2338 pos, class_2248 block,
                                       class_9904 orientation, boolean moved) {
        FRAMES.get().push(new Object[] {self, pos, orientation});
        try {
            self.method_9612(state, level, pos, block, fromPos(pos, orientation), moved);
        } finally {
            FRAMES.get().pop();
        }
    }

    /** the orientation the innermost 1.21.2-shaped call handed this object about pos, else the one for fromPos */
    private static class_9904 orientation(Object self, class_1937 level, class_2338 pos, class_2338 fromPos) {
        for (Object[] f : FRAMES.get()) {
            if (f[0] == self && pos.equals(f[1])) {
                return (class_9904) f[2];
            }
        }
        return toward(level, fromPos, pos);
    }

    /** a 1.21.1 block's super.neighborChanged(..): super's 1.21.2 neighborChanged with the orientation vanilla passed */
    public static void superNeighborChanged(Super self, class_2680 state, class_1937 level, class_2338 pos, class_2248 block,
                                            class_2338 fromPos, boolean moved) {
        self.cb$super$method_9612(state, level, pos, block, orientation(self, level, pos, fromPos), moved);
    }

    public interface LegacyState {
        void method_26181(class_1937 level, class_2338 pos, class_2248 block, class_2338 fromPos, boolean moved);
    }

    public interface SuperState {
        void cb$super$method_26181(class_1937 level, class_2338 pos, class_2248 block, class_9904 orientation, boolean moved);
    }

    public static void handleNeighborChanged(LegacyState self, class_1937 level, class_2338 pos, class_2248 block,
                                             class_9904 orientation, boolean moved) {
        FRAMES.get().push(new Object[] {self, pos, orientation});
        try {
            self.method_26181(level, pos, block, fromPos(pos, orientation), moved);
        } finally {
            FRAMES.get().pop();
        }
    }

    public static void superHandleNeighborChanged(SuperState self, class_1937 level, class_2338 pos, class_2248 block,
                                                  class_2338 fromPos, boolean moved) {
        self.cb$super$method_26181(level, pos, block, orientation(self, level, pos, fromPos), moved);
    }

    /** a 1.21.1 Level subclass's neighborChanged overrides (rare: a mod's own level type) */
    public interface LegacyLevel {
        void method_8492(class_2338 pos, class_2248 block, class_2338 fromPos);
    }

    public interface SuperLevel {
        void cb$super$method_8492(class_2338 pos, class_2248 block, class_9904 orientation);
    }

    public interface LegacyLevelFull {
        void method_41410(class_2680 state, class_2338 pos, class_2248 block, class_2338 fromPos, boolean moved);
    }

    public interface SuperLevelFull {
        void cb$super$method_41410(class_2680 state, class_2338 pos, class_2248 block, class_9904 orientation, boolean moved);
    }

    public static void levelNeighborChanged(LegacyLevel self, class_2338 pos, class_2248 block, class_9904 orientation) {
        FRAMES.get().push(new Object[] {self, pos, orientation});
        try {
            self.method_8492(pos, block, callerSource(pos, orientation));
        } finally {
            FRAMES.get().pop();
        }
    }

    public static void superLevelNeighborChanged(SuperLevel self, class_2338 pos, class_2248 block, class_2338 fromPos) {
        class_9904 o = orientation(self, (class_1937) self, pos, fromPos);
        PENDING.get().push(fromPos == null ? NONE : fromPos);
        try {
            self.cb$super$method_8492(pos, block, o);
        } finally {
            PENDING.get().pop();
        }
    }

    public static void levelNeighborChangedFull(LegacyLevelFull self, class_2680 state, class_2338 pos, class_2248 block,
                                                class_9904 orientation, boolean moved) {
        FRAMES.get().push(new Object[] {self, pos, orientation});
        try {
            self.method_41410(state, pos, block, callerSource(pos, orientation), moved);
        } finally {
            FRAMES.get().pop();
        }
    }

    public static void superLevelNeighborChangedFull(SuperLevelFull self, class_2680 state, class_2338 pos, class_2248 block,
                                                     class_2338 fromPos, boolean moved) {
        class_9904 o = orientation(self, (class_1937) self, pos, fromPos);
        PENDING.get().push(fromPos == null ? NONE : fromPos);
        try {
            self.cb$super$method_41410(state, pos, block, o, moved);
        } finally {
            PENDING.get().pop();
        }
    }

    /** a 1.21.1 NeighborUpdater class's neighborChanged overrides, asked in 1.21.2's shapes (override hook bridges) */
    public interface LegacyUpdater {
        void method_41704(class_2338 pos, class_2248 block, class_2338 fromPos);
    }

    public interface LegacyUpdaterFull {
        void method_41703(class_2680 state, class_2338 pos, class_2248 block, class_2338 fromPos, boolean moved);
    }

    public interface SuperUpdater {
        void cb$super$method_41704(class_2338 pos, class_2248 block, class_9904 orientation);
    }

    public interface SuperUpdaterFull {
        void cb$super$method_41703(class_2680 state, class_2338 pos, class_2248 block, class_9904 orientation, boolean moved);
    }

    public static void updaterNeighborChanged(LegacyUpdater self, class_2338 pos, class_2248 block, class_9904 orientation) {
        FRAMES.get().push(new Object[] {self, pos, orientation});
        try {
            self.method_41704(pos, block, callerSource(pos, orientation));
        } finally {
            FRAMES.get().pop();
        }
    }

    public static void updaterNeighborChangedFull(LegacyUpdaterFull self, class_2680 state, class_2338 pos, class_2248 block,
                                                  class_9904 orientation, boolean moved) {
        FRAMES.get().push(new Object[] {self, pos, orientation});
        try {
            self.method_41703(state, pos, block, callerSource(pos, orientation), moved);
        } finally {
            FRAMES.get().pop();
        }
    }

    /** the orientation the innermost 1.21.2-shaped call handed this updater about pos, else none */
    private static class_9904 framed(Object self, class_2338 pos) {
        for (Object[] f : FRAMES.get()) {
            if (f[0] == self && pos.equals(f[1])) {
                return (class_9904) f[2];
            }
        }
        return null;
    }

    public static void superUpdaterNeighborChanged(SuperUpdater self, class_2338 pos, class_2248 block, class_2338 fromPos) {
        PENDING.get().push(fromPos == null ? NONE : fromPos);
        try {
            self.cb$super$method_41704(pos, block, framed(self, pos));
        } finally {
            PENDING.get().pop();
        }
    }

    public static void superUpdaterNeighborChangedFull(SuperUpdaterFull self, class_2680 state, class_2338 pos, class_2248 block,
                                                       class_2338 fromPos, boolean moved) {
        PENDING.get().push(fromPos == null ? NONE : fromPos);
        try {
            self.cb$super$method_41703(state, pos, block, framed(self, pos), moved);
        } finally {
            PENDING.get().pop();
        }
    }

    /**
     * the orientation a 1.21.1 updateNeighborsAtExceptFromFacing call passes: none -- 1.21.2's MultiNeighborUpdate
     * computes its own from the level and the skipped direction when it has none (and the experiments are on)
     */
    public static class_9904 noOrientation() {
        return null;
    }

    private CbLevelNeighbors() {
    }
}
