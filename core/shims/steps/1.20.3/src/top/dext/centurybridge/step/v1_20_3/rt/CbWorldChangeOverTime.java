package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_5819;

/**
 * 1.20.2's ChangeOverTimeBlock.applyChangeOverTime shape, as a 1.20.2 block overrides it. The converter adds
 * this interface to such a block, whose own method implements it, so CbWorldBlocks.legacyNextState can call
 * the override from 1.20.3's getNextState.
 */
public interface CbWorldChangeOverTime {
    void method_33623(class_2680 state, class_3218 level, class_2338 pos, class_5819 random);
}
