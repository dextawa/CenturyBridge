package top.dext.centurybridge.step.v26_2.rt;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;

/**
 * 26.1.2 TreeConfiguration.PLACE_BELOW_OVERWORLD_TRUNKS on 26.2 (javap -c of both): 26.1.2's &lt;clinit&gt; built it once as
 * RuleBasedStateProvider.ifTrueThenProvide(CAN_PLACE_BELOW_OVERWORLD_TRUNKS, Blocks.DIRT); 26.2 renamed the predicate
 * CAN_PLACE_BELOW_TREE_TRUNKS (same not(matchesTag(CANNOT_REPLACE_BELOW_TREE_TRUNK))) and builds the same provider on
 * each defaultPlaceBelowTreeTrunkProvider(biomes) call, ignoring the argument. One instance, built the same way, as
 * 26.1.2 held one.
 */
public final class CbS26Trees {
    private CbS26Trees() {}

    private static final class Holder {
        static final RuleBasedStateProvider PLACE_BELOW = RuleBasedStateProvider.ifTrueThenProvide(
            TreeConfiguration.CAN_PLACE_BELOW_TREE_TRUNKS, Blocks.DIRT);
    }

    /** 26.1.2 TreeConfiguration.PLACE_BELOW_OVERWORLD_TRUNKS (fieldGetters) */
    public static RuleBasedStateProvider placeBelowOverworldTrunks() {
        return Holder.PLACE_BELOW;
    }
}
