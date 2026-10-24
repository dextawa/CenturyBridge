package top.dext.centurybridge.step.v26_2.rt;

import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;

/**
 * 26.1.2's sixteen copper BlockFamilies constants, which 26.2 declares as two WeatheringCopperCollection<BlockFamily>
 * (COPPER_BLOCK, CUT_COPPER): each 26.1.2 constant is the family of its weathering state, unwaxed or waxed, in the
 * collection of its kind (the families themselves are 26.2's objects, built once in BlockFamilies' initializer).
 */
public final class CbS26BlockFamilies {
    private CbS26BlockFamilies() {}

    public static BlockFamily COPPER_BLOCK() {
        return BlockFamilies.COPPER_BLOCK.weathering().unaffected();
    }

    public static BlockFamily EXPOSED_COPPER() {
        return BlockFamilies.COPPER_BLOCK.weathering().exposed();
    }

    public static BlockFamily WEATHERED_COPPER() {
        return BlockFamilies.COPPER_BLOCK.weathering().weathered();
    }

    public static BlockFamily OXIDIZED_COPPER() {
        return BlockFamilies.COPPER_BLOCK.weathering().oxidized();
    }

    public static BlockFamily WAXED_COPPER_BLOCK() {
        return BlockFamilies.COPPER_BLOCK.waxed().unaffected();
    }

    public static BlockFamily WAXED_EXPOSED_COPPER() {
        return BlockFamilies.COPPER_BLOCK.waxed().exposed();
    }

    public static BlockFamily WAXED_WEATHERED_COPPER() {
        return BlockFamilies.COPPER_BLOCK.waxed().weathered();
    }

    public static BlockFamily WAXED_OXIDIZED_COPPER() {
        return BlockFamilies.COPPER_BLOCK.waxed().oxidized();
    }

    public static BlockFamily CUT_COPPER() {
        return BlockFamilies.CUT_COPPER.weathering().unaffected();
    }

    public static BlockFamily EXPOSED_CUT_COPPER() {
        return BlockFamilies.CUT_COPPER.weathering().exposed();
    }

    public static BlockFamily WEATHERED_CUT_COPPER() {
        return BlockFamilies.CUT_COPPER.weathering().weathered();
    }

    public static BlockFamily OXIDIZED_CUT_COPPER() {
        return BlockFamilies.CUT_COPPER.weathering().oxidized();
    }

    public static BlockFamily WAXED_CUT_COPPER() {
        return BlockFamilies.CUT_COPPER.waxed().unaffected();
    }

    public static BlockFamily WAXED_EXPOSED_CUT_COPPER() {
        return BlockFamilies.CUT_COPPER.waxed().exposed();
    }

    public static BlockFamily WAXED_WEATHERED_CUT_COPPER() {
        return BlockFamilies.CUT_COPPER.waxed().weathered();
    }

    public static BlockFamily WAXED_OXIDIZED_CUT_COPPER() {
        return BlockFamilies.CUT_COPPER.waxed().oxidized();
    }

}
