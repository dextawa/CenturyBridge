package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * 1.21.11's RandomPatchFeature (net/minecraft/class_4628): 26.1 removed it (Feature.RANDOM_PATCH, FLOWER and
 * NO_BONEMEAL_FLOWER were instances). Places the configuration's feature at tries random offsets within the
 * spread, 1.21.11's body ({@link #cb$place}; a mod's super.place(..) reaches it through CbWallFeatures).
 */
public class RandomPatchFeature extends Feature<RandomPatchConfiguration> {

    /** 1.21.11 RandomPatchFeature(codec) */
    public RandomPatchFeature(Codec<RandomPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RandomPatchConfiguration> context) {
        return cb$place(context);
    }

    /** 1.21.11 RandomPatchFeature.place */
    public boolean cb$place(FeaturePlaceContext<RandomPatchConfiguration> context) {
        RandomPatchConfiguration config = context.config();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        int placed = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int xz = config.comp_150() + 1;
        int y = config.comp_151() + 1;
        for (int i = 0; i < config.comp_149(); i++) {
            pos.setWithOffset(origin, random.nextInt(xz) - random.nextInt(xz), random.nextInt(y) - random.nextInt(y),
                random.nextInt(xz) - random.nextInt(xz));
            if (config.comp_155().value().place(level, context.chunkGenerator(), random, pos)) {
                placed++;
            }
        }
        return placed > 0;
    }
}
