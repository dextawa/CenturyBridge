package top.dext.centurybridge.step.v1_20_5.compat.fapi.mininglevel.v1;

import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.mininglevel.CbMiningLevelManagerImpl;

/**
 * Fabric API 0.97.3's (1.20.4) fabric-mining-level-api-v1 MiningLevelManager: a block state's required
 * mining level from #fabric:needs_tool_level_N and the vanilla needs_*_tool tags, and the tag of a level.
 * Bodies as in 1.20.4 (Fabric API, Apache-2.0); 1.20.5 kept those vanilla tags under the same ids.
 */
public final class MiningLevelManager {

    private MiningLevelManager() {
    }

    public static int getRequiredMiningLevel(net.minecraft.class_2680 state) {
        return CbMiningLevelManagerImpl.getRequiredMiningLevel(state);
    }

    public static net.minecraft.class_6862<net.minecraft.class_2248> getBlockTag(int miningLevel) {
        if (miningLevel <= 0) {
            throw new IllegalArgumentException("Mining level tags only exist for mining levels > 0, received: " + miningLevel);
        }
        return switch (miningLevel) {
            case 1 -> net.minecraft.class_3481.field_33719;
            case 2 -> net.minecraft.class_3481.field_33718;
            case 3 -> net.minecraft.class_3481.field_33717;
            default -> net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41254,
                new net.minecraft.class_2960("fabric", "needs_tool_level_" + miningLevel));
        };
    }
}
