package top.dext.centurybridge.step.v26_1.rt;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import top.dext.centurybridge.step.v26_1.facade.RandomPatchConfiguration;

/**
 * 1.21.11's FeatureUtils patch helpers, which 26.1 removed with RandomPatchConfiguration (facade/RandomPatchConfiguration,
 * configuring this layer's RandomPatchFeature). Bodies are 1.21.11's (javap class_6803): a patch of tries placements
 * spread 7 horizontally and 3 vertically, each the feature filtered (PlacementUtils.filtered, which 26.1 keeps) by
 * 1.21.11's simplePatchPredicate: air, and on one of the given blocks when there are any.
 */
public final class CbWallFeatureUtils {
    private CbWallFeatureUtils() {
    }

    /** simpleRandomPatchConfiguration(tries, feature) */
    public static RandomPatchConfiguration simpleRandomPatchConfiguration(int tries, Holder<PlacedFeature> feature) {
        return new RandomPatchConfiguration(tries, 7, 3, feature);
    }

    /** simplePatchConfiguration(feature, config, allowedOn, tries) */
    public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(F feature, FC config,
        List<Block> allowedOn, int tries) {
        return simpleRandomPatchConfiguration(tries, PlacementUtils.filtered(feature, config, simplePatchPredicate(allowedOn)));
    }

    /** simplePatchConfiguration(feature, config, allowedOn): 96 tries */
    public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(F feature, FC config,
        List<Block> allowedOn) {
        return simplePatchConfiguration(feature, config, allowedOn, 96);
    }

    /** simplePatchConfiguration(feature, config): anywhere, 96 tries */
    public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(F feature, FC config) {
        return simplePatchConfiguration(feature, config, List.of(), 96);
    }

    /** 1.21.11's private simplePatchPredicate(allowedOn) */
    private static BlockPredicate simplePatchPredicate(List<Block> allowedOn) {
        return allowedOn.isEmpty() ? BlockPredicate.ONLY_IN_AIR_PREDICATE
            : BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.matchesBlocks(Direction.DOWN.getUnitVec3i(), allowedOn));
    }
}
