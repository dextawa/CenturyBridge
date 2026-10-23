package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

/**
 * The client half of {@link CbWallTint}: 1.21.11's BlockAndTintGetter tint members on 26.1's client
 * BlockAndTintGetter, and the conversion of a 1.21.11 BlockAndTintGetter value (a BlockAndLightGetter in
 * 26.1 terms) into one where a 26.1 client API takes the client type.
 */
@Environment(EnvType.CLIENT)
public final class CbWallTintClient {

    /** what every 1.21.11 BlockAndTintGetter that is not a client one answered from getShade: 1.0 for each face */
    private static final CardinalLighting UNSHADED = new CardinalLighting(1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);

    private CbWallTintClient() {
    }

    /**
     * A 1.21.11 mod class implementing BlockAndTintGetter itself: its own getShade, which 26.1's
     * CardinalLighting stands for (its getBlockTint already implements 26.1's).
     */
    public interface LegacyBlockAndTintGetter extends BlockAndTintGetter {
        float getShade(Direction direction, boolean shade);

        @Override
        int getBlockTint(BlockPos pos, ColorResolver resolver);
    }

    /**
     * cardinalLighting() of a 1.21.11 implementation: its shaded value per face. 26.1 lights an unshaded
     * quad with up(); vanilla 1.21.11's unshaded shade equals its shaded up face (1.0, or 0.9 with nether
     * lighting), which this keeps.
     */
    public static CardinalLighting cardinalLighting(LegacyBlockAndTintGetter g) {
        return new CardinalLighting(g.getShade(Direction.DOWN, true), g.getShade(Direction.UP, true),
            g.getShade(Direction.NORTH, true), g.getShade(Direction.SOUTH, true),
            g.getShade(Direction.WEST, true), g.getShade(Direction.EAST, true));
    }

    /** getShade on a client getter: the mod's own for a 1.21.11 implementation, else 26.1's lighting; null for any other value */
    static Float shade(BlockAndLightGetter level, Direction direction, boolean shade) {
        if (level instanceof LegacyBlockAndTintGetter legacy) {
            return legacy.getShade(direction, shade);
        }
        if (level instanceof BlockAndTintGetter tint) {
            // 26.1's BlockModelLighter: byFace for a shaded quad, up() for an unshaded one -- 1.21.11's
            // ClientLevel.getShade values exactly
            CardinalLighting lighting = tint.cardinalLighting();
            return shade ? lighting.byFace(direction) : lighting.up();
        }
        return null;
    }

    /** getBlockTint on a client getter; null for any other value */
    static Integer tint(BlockAndLightGetter level, BlockPos pos, ColorResolver resolver) {
        return level instanceof BlockAndTintGetter tint ? tint.getBlockTint(pos, resolver) : null;
    }

    /**
     * A 1.21.11 BlockAndTintGetter value where a 26.1 client API takes the client type: itself when it is
     * one, a LevelReader (a server or world-gen level 1.21.11 passed as one) seen through its 1.21.11
     * shade and tint.
     */
    public static BlockAndTintGetter toTint(BlockAndLightGetter level) {
        if (level == null || level instanceof BlockAndTintGetter) {
            return (BlockAndTintGetter) level;
        }
        if (level instanceof LevelReader reader) {
            return new ReaderTint(reader);
        }
        throw new IllegalArgumentException(level + " was no 1.21.11 BlockAndTintGetter: it has no block tint");
    }

    /** 1.21.11 BiomeColors.getAverageGrassColor(BlockAndTintGetter, BlockPos) */
    public static int getAverageGrassColor(BlockAndLightGetter level, BlockPos pos) {
        return BiomeColors.getAverageGrassColor(toTint(level), pos);
    }

    /** 1.21.11 BiomeColors.getAverageFoliageColor */
    public static int getAverageFoliageColor(BlockAndLightGetter level, BlockPos pos) {
        return BiomeColors.getAverageFoliageColor(toTint(level), pos);
    }

    /** 1.21.11 BiomeColors.getAverageDryFoliageColor */
    public static int getAverageDryFoliageColor(BlockAndLightGetter level, BlockPos pos) {
        return BiomeColors.getAverageDryFoliageColor(toTint(level), pos);
    }

    /** 1.21.11 BiomeColors.getAverageWaterColor */
    public static int getAverageWaterColor(BlockAndLightGetter level, BlockPos pos) {
        return BiomeColors.getAverageWaterColor(toTint(level), pos);
    }

    /** a LevelReader with the shade and tint 1.21.11 gave it: 1.0 on every face, LevelReader's default tint */
    private record ReaderTint(LevelReader reader) implements BlockAndTintGetter {
        @Override
        public CardinalLighting cardinalLighting() {
            return UNSHADED;
        }

        @Override
        public int getBlockTint(BlockPos pos, ColorResolver resolver) {
            return CbWallTint.levelReaderTint(this.reader, pos, resolver);
        }

        @Override
        public LevelLightEngine getLightEngine() {
            return this.reader.getLightEngine();
        }

        @Override
        public BlockEntity getBlockEntity(BlockPos pos) {
            return this.reader.getBlockEntity(pos);
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return this.reader.getBlockState(pos);
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return this.reader.getFluidState(pos);
        }

        @Override
        public int getHeight() {
            return this.reader.getHeight();
        }

        @Override
        public int getMinY() {
            return this.reader.getMinY();
        }
    }
}
