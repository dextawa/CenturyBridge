package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3965;
import top.dext.centurybridge.step.v1_21_2.facade.ItemInteractionResult;

/**
 * 1.21.1's Block.useItemOn / BlockState.useItemOn, which answered an ItemInteractionResult, on 1.21.2,
 * where they answer an InteractionResult (the facade's toResult / fromResult convert, see
 * {@link ItemInteractionResult}: PASS_TO_DEFAULT_BLOCK_INTERACTION is TRY_WITH_EMPTY_HAND, the one answer
 * after which 1.21.2's game modes go on to useWithoutItem, as 1.21.1's went on after PASS_TO_DEFAULT on
 * the main hand).
 *
 * <ul>
 * <li>vanilla asking a mod block whose class overrides the 1.21.1 shape: the override hook's new shape
 *     forwards and converts the answer (unwrap {@link ItemInteractionResult#toResult});</li>
 * <li>a mod calling the 1.21.1 shape: the call reshape takes 1.21.2's answer back
 *     ({@link ItemInteractionResult#fromResult});</li>
 * <li>the mod's own super.useItemOn(..): super's 1.21.2 useItemOn through the accessor the class
 *     carries ({@link Super}), its answer as 1.21.1's.</li>
 * </ul>
 */
public final class CbLevelUse {

    /** super's 1.21.2 Block.useItemOn, reached from a mod block's super.useItemOn(..) */
    public interface Super {
        class_1269 cb$super$method_55765(class_1799 stack, class_2680 state, class_1937 level, class_2338 pos,
                                         class_1657 player, class_1268 hand, class_3965 hit);
    }

    /** super's 1.21.2 BlockState.useItemOn, reached from a mod state's super.useItemOn(..) */
    public interface SuperState {
        class_1269 cb$super$method_55780(class_1799 stack, class_1937 level, class_1657 player, class_1268 hand,
                                         class_3965 hit);
    }

    public static ItemInteractionResult superUseItemOn(Super self, class_1799 stack, class_2680 state, class_1937 level,
                                                       class_2338 pos, class_1657 player, class_1268 hand, class_3965 hit) {
        return ItemInteractionResult.fromResult(self.cb$super$method_55765(stack, state, level, pos, player, hand, hit));
    }

    public static ItemInteractionResult superStateUseItemOn(SuperState self, class_1799 stack, class_1937 level,
                                                            class_1657 player, class_1268 hand, class_3965 hit) {
        return ItemInteractionResult.fromResult(self.cb$super$method_55780(stack, level, player, hand, hit));
    }

    private CbLevelUse() {
    }
}
