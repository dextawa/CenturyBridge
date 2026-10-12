package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's block interaction -- Block.use(state, level, pos, player, hand, hit) and
 * BlockState.use(level, player, hand, hit) -- on 1.20.5, which split it in two:
 * useItemOn(stack, ...) for the item in that hand, then, when that answers
 * PASS_TO_DEFAULT_BLOCK_INTERACTION on the main hand, useWithoutItem(...). The three places
 * 1.20.5 interacts with a block (ServerPlayerGameMode.useItemOn, MultiPlayerGameMode's
 * performUseItemOn, GameTestHelper.useBlock) all go this way, after the same sneaking test 1.20.4
 * made before use; what 1.20.4's use answered for a hand is therefore what this sequence answers.
 *
 * Three directions:
 * <ul>
 * <li>a mod calling the old shapes (instance redirects method_9534 / method_26174): the
 *     receiver's 1.20.5 sequence for that hand;</li>
 * <li>vanilla calling a mod block whose class overrides the old use (override hooks, bridge
 *     mode): useItemOn forwards to the mod's use and answers its result as an item interaction --
 *     a result that does not consume the action is SKIP_DEFAULT_BLOCK_INTERACTION (FAIL stays
 *     FAIL), since in 1.20.4 the mod's use was the whole block interaction and the game went on
 *     to the item; useWithoutItem, which 1.20.5 only reaches after a PASS_TO_DEFAULT that such a
 *     useItemOn never answers, forwards to the mod's use for the main hand;</li>
 * <li>the mod's own super.use(..) (super bridge): super's 1.20.5 sequence, through the accessors
 *     the class carries ({@link Super}).</li>
 * </ul>
 */
public final class CbBlockUse {

    /** the 1.20.4 Block.use a mod block class overrides (adapter of the override hooks) */
    public interface Legacy {
        net.minecraft.class_1269 method_9534(net.minecraft.class_2680 state, net.minecraft.class_1937 level,
            net.minecraft.class_2338 pos, net.minecraft.class_1657 player, net.minecraft.class_1268 hand,
            net.minecraft.class_3965 hit);
    }

    /** super's useItemOn / useWithoutItem, reached from a mod block's super.use(..) */
    public interface Super {
        net.minecraft.class_9062 cb$super$method_55765(net.minecraft.class_1799 stack, net.minecraft.class_2680 state,
            net.minecraft.class_1937 level, net.minecraft.class_2338 pos, net.minecraft.class_1657 player,
            net.minecraft.class_1268 hand, net.minecraft.class_3965 hit);

        net.minecraft.class_1269 cb$super$method_55766(net.minecraft.class_2680 state, net.minecraft.class_1937 level,
            net.minecraft.class_2338 pos, net.minecraft.class_1657 player, net.minecraft.class_3965 hit);
    }

    /** the 1.20.4 BlockState.use a mod's BlockStateBase subclass overrides */
    public interface LegacyState {
        net.minecraft.class_1269 method_26174(net.minecraft.class_1937 level, net.minecraft.class_1657 player,
            net.minecraft.class_1268 hand, net.minecraft.class_3965 hit);
    }

    /** super's state useItemOn / useWithoutItem, reached from such a subclass's super.use(..) */
    public interface SuperState {
        net.minecraft.class_9062 cb$super$method_55780(net.minecraft.class_1799 stack, net.minecraft.class_1937 level,
            net.minecraft.class_1657 player, net.minecraft.class_1268 hand, net.minecraft.class_3965 hit);

        net.minecraft.class_1269 cb$super$method_55781(net.minecraft.class_1937 level, net.minecraft.class_1657 player,
            net.minecraft.class_3965 hit);
    }

    /** whether 1.20.5 goes on to useWithoutItem after this item interaction */
    private static boolean toDefault(net.minecraft.class_9062 r, net.minecraft.class_1268 hand) {
        return r == net.minecraft.class_9062.field_47731 && hand == net.minecraft.class_1268.field_5808;
    }

    /** Block.use(state, level, pos, player, hand, hit) called on any block */
    public static net.minecraft.class_1269 method_9534(net.minecraft.class_4970 self, net.minecraft.class_2680 state,
            net.minecraft.class_1937 level, net.minecraft.class_2338 pos, net.minecraft.class_1657 player,
            net.minecraft.class_1268 hand, net.minecraft.class_3965 hit) {
        CbBlockAccess block = (CbBlockAccess) self;
        net.minecraft.class_9062 r = block.cb$useItemOn(player.method_5998(hand), state, level, pos, player, hand, hit);
        return toDefault(r, hand) ? block.cb$useWithoutItem(state, level, pos, player, hit) : r.method_55645();
    }

    /** BlockState.use(level, player, hand, hit) called on any state */
    public static net.minecraft.class_1269 method_26174(net.minecraft.class_4970.class_4971 self,
            net.minecraft.class_1937 level, net.minecraft.class_1657 player, net.minecraft.class_1268 hand,
            net.minecraft.class_3965 hit) {
        net.minecraft.class_9062 r = self.method_55780(player.method_5998(hand), level, player, hand, hit);
        return toDefault(r, hand) ? self.method_55781(level, player, hit) : r.method_55645();
    }

    /** a mod block's use(..) result as 1.20.5's item interaction (see the class comment) */
    private static net.minecraft.class_9062 itemResult(net.minecraft.class_1269 r) {
        if (r.method_23665()) { // consumesAction: the interaction is decided
            if (r == net.minecraft.class_1269.field_21466) {
                return net.minecraft.class_9062.field_47729; // CONSUME
            }
            if (r == net.minecraft.class_1269.field_33562) {
                return net.minecraft.class_9062.field_47730; // CONSUME_PARTIAL
            }
            return net.minecraft.class_9062.field_47728; // SUCCESS (and SUCCESS_NO_ITEM_USED, which has no item form)
        }
        return r == net.minecraft.class_1269.field_5814
            ? net.minecraft.class_9062.field_47733  // FAIL
            : net.minecraft.class_9062.field_47732; // PASS: on to the item, no default block interaction
    }

    /** useItemOn of a mod block whose class overrides the 1.20.4 use (override hook bridge) */
    public static net.minecraft.class_9062 useItemOn(Legacy self, net.minecraft.class_1799 stack,
            net.minecraft.class_2680 state, net.minecraft.class_1937 level, net.minecraft.class_2338 pos,
            net.minecraft.class_1657 player, net.minecraft.class_1268 hand, net.minecraft.class_3965 hit) {
        return itemResult(self.method_9534(state, level, pos, player, hand, hit));
    }

    /** useWithoutItem of such a block: its use for the main hand */
    public static net.minecraft.class_1269 useWithoutItem(Legacy self, net.minecraft.class_2680 state,
            net.minecraft.class_1937 level, net.minecraft.class_2338 pos, net.minecraft.class_1657 player,
            net.minecraft.class_3965 hit) {
        return self.method_9534(state, level, pos, player, net.minecraft.class_1268.field_5808, hit);
    }

    /** a mod block's super.use(..): super's 1.20.5 sequence for that hand */
    public static net.minecraft.class_1269 superUse(Super self, net.minecraft.class_2680 state,
            net.minecraft.class_1937 level, net.minecraft.class_2338 pos, net.minecraft.class_1657 player,
            net.minecraft.class_1268 hand, net.minecraft.class_3965 hit) {
        net.minecraft.class_9062 r = self.cb$super$method_55765(player.method_5998(hand), state, level, pos, player, hand, hit);
        return toDefault(r, hand) ? self.cb$super$method_55766(state, level, pos, player, hit) : r.method_55645();
    }

    /** useItemOn of a mod's BlockStateBase subclass overriding the 1.20.4 use */
    public static net.minecraft.class_9062 stateUseItemOn(LegacyState self, net.minecraft.class_1799 stack,
            net.minecraft.class_1937 level, net.minecraft.class_1657 player, net.minecraft.class_1268 hand,
            net.minecraft.class_3965 hit) {
        return itemResult(self.method_26174(level, player, hand, hit));
    }

    /** useWithoutItem of such a state: its use for the main hand */
    public static net.minecraft.class_1269 stateUseWithoutItem(LegacyState self, net.minecraft.class_1937 level,
            net.minecraft.class_1657 player, net.minecraft.class_3965 hit) {
        return self.method_26174(level, player, net.minecraft.class_1268.field_5808, hit);
    }

    /** such a subclass's super.use(..) */
    public static net.minecraft.class_1269 superStateUse(SuperState self, net.minecraft.class_1937 level,
            net.minecraft.class_1657 player, net.minecraft.class_1268 hand, net.minecraft.class_3965 hit) {
        net.minecraft.class_9062 r = self.cb$super$method_55780(player.method_5998(hand), level, player, hand, hit);
        return toDefault(r, hand) ? self.cb$super$method_55781(level, player, hit) : r.method_55645();
    }

    private CbBlockUse() {
    }
}
