package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;

/**
 * 1.20.2's Block.playerWillDestroy shape (void), as a 1.20.2 block overrides it. The converter adds this
 * interface to such a block, whose own method implements it, so CbWorldBlocks.playerWillDestroy can call
 * the override from the 1.20.3 shape (which returns a state).
 */
public interface CbWorldPlayerWillDestroy {
    void method_9576(class_1937 level, class_2338 pos, class_2680 state, class_1657 player);
}
