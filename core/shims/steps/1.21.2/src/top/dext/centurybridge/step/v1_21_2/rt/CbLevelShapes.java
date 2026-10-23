package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_10225;
import net.minecraft.class_1922;
import net.minecraft.class_1936;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2682;
import net.minecraft.class_265;
import net.minecraft.class_4970;
import net.minecraft.class_5819;

/**
 * 1.21.1's block shape API on 1.21.2.
 *
 * <p><b>updateShape.</b> 1.21.1's BlockBehaviour.updateShape(state, direction, neighborState, LevelAccessor,
 * pos, neighborPos) is 1.21.2's updateShape(state, LevelReader, ScheduledTickAccess, pos, direction,
 * neighborPos, neighborState, RandomSource). Every 1.21.2 caller (BlockStateBase.updateShape from
 * NeighborUpdater.executeShapeUpdate, Block.updateFromNeighbourShapes, UpgradeData's fixers,
 * StructureTemplate.updateShapeAtEdge) passes one LevelAccessor as both the reader and the tick access and
 * that level's getRandom() as the random -- so 1.21.1's single LevelAccessor is that level, and a 1.21.1
 * call becomes the 1.21.2 call with the level twice and its random ({@link #random}). The override hook
 * hands a mod block's old updateShape the reader when it is a LevelAccessor (narrow), and the old method's
 * super call gets the tick access and random vanilla passed (frames), or -- entered directly -- the level
 * and its random ({@link #tickAccess}, {@link #superRandom}).
 *
 * <p><b>Occlusion / light / skylight.</b> 1.21.1's getOcclusionShape, getLightBlock and
 * propagatesSkylightDown took (state, BlockGetter, pos); 1.21.2's take the state only. 1.21.1's
 * BlockStateBase cached exactly these three for every block without a dynamic shape by asking the block
 * with EmptyBlockGetter.INSTANCE and BlockPos.ZERO (BlockStateBase.Cache), and 1.21.2 caches the
 * one-argument answers the same way (BlockStateBase.initCache): a mod block's old override is asked with
 * those same two values ({@link Legacy}). 1.21.1 asked a dynamic-shape block with the real level and
 * position; 1.21.2 never asks with a position at all, so such a block is asked with the cache's values too.
 * A 1.21.1 call naming a getter and position drops them: 1.21.2 answers from the state.
 */
public final class CbLevelShapes {

    /** the getter and position 1.21.1's BlockStateBase.Cache asked a block's shape methods with */
    public static class_1922 emptyGetter() {
        return class_2682.field_12294;
    }

    public static class_2338 zeroPos() {
        return class_2338.field_10980;
    }

    // ---- updateShape ----

    /** the random 1.21.2's callers pass with a level: that level's own */
    public static class_5819 random(class_1936 level) {
        return level.method_8409();
    }

    /** frameDefaults of the old updateShape's super call, entered directly: 1.21.1's level is the tick access */
    public static class_10225 tickAccess(class_4970 self, class_2680 state, class_2350 direction, class_2680 neighborState,
                                         class_1936 level, class_2338 pos, class_2338 neighborPos) {
        return level;
    }

    /** frameDefaults of the old updateShape's super call, entered directly: the level's random */
    public static class_5819 superRandom(class_4970 self, class_2680 state, class_2350 direction, class_2680 neighborState,
                                         class_1936 level, class_2338 pos, class_2338 neighborPos) {
        return level.method_8409();
    }

    /** the same for a 1.21.1 BlockStateBase subclass's updateShape super call */
    public static class_10225 stateTickAccess(class_4970.class_4971 self, class_2350 direction, class_2680 neighborState,
                                              class_1936 level, class_2338 pos, class_2338 neighborPos) {
        return level;
    }

    public static class_5819 stateRandom(class_4970.class_4971 self, class_2350 direction, class_2680 neighborState,
                                         class_1936 level, class_2338 pos, class_2338 neighborPos) {
        return level.method_8409();
    }

    // ---- the one-argument shape methods, asked of a mod block's old three-argument override ----

    /** the 1.21.1 shape methods a mod block class overrides (adapters of the bridge hooks) */
    public interface LegacyOcclusion {
        class_265 method_9571(class_2680 state, class_1922 level, class_2338 pos);
    }

    public interface LegacyLightBlock {
        int method_9505(class_2680 state, class_1922 level, class_2338 pos);
    }

    public interface LegacySkylight {
        boolean method_9579(class_2680 state, class_1922 level, class_2338 pos);
    }

    public static class_265 occlusionShape(LegacyOcclusion self, class_2680 state) {
        return self.method_9571(state, emptyGetter(), zeroPos());
    }

    public static int lightBlock(LegacyLightBlock self, class_2680 state) {
        return self.method_9505(state, emptyGetter(), zeroPos());
    }

    public static boolean propagatesSkylightDown(LegacySkylight self, class_2680 state) {
        return self.method_9579(state, emptyGetter(), zeroPos());
    }

    // ---- BlockStateBase's own (overridable, public) shape methods ----

    public interface LegacyStateOcclusion {
        class_265 method_26201(class_1922 level, class_2338 pos);
    }

    public interface LegacyStateFaceOcclusion {
        class_265 method_26173(class_1922 level, class_2338 pos, class_2350 direction);
    }

    public interface LegacyStateLightBlock {
        int method_26193(class_1922 level, class_2338 pos);
    }

    public interface LegacyStateSkylight {
        boolean method_26167(class_1922 level, class_2338 pos);
    }

    public interface LegacyStateSolidRender {
        boolean method_26216(class_1922 level, class_2338 pos);
    }

    public interface LegacyStateOffset {
        class_243 method_26226(class_1922 level, class_2338 pos);
    }

    public static class_265 stateOcclusionShape(LegacyStateOcclusion self) {
        return self.method_26201(emptyGetter(), zeroPos());
    }

    public static class_265 stateFaceOcclusionShape(LegacyStateFaceOcclusion self, class_2350 direction) {
        return self.method_26173(emptyGetter(), zeroPos(), direction);
    }

    public static int stateLightBlock(LegacyStateLightBlock self) {
        return self.method_26193(emptyGetter(), zeroPos());
    }

    public static boolean statePropagatesSkylightDown(LegacyStateSkylight self) {
        return self.method_26167(emptyGetter(), zeroPos());
    }

    public static boolean stateSolidRender(LegacyStateSolidRender self) {
        return self.method_26216(emptyGetter(), zeroPos());
    }

    /** 1.21.2 asks the offset with the position only; 1.21.1's offset function was handed the level too */
    public static class_243 stateOffset(LegacyStateOffset self, class_2338 pos) {
        return self.method_26226(emptyGetter(), pos);
    }

    // ---- a mod block's super calls of the 1.21.1 shapes: super's 1.21.2 one-argument shape ----

    public interface SuperOcclusion {
        class_265 cb$super$method_9571(class_2680 state);
    }

    public interface SuperLightBlock {
        int cb$super$method_9505(class_2680 state);
    }

    public interface SuperSkylight {
        boolean cb$super$method_9579(class_2680 state);
    }

    public static class_265 superOcclusionShape(SuperOcclusion self, class_2680 state, class_1922 level, class_2338 pos) {
        return self.cb$super$method_9571(state);
    }

    public static int superLightBlock(SuperLightBlock self, class_2680 state, class_1922 level, class_2338 pos) {
        return self.cb$super$method_9505(state);
    }

    public static boolean superPropagatesSkylightDown(SuperSkylight self, class_2680 state, class_1922 level, class_2338 pos) {
        return self.cb$super$method_9579(state);
    }

    public interface SuperStateOcclusion {
        class_265 cb$super$method_26201();
    }

    public interface SuperStateFaceOcclusion {
        class_265 cb$super$method_26173(class_2350 direction);
    }

    public interface SuperStateLightBlock {
        int cb$super$method_26193();
    }

    public interface SuperStateSkylight {
        boolean cb$super$method_26167();
    }

    public interface SuperStateSolidRender {
        boolean cb$super$method_26216();
    }

    public interface SuperStateOffset {
        class_243 cb$super$method_26226(class_2338 pos);
    }

    public static class_265 superStateOcclusionShape(SuperStateOcclusion self, class_1922 level, class_2338 pos) {
        return self.cb$super$method_26201();
    }

    public static class_265 superStateFaceOcclusionShape(SuperStateFaceOcclusion self, class_1922 level, class_2338 pos,
                                                        class_2350 direction) {
        return self.cb$super$method_26173(direction);
    }

    public static int superStateLightBlock(SuperStateLightBlock self, class_1922 level, class_2338 pos) {
        return self.cb$super$method_26193();
    }

    public static boolean superStatePropagatesSkylightDown(SuperStateSkylight self, class_1922 level, class_2338 pos) {
        return self.cb$super$method_26167();
    }

    public static boolean superStateSolidRender(SuperStateSolidRender self, class_1922 level, class_2338 pos) {
        return self.cb$super$method_26216();
    }

    public static class_243 superStateOffset(SuperStateOffset self, class_1922 level, class_2338 pos) {
        return self.cb$super$method_26226(pos);
    }

    // ---- 1.21.1 calls naming a getter and position (instance redirects) ----

    /**
     * block.getOcclusionShape(state, level, pos) on any block: a converted 1.21.1 block that declares the
     * three-argument shape answers with the level and position it is asked about, as 1.21.1 asked it; any other
     * block answers 1.21.2's state-only shape (CbLevelBlockInvoker)
     */
    public static class_265 method_9571(class_4970 self, class_2680 state, class_1922 level, class_2338 pos) {
        return self instanceof LegacyOcclusion l ? l.method_9571(state, level, pos) : ((Access) self).cb$occlusionShape(state);
    }

    public static int method_9505(class_4970 self, class_2680 state, class_1922 level, class_2338 pos) {
        return self instanceof LegacyLightBlock l ? l.method_9505(state, level, pos) : ((Access) self).cb$lightBlock(state);
    }

    public static boolean method_9579(class_4970 self, class_2680 state, class_1922 level, class_2338 pos) {
        return self instanceof LegacySkylight l ? l.method_9579(state, level, pos) : ((Access) self).cb$propagatesSkylightDown(state);
    }

    /**
     * 1.21.1's isOcclusionShapeFullBlock (gone; no vanilla code of either release calls it): a 1.21.1 block's own
     * override first, else Block.isShapeFullBlock of the occlusion shape it is asked with
     */
    public static boolean method_42326(class_4970 self, class_2680 state, class_1922 level, class_2338 pos) {
        if (self instanceof LegacyOcclusionFullBlock l) {
            return l.method_42326(state, level, pos);
        }
        return occlusionFullBlock(self, state, level, pos);
    }

    /** super.isOcclusionShapeFullBlock(..): 1.21.1's body */
    public static boolean occlusionFullBlock(class_4970 self, class_2680 state, class_1922 level, class_2338 pos) {
        return net.minecraft.class_2248.method_9614(method_9571(self, state, level, pos));
    }

    public interface LegacyOcclusionFullBlock {
        boolean method_42326(class_2680 state, class_1922 level, class_2338 pos);
    }

    public static class_265 method_26201(class_4970.class_4971 self, class_1922 level, class_2338 pos) {
        return self instanceof LegacyStateOcclusion l ? l.method_26201(level, pos) : self.method_26201();
    }

    public static class_265 method_26173(class_4970.class_4971 self, class_1922 level, class_2338 pos, class_2350 direction) {
        return self instanceof LegacyStateFaceOcclusion l ? l.method_26173(level, pos, direction) : self.method_26173(direction);
    }

    public static int method_26193(class_4970.class_4971 self, class_1922 level, class_2338 pos) {
        return self instanceof LegacyStateLightBlock l ? l.method_26193(level, pos) : self.method_26193();
    }

    public static boolean method_26167(class_4970.class_4971 self, class_1922 level, class_2338 pos) {
        return self instanceof LegacyStateSkylight l ? l.method_26167(level, pos) : self.method_26167();
    }

    public static boolean method_26216(class_4970.class_4971 self, class_1922 level, class_2338 pos) {
        return self instanceof LegacyStateSolidRender l ? l.method_26216(level, pos) : self.method_26216();
    }

    public static class_243 method_26226(class_4970.class_4971 self, class_1922 level, class_2338 pos) {
        return self instanceof LegacyStateOffset l ? l.method_26226(level, pos) : self.method_26226(pos);
    }

    /** BlockBehaviour's protected 1.21.2 shape methods, called virtually (CbLevelBlockInvoker) */
    public interface Access {
        class_265 cb$occlusionShape(class_2680 state);

        int cb$lightBlock(class_2680 state);

        boolean cb$propagatesSkylightDown(class_2680 state);
    }

    // ---- OffsetFunction ----

    /** 1.21.1's BlockBehaviour.OffsetFunction: evaluate(state, level, pos) */
    @FunctionalInterface
    public interface LegacyOffsetFunction {
        class_243 evaluate(class_2680 state, class_1922 level, class_2338 pos);
    }

    /**
     * A 1.21.1 offset function as 1.21.2's evaluate(state, pos). Every 1.21.1 caller (BlockStateBase.getOffset)
     * passed the level the state was asked about; 1.21.2's getOffset is asked without one, and the vanilla
     * functions never read it (Properties.offsetType's XZ / XYZ lambdas use the position only).
     */
    public static class_4970.class_8176 offsetFunction(LegacyOffsetFunction legacy) {
        return legacy == null ? null : (state, pos) -> legacy.evaluate(state, emptyGetter(), pos);
    }

    /** evaluate(state, pos) of a mod class implementing the 1.21.1 OffsetFunction (adapter hook) */
    public static class_243 evaluate(LegacyOffsetFunction self, class_2680 state, class_2338 pos) {
        return self.evaluate(state, emptyGetter(), pos);
    }

    private CbLevelShapes() {
    }
}
