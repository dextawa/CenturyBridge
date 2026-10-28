package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10023;
import net.minecraft.class_11791;
import net.minecraft.class_1920;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_3568;
import net.minecraft.class_3610;
import net.minecraft.class_6539;
import net.minecraft.class_6880;

/**
 * FallingBlockRenderState, 1.21.8 -> 1.21.9 (javap of both code jars): the block 1.21.8's state held itself --
 * startBlockPos, blockPos, blockState, biome, level, and the BlockAndTintGetter answers over them -- moved into the
 * MovingBlockRenderState the 1.21.9 state holds (FallingBlockRenderState.movingBlockRenderState, made by its
 * constructor). FallingBlockRenderer.extractRenderState writes the same values in the same order into it (1.21.8 @32..67,
 * 1.21.9 @35..82): startBlockPos -> randomSeedPos (field_62245), blockPos (field_62246), blockState (field_62247),
 * biome (field_62248), level (field_62249); and MovingBlockRenderState's BlockAndTintGetter methods are 1.21.8's bodies.
 * A 1.21.8 read or write of the state's field, or call of its BlockAndTintGetter method, is the moving block's.
 */
@Environment(EnvType.CLIENT)
public final class CbFallingBlockState {

    private CbFallingBlockState() {
    }

    private static class_11791 moving(class_10023 state) {
        return state.field_62257;
    }

    public static class_2338 startBlockPos(class_10023 state) {
        return moving(state).field_62245;
    }

    public static void startBlockPos(class_10023 state, class_2338 value) {
        moving(state).field_62245 = value;
    }

    public static class_2338 blockPos(class_10023 state) {
        return moving(state).field_62246;
    }

    public static void blockPos(class_10023 state, class_2338 value) {
        moving(state).field_62246 = value;
    }

    public static class_2680 blockState(class_10023 state) {
        return moving(state).field_62247;
    }

    public static void blockState(class_10023 state, class_2680 value) {
        moving(state).field_62247 = value;
    }

    public static class_6880<class_1959> biome(class_10023 state) {
        return moving(state).field_62248;
    }

    public static void biome(class_10023 state, class_6880<class_1959> value) {
        moving(state).field_62248 = value;
    }

    public static class_1920 level(class_10023 state) {
        return moving(state).field_62249;
    }

    public static void level(class_10023 state, class_1920 value) {
        moving(state).field_62249 = value;
    }

    /** BlockAndTintGetter.getShade(direction, shade) */
    public static float method_24852(class_10023 state, class_2350 direction, boolean shade) {
        return moving(state).method_24852(direction, shade);
    }

    /** BlockAndTintGetter.getLightEngine() */
    public static class_3568 method_22336(class_10023 state) {
        return moving(state).method_22336();
    }

    /** BlockAndTintGetter.getBlockTint(pos, resolver) */
    public static int method_23752(class_10023 state, class_2338 pos, class_6539 resolver) {
        return moving(state).method_23752(pos, resolver);
    }

    /** BlockGetter.getBlockEntity(pos) */
    public static class_2586 method_8321(class_10023 state, class_2338 pos) {
        return moving(state).method_8321(pos);
    }

    /** BlockGetter.getBlockState(pos) */
    public static class_2680 method_8320(class_10023 state, class_2338 pos) {
        return moving(state).method_8320(pos);
    }

    /** BlockGetter.getFluidState(pos) */
    public static class_3610 method_8316(class_10023 state, class_2338 pos) {
        return moving(state).method_8316(pos);
    }

    /** LevelHeightAccessor.getHeight() */
    public static int method_31605(class_10023 state) {
        return moving(state).method_31605();
    }

    /** LevelHeightAccessor.getMinY() */
    public static int method_31607(class_10023 state) {
        return moving(state).method_31607();
    }
}
