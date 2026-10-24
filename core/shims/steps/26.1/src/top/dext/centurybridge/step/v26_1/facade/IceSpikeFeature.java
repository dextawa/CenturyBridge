package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import top.dext.centurybridge.step.v26_1.rt.CbWallStatics;

/**
 * 1.21.11's IceSpikeFeature (net/minecraft/class_3070): 26.1 generalized it into SpikeFeature with a
 * configuration (ice_spike is a configured "spike" now) and removed Feature.ICE_SPIKE. The packed ice spike on
 * snow 1.21.11 placed, 1.21.11's body ({@link #cb$place}; a mod's super.place(..) reaches it through
 * CbWallFeatures, its blocks set through setBlock as 1.21.11's were, so a subclass's setBlock still sees them).
 */
public class IceSpikeFeature extends Feature<NoneFeatureConfiguration> {

    /** 1.21.11 IceSpikeFeature(codec) */
    public IceSpikeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        return cb$place(context);
    }

    private static boolean replaceable(BlockState state) {
        return state.isAir() || CbWallStatics.isDirt(state) || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.ICE);
    }

    /** 1.21.11 IceSpikeFeature.place */
    public boolean cb$place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        WorldGenLevel level = context.level();
        while (level.isEmptyBlock(origin) && origin.getY() > level.getMinY() + 2) {
            origin = origin.below();
        }
        if (!level.getBlockState(origin).is(Blocks.SNOW_BLOCK)) {
            return false;
        }
        origin = origin.above(random.nextInt(4));
        int height = random.nextInt(4) + 7;
        int width = height / 4 + random.nextInt(2);
        if (width > 1 && random.nextInt(60) == 0) {
            origin = origin.above(10 + random.nextInt(30));
        }
        for (int y = 0; y < height; y++) {
            float scale = (1.0F - (float) y / height) * width;
            int radius = Mth.ceil(scale);
            for (int dx = -radius; dx <= radius; dx++) {
                float fx = Mth.abs(dx) - 0.25F;
                for (int dz = -radius; dz <= radius; dz++) {
                    float fz = Mth.abs(dz) - 0.25F;
                    if ((dx == 0 && dz == 0 || !(fx * fx + fz * fz > scale * scale))
                            && (dx != -radius && dx != radius && dz != -radius && dz != radius || !(random.nextFloat() > 0.75F))) {
                        BlockState state = level.getBlockState(origin.offset(dx, y, dz));
                        if (replaceable(state)) {
                            this.setBlock(level, origin.offset(dx, y, dz), Blocks.PACKED_ICE.defaultBlockState());
                        }
                        if (y != 0 && radius > 1) {
                            state = level.getBlockState(origin.offset(dx, -y, dz));
                            if (replaceable(state)) {
                                this.setBlock(level, origin.offset(dx, -y, dz), Blocks.PACKED_ICE.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
        int maxRadius = width - 1;
        if (maxRadius < 0) {
            maxRadius = 0;
        } else if (maxRadius > 1) {
            maxRadius = 1;
        }
        for (int dx = -maxRadius; dx <= maxRadius; dx++) {
            for (int dz = -maxRadius; dz <= maxRadius; dz++) {
                BlockPos pos = origin.offset(dx, -1, dz);
                int run = 50;
                if (Math.abs(dx) == 1 && Math.abs(dz) == 1) {
                    run = random.nextInt(5);
                }
                while (pos.getY() > 50) {
                    BlockState state = level.getBlockState(pos);
                    if (!replaceable(state) && !state.is(Blocks.PACKED_ICE)) {
                        break;
                    }
                    this.setBlock(level, pos, Blocks.PACKED_ICE.defaultBlockState());
                    pos = pos.below();
                    if (--run <= 0) {
                        pos = pos.below(random.nextInt(5) + 1);
                        run = random.nextInt(5);
                    }
                }
            }
        }
        return true;
    }
}
