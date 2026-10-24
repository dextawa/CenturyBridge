package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import top.dext.centurybridge.step.v26_1.rt.CbWallStatics;

/**
 * 1.21.11's BlockBlobFeature (net/minecraft/class_2950), configured by a BlockStateConfiguration and placing
 * its blobs on dirt or stone (Feature.FOREST_ROCK). 26.1's BlockBlobFeature takes a BlockBlobConfiguration
 * (state and a can-place-on predicate); 1.21.11 code naming BlockBlobFeature gets this class, 1.21.11's body
 * ({@link #cb$place}; a mod's super.place(..) reaches it through CbWallFeatures).
 */
public class BlockBlobFeature extends Feature<BlockStateConfiguration> {

    /** 1.21.11 BlockBlobFeature(codec) */
    public BlockBlobFeature(Codec<BlockStateConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        return cb$place(context);
    }

    /** 1.21.11 BlockBlobFeature.place */
    public boolean cb$place(FeaturePlaceContext<BlockStateConfiguration> context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockStateConfiguration config = context.config();
        while (origin.getY() > level.getMinY() + 3) {
            if (!level.isEmptyBlock(origin.below())) {
                BlockState below = level.getBlockState(origin.below());
                if (CbWallStatics.isDirt(below) || CbWallStatics.isStone(below)) {
                    break;
                }
            }
            origin = origin.below();
        }
        if (origin.getY() <= level.getMinY() + 3) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            int xr = random.nextInt(2);
            int yr = random.nextInt(2);
            int zr = random.nextInt(2);
            float radius = (xr + yr + zr) * 0.333F + 0.5F;
            for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-xr, -yr, -zr), origin.offset(xr, yr, zr))) {
                if (pos.distSqr(origin) <= radius * radius) {
                    level.setBlock(pos, config.state, 3);
                }
            }
            origin = origin.offset(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
        }
        return true;
    }
}
