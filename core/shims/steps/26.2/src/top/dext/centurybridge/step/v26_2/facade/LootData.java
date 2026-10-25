package top.dext.centurybridge.step.v26_2.facade;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import top.dext.centurybridge.step.v26_2.rt.CbRegistryConstants;

/**
 * 26.1.2's data-generation LootData (gone on 26.2): reached by classRenames, the same interface with the same
 * constant: 26.1.2's Maps.newEnumMap of the sixteen wool blocks, each 26.1.2 Blocks.&lt;COLOR&gt;_WOOL as the layer
 * gives it back (26.2's Blocks.WOOL.&lt;color&gt;(), rt/CbRegistryConstants).
 */
public interface LootData {
    Map<DyeColor, ItemLike> WOOL_ITEM_BY_DYE = Holder.build();

    final class Holder {
        private Holder() {}

        static Map<DyeColor, ItemLike> build() {
            EnumMap<DyeColor, ItemLike> m = new EnumMap<>(DyeColor.class);
        m.put(DyeColor.WHITE, CbRegistryConstants.Blocks_WHITE_WOOL());
        m.put(DyeColor.ORANGE, CbRegistryConstants.Blocks_ORANGE_WOOL());
        m.put(DyeColor.MAGENTA, CbRegistryConstants.Blocks_MAGENTA_WOOL());
        m.put(DyeColor.LIGHT_BLUE, CbRegistryConstants.Blocks_LIGHT_BLUE_WOOL());
        m.put(DyeColor.YELLOW, CbRegistryConstants.Blocks_YELLOW_WOOL());
        m.put(DyeColor.LIME, CbRegistryConstants.Blocks_LIME_WOOL());
        m.put(DyeColor.PINK, CbRegistryConstants.Blocks_PINK_WOOL());
        m.put(DyeColor.GRAY, CbRegistryConstants.Blocks_GRAY_WOOL());
        m.put(DyeColor.LIGHT_GRAY, CbRegistryConstants.Blocks_LIGHT_GRAY_WOOL());
        m.put(DyeColor.CYAN, CbRegistryConstants.Blocks_CYAN_WOOL());
        m.put(DyeColor.PURPLE, CbRegistryConstants.Blocks_PURPLE_WOOL());
        m.put(DyeColor.BLUE, CbRegistryConstants.Blocks_BLUE_WOOL());
        m.put(DyeColor.BROWN, CbRegistryConstants.Blocks_BROWN_WOOL());
        m.put(DyeColor.GREEN, CbRegistryConstants.Blocks_GREEN_WOOL());
        m.put(DyeColor.RED, CbRegistryConstants.Blocks_RED_WOOL());
        m.put(DyeColor.BLACK, CbRegistryConstants.Blocks_BLACK_WOOL());
            return m;
        }
    }
}
