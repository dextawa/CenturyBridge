package top.dext.centurybridge.step.v26_2.facade;

import com.google.common.collect.ImmutableBiMap;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopperCollection;

/**
 * 26.1.2's net.minecraft.world.item.WeatheringCopperItems, which 26.2 replaced by WeatheringCopperCollection<Item>.
 * The record and every member as 26.1.2 had them (javap of 26.1.2's class); of(..) builds one from 26.2's
 * collection for the 26.1.2 constants (Items.COPPER_BARS, ...) answered in rt/CbS26Copper. classRenames in s26.json
 * send a 26.1.2 mod's references here.
 */
public record WeatheringCopperItems(Item unaffected, Item exposed, Item weathered, Item oxidized, Item waxed,
                                    Item waxedExposed, Item waxedWeathered, Item waxedOxidized) {

    public static WeatheringCopperItems of(WeatheringCopperCollection<Item> c) {
        return new WeatheringCopperItems(c.weathering().unaffected(), c.weathering().exposed(), c.weathering().weathered(),
            c.weathering().oxidized(), c.waxed().unaffected(), c.waxed().exposed(), c.waxed().weathered(), c.waxed().oxidized());
    }

    /** 26.1.2: one item per block, through the caller's function, in the record's order */
    public static WeatheringCopperItems create(WeatheringCopperBlocks blocks, Function<Block, Item> itemFactory) {
        return new WeatheringCopperItems(itemFactory.apply(blocks.unaffected()), itemFactory.apply(blocks.exposed()),
            itemFactory.apply(blocks.weathered()), itemFactory.apply(blocks.oxidized()), itemFactory.apply(blocks.waxed()),
            itemFactory.apply(blocks.waxedExposed()), itemFactory.apply(blocks.waxedWeathered()), itemFactory.apply(blocks.waxedOxidized()));
    }

    public ImmutableBiMap<Item, Item> waxedMapping() {
        return ImmutableBiMap.of(this.unaffected, this.waxed, this.exposed, this.waxedExposed, this.weathered, this.waxedWeathered,
            this.oxidized, this.waxedOxidized);
    }

    public void forEach(Consumer<Item> action) {
        action.accept(this.unaffected);
        action.accept(this.exposed);
        action.accept(this.weathered);
        action.accept(this.oxidized);
        action.accept(this.waxed);
        action.accept(this.waxedExposed);
        action.accept(this.waxedWeathered);
        action.accept(this.waxedOxidized);
    }
}
