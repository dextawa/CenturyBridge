package top.dext.centurybridge.step.v26_2.rt;

import net.minecraft.world.level.block.MultifaceSpreadeableBlock;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;

/**
 * 26.1.2 MultifaceGrowthConfiguration.placeBlock was a MultifaceSpreadeableBlock; 26.2 widened the field to Block
 * (javap of both). 26.2's codec still validates it as a MultifaceSpreadeableBlock (validateBlock: instanceof, else
 * DataResult.error), and 26.2's MultifaceGrowthFeature casts the field to MultifaceSpreadeableBlock at each use, so a
 * 26.1.2 read gets the same object under 26.1.2's type -- the cast 26.2 itself makes.
 */
public final class CbS26Multiface {
    private CbS26Multiface() {}

    /** 26.1.2 config.placeBlock (instanceFieldGetters) */
    public static MultifaceSpreadeableBlock placeBlock(MultifaceGrowthConfiguration config) {
        return (MultifaceSpreadeableBlock) config.placeBlock;
    }
}
