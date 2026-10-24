package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * 1.21.11's HugeMushroomFeatureConfiguration(capProvider, stemProvider, foliageRadius): 26.1's record adds where the
 * mushroom may stand (canPlaceOn, tested on the block below). 1.21.11's AbstractHugeMushroomFeature.isValidPosition
 * asked the block below for Feature.isDirt (1.21.11's #minecraft:dirt) or #minecraft:mushroom_grow_block: 26.1's tags
 * of exactly those members are substrate_overworld and overrides_mushroom_light_requirement (26.1's own
 * huge_*_mushroom_can_place_on hold the same blocks).
 */
public final class CbWallMushrooms {

    private CbWallMushrooms() {
    }

    /** 1.21.11's placement test as a 26.1 predicate on the block below */
    public static BlockPredicate legacyCanPlaceOn() {
        return BlockPredicate.anyOf(BlockPredicate.matchesTag(BlockTags.SUBSTRATE_OVERWORLD),
            BlockPredicate.matchesTag(BlockTags.OVERRIDES_MUSHROOM_LIGHT_REQUIREMENT));
    }

    /** 1.21.11 new HugeMushroomFeatureConfiguration(capProvider, stemProvider, foliageRadius) */
    public static HugeMushroomFeatureConfiguration config(BlockStateProvider capProvider, BlockStateProvider stemProvider, int foliageRadius) {
        return new HugeMushroomFeatureConfiguration(capProvider, stemProvider, foliageRadius, legacyCanPlaceOn());
    }
}
