package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's CauldronInteraction on 1.20.5's (area s4fin, core/cauldron).
 *
 * <p>1.20.4's interact(state, level, pos, player, hand, stack) answered an InteractionResult; 1.20.5's answers
 * an ItemInteractionResult, whose PASS_TO_DEFAULT_BLOCK_INTERACTION is what a cauldron's map answers for an
 * item it has no interaction for (1.20.4 answered PASS there; AbstractCauldronBlock then let the item and the
 * block's own use run, as 1.20.5's PASS_TO_DEFAULT_BLOCK_INTERACTION does). The two map one to one the way
 * 1.20.5's own ItemInteractionResult.result() does: SUCCESS, CONSUME, CONSUME_PARTIAL and FAIL by name, PASS
 * to PASS_TO_DEFAULT_BLOCK_INTERACTION and both PASS kinds back to PASS.
 *
 * <ul>
 * <li>a 1.20.4 call of interact on any interaction: 1.20.5's, its result as 1.20.4's ({@link #interact});</li>
 * <li>a 1.20.4 lambda / class implementing it: built as {@link Legacy} and adapted ({@link #adapt}) -- vanilla's
 *     maps hold the adapter, whose interact answers the lambda's result converted;</li>
 * <li>fillBucket / emptyBucket: 1.20.5's, the result converted.</li>
 * </ul>
 */
public final class CbFinItemCauldron {

    private CbFinItemCauldron() {
    }

    /** 1.20.4's CauldronInteraction shape */
    @FunctionalInterface
    public interface Legacy {
        net.minecraft.class_1269 interact(net.minecraft.class_2680 state, net.minecraft.class_1937 level, net.minecraft.class_2338 pos,
                                          net.minecraft.class_1657 player, net.minecraft.class_1268 hand, net.minecraft.class_1799 stack);
    }

    /** 1.20.5's result for 1.20.4's */
    public static net.minecraft.class_9062 toItemResult(net.minecraft.class_1269 r) {
        if (r == null) {
            return null;
        }
        if (r == net.minecraft.class_1269.field_5812) {
            return net.minecraft.class_9062.field_47728;
        }
        if (r == net.minecraft.class_1269.field_21466) {
            return net.minecraft.class_9062.field_47729;
        }
        if (r == net.minecraft.class_1269.field_33562) {
            return net.minecraft.class_9062.field_47730;
        }
        if (r == net.minecraft.class_1269.field_5814) {
            return net.minecraft.class_9062.field_47733;
        }
        return net.minecraft.class_9062.field_47731;
    }

    /** 1.20.4's result for 1.20.5's (ItemInteractionResult.result()) */
    public static net.minecraft.class_1269 toResult(net.minecraft.class_9062 r) {
        return r == null ? null : r.method_55645();
    }

    /** a 1.20.4 interaction as 1.20.5's */
    public static net.minecraft.class_5620 adapt(Legacy legacy) {
        if (legacy == null) {
            return null;
        }
        return (state, level, pos, player, hand, stack) -> toItemResult(legacy.interact(state, level, pos, player, hand, stack));
    }

    /** CauldronInteraction.interact(..) called by 1.20.4 code: 1.20.5's, the result as 1.20.4's */
    public static net.minecraft.class_1269 interact(net.minecraft.class_5620 i, net.minecraft.class_2680 state, net.minecraft.class_1937 level,
                                                    net.minecraft.class_2338 pos, net.minecraft.class_1657 player, net.minecraft.class_1268 hand,
                                                    net.minecraft.class_1799 stack) {
        return toResult(i.interact(state, level, pos, player, hand, stack));
    }

    /** CauldronInteraction.fillBucket(..) */
    public static net.minecraft.class_1269 method_32210(net.minecraft.class_2680 state, net.minecraft.class_1937 level, net.minecraft.class_2338 pos,
                                                        net.minecraft.class_1657 player, net.minecraft.class_1268 hand, net.minecraft.class_1799 empty,
                                                        net.minecraft.class_1799 filled, java.util.function.Predicate<net.minecraft.class_2680> fullness,
                                                        net.minecraft.class_3414 sound) {
        return toResult(net.minecraft.class_5620.method_32210(state, level, pos, player, hand, empty, filled, fullness, sound));
    }

    /** CauldronInteraction.emptyBucket(..) */
    public static net.minecraft.class_1269 method_32207(net.minecraft.class_1937 level, net.minecraft.class_2338 pos, net.minecraft.class_1657 player,
                                                        net.minecraft.class_1268 hand, net.minecraft.class_1799 filled, net.minecraft.class_2680 result,
                                                        net.minecraft.class_3414 sound) {
        return toResult(net.minecraft.class_5620.method_32207(level, pos, player, hand, filled, result, sound));
    }

    /** a 1.20.4 class implementing CauldronInteraction (its interact the 1.20.4 shape): 1.20.5's interact answers from it */
    public static net.minecraft.class_9062 interactOf(Legacy legacy, net.minecraft.class_2680 state, net.minecraft.class_1937 level,
                                                      net.minecraft.class_2338 pos, net.minecraft.class_1657 player, net.minecraft.class_1268 hand,
                                                      net.minecraft.class_1799 stack) {
        return toItemResult(legacy.interact(state, level, pos, player, hand, stack));
    }
}
