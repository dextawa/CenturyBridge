package top.dext.centurybridge.step.v26_2.rt;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

/**
 * 26.1.2's emissive test on 26.2. 26.1.2 kept a StatePredicate (state, level, pos) and asked it in
 * LevelRenderer.getLightCoords(getter, level, state, pos); 26.2 keeps a Predicate&lt;BlockState&gt; and asks it with the
 * state only, in LightCoordsUtil.getLightCoords(getter, level, state, pos) (the same body, moved) and in
 * BlockModelResolver.update (a 26.2 display path with no level). mixin/CbS26EmissiveContextMixin holds the level and
 * position of the getLightCoords call that is asking, so a 26.1.2 predicate is asked with what 26.1.2 passed it; a
 * mod's own emissiveRendering(level, pos) call holds its arguments the same way. Asked anywhere else (the display
 * path), there is no level: it gets EmptyBlockGetter.INSTANCE at BlockPos.ZERO (release_notes_known.md).
 * The converters undo each other, so a predicate that crosses twice comes back as itself.
 */
public final class CbS26Emissive {
    private static final ThreadLocal<Object[]> ASKING = new ThreadLocal<>();
    private static volatile boolean adapted;

    private CbS26Emissive() {
    }

    /** true once a 26.1.2 StatePredicate was adapted: until then no 26.2 test needs a place and nothing is held */
    public static boolean adapted() {
        return adapted;
    }

    /** holds level/pos as the place the emissive test is asked; returns the outer place for {@link #leave} */
    public static Object[] enter(BlockGetter level, BlockPos pos) {
        Object[] outer = ASKING.get();
        ASKING.set(new Object[] {level, pos});
        return outer;
    }

    public static void leave(Object[] outer) {
        ASKING.set(outer);
    }

    /** 26.1.2 BlockStateBase.emissiveRendering(BlockGetter, BlockPos): 26.2's test, asked at the caller's level/pos */
    public static boolean emissiveRendering(BlockBehaviour.BlockStateBase self, BlockGetter level, BlockPos pos) {
        Object[] outer = enter(level, pos);
        try {
            return self.emissiveRendering();
        } finally {
            leave(outer);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Predicate toPredicate(BlockBehaviour.StatePredicate p) {
        if (p == null) {
            return null;
        }
        if (p instanceof AsStatePredicate a) {
            return a.predicate();
        }
        adapted = true;
        return new AsPredicate(p);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static BlockBehaviour.StatePredicate toStatePredicate(Predicate q) {
        if (q == null) {
            return null;
        }
        return q instanceof AsPredicate a ? a.statePredicate() : new AsStatePredicate(q);
    }

    /** 26.1.2 Properties.emissiveRendering field read */
    public static BlockBehaviour.StatePredicate getPropertiesEmissive(BlockBehaviour.Properties self) {
        return toStatePredicate(self.emissiveRendering);
    }

    /** 26.1.2 Properties.emissiveRendering field write */
    public static void setPropertiesEmissive(BlockBehaviour.Properties self, BlockBehaviour.StatePredicate p) {
        self.emissiveRendering = toPredicate(p);
    }

    /** 26.1.2 BlockStateBase.emissiveRendering field read */
    public static BlockBehaviour.StatePredicate getStateEmissive(BlockBehaviour.BlockStateBase self) {
        return toStatePredicate(self.emissiveRendering);
    }

    private record AsPredicate(BlockBehaviour.StatePredicate statePredicate) implements Predicate<BlockState> {
        @Override
        public boolean test(BlockState state) {
            Object[] at = ASKING.get();
            if (at == null) {
                return this.statePredicate.test(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            }
            return this.statePredicate.test(state, (BlockGetter) at[0], (BlockPos) at[1]);
        }
    }

    private record AsStatePredicate(Predicate<BlockState> predicate) implements BlockBehaviour.StatePredicate {
        @Override
        public boolean test(BlockState state, BlockGetter level, BlockPos pos) {
            Object[] outer = enter(level, pos);
            try {
                return this.predicate.test(state);
            } finally {
                leave(outer);
            }
        }
    }
}
