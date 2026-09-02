package top.dext.centurybridge.compat.fapi;

import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2680;

/**
 * fabric-events-interaction-v0's BlockPickInteractionAware (removed in 1.21.4, where pick-block moved to the
 * server and became PlayerPickItemEvents.BLOCK). Blocks that implement it keep their override; CbInit
 * registers one PlayerPickItemEvents.BLOCK listener that asks it (7 corpus mods).
 * classRename: net/fabricmc/fabric/api/block/BlockPickInteractionAware -> this interface.
 */
public interface CbBlockPickInteractionAware {
    class_1799 getPickedStack(class_2680 state, class_1922 view, class_2338 pos, class_1657 player, class_239 result);
}
