package top.dext.centurybridge.step.v26_2.rt;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import top.dext.centurybridge.step.v26_2.facade.WeatheringCopperBlocks;
import top.dext.centurybridge.step.v26_2.facade.WeatheringCopperItems;

/**
 * 26.1.2's WeatheringCopperBlocks / WeatheringCopperItems constants (Blocks.COPPER_BARS, COPPER_CHAIN,
 * COPPER_LANTERN and the Items of the same names), which 26.2 declares as WeatheringCopperCollection: the same
 * eight blocks / items (the collection's weathering and waxed states, in the 26.1.2 record's order), one record per
 * constant built on first use and kept, as 26.1.2's static final field kept one.
 */
public final class CbS26Copper {
    private CbS26Copper() {}

    private static final class BlocksHolder {
        static final WeatheringCopperBlocks COPPER_BARS = WeatheringCopperBlocks.of(Blocks.COPPER_BARS);
        static final WeatheringCopperBlocks COPPER_CHAIN = WeatheringCopperBlocks.of(Blocks.COPPER_CHAIN);
        static final WeatheringCopperBlocks COPPER_LANTERN = WeatheringCopperBlocks.of(Blocks.COPPER_LANTERN);
    }

    private static final class ItemsHolder {
        static final WeatheringCopperItems COPPER_BARS = WeatheringCopperItems.of(Items.COPPER_BARS);
        static final WeatheringCopperItems COPPER_CHAIN = WeatheringCopperItems.of(Items.COPPER_CHAIN);
        static final WeatheringCopperItems COPPER_LANTERN = WeatheringCopperItems.of(Items.COPPER_LANTERN);
    }

    public static WeatheringCopperBlocks Blocks_COPPER_BARS() {
        return BlocksHolder.COPPER_BARS;
    }

    public static WeatheringCopperBlocks Blocks_COPPER_CHAIN() {
        return BlocksHolder.COPPER_CHAIN;
    }

    public static WeatheringCopperBlocks Blocks_COPPER_LANTERN() {
        return BlocksHolder.COPPER_LANTERN;
    }

    public static WeatheringCopperItems Items_COPPER_BARS() {
        return ItemsHolder.COPPER_BARS;
    }

    public static WeatheringCopperItems Items_COPPER_CHAIN() {
        return ItemsHolder.COPPER_CHAIN;
    }

    public static WeatheringCopperItems Items_COPPER_LANTERN() {
        return ItemsHolder.COPPER_LANTERN;
    }
}
