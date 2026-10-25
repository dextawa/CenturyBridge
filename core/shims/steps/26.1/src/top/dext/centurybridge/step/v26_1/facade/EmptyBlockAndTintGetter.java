package top.dext.centurybridge.step.v26_1.facade;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * 1.21.11's EmptyBlockAndTintGetter (net/minecraft/class_9891), gone in 26.1: the empty world a block model is
 * rendered against outside a level. One constant, INSTANCE, as 1.21.11 (values(), valueOf, ordinal alike), and
 * 1.21.11's answers: air everywhere, no block entity, empty fluid, height and min Y 0, the empty light engine,
 * shade 1.0 and tint -1.
 * <p>
 * It is a BlockAndLightGetter, what the layer renames 1.21.11's BlockAndTintGetter (class_1920) to. 1.21.11's
 * getShade / getBlockTint calls through class_1920 go to rt/CbWallTint, which answers this getter's own values.
 */
public enum EmptyBlockAndTintGetter implements BlockAndLightGetter {
    INSTANCE;

    /** 1.21.11 EmptyBlockAndTintGetter.getShade */
    public float getShade(Direction direction, boolean shade) {
        return 1.0F;
    }

    /** 1.21.11 EmptyBlockAndTintGetter.getBlockTint */
    public int getBlockTint(BlockPos pos, ColorResolver resolver) {
        return -1;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return LevelLightEngine.EMPTY;
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }
}
