package top.dext.centurybridge.compat.fapi;

import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_239;

/**
 * fabric-events-interaction-v0's EntityPickInteractionAware (removed in 1.21.4 with the server-side pick
 * rework); see CbBlockPickInteractionAware -- CbInit forwards PlayerPickItemEvents.ENTITY here (3 corpus mods).
 * classRename: net/fabricmc/fabric/api/entity/EntityPickInteractionAware -> this interface.
 */
public interface CbEntityPickInteractionAware {
    class_1799 getPickedStack(class_1657 player, class_239 result);
}
