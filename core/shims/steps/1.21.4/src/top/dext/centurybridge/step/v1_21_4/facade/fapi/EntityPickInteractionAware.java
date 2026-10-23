package top.dext.centurybridge.step.v1_21_4.facade.fapi;

import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_239;

/**
 * Fabric API's EntityPickInteractionAware of 1.21.3 (classRenames), removed for 1.21.4: an entity answering the
 * stack pick-block gives (CbRestFapiPickMixin asks it on the server, as 1.21.3's Fabric API did on the client).
 */
public interface EntityPickInteractionAware {
    class_1799 getPickedStack(class_1657 player, class_239 hit);
}
