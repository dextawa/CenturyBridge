package top.dext.centurybridge.step.v1_21_4.facade.fapi;

import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2680;

/**
 * Fabric API's BlockPickInteractionAware of 1.21.3 (classRenames), removed from fabric-events-interaction-v0
 * for 1.21.4: a block answering the stack pick-block gives. 1.21.3's Fabric API asked it on the client when the
 * player picked the block and used its stack when not empty; 1.21.4 picks on the server, where
 * CbRestFapiPickMixin asks it the same way.
 */
public interface BlockPickInteractionAware {
    class_1799 getPickedStack(class_2680 state, class_1922 view, class_2338 pos, class_1657 player, class_239 hit);
}
