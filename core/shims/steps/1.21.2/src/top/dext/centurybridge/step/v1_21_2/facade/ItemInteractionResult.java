package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1269;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemResults;

/**
 * 1.21.1's ItemInteractionResult (net/minecraft/class_9062, classRenames), gone in 1.21.2: what
 * Block.useItemOn, BlockState.useItemOn, CauldronInteraction.interact and JukeboxPlayable answered.
 * 1.21.2 answers an InteractionResult there. The constants in 1.21.1's order (ordinals and switches
 * stay right), the intermediary field names kept as aliases, which is what 1.21.1 bytecode reads.
 *
 * <p>{@link #toResult} / {@link #fromResult} convert to and from the 1.21.2 result; they undo each other.
 * 1.21.1's game modes (ServerPlayerGameMode / MultiPlayerGameMode.useItemOn, GameTestHelper.useBlock)
 * returned result() of a result that consumed the action, went on to useWithoutItem after
 * PASS_TO_DEFAULT_BLOCK_INTERACTION on the main hand, and otherwise on to the item. 1.21.2's do the
 * same with a consuming result, TRY_WITH_EMPTY_HAND and anything else: PASS_TO_DEFAULT is
 * TRY_WITH_EMPTY_HAND, SKIP_DEFAULT is PASS (1.21.2's own blocks made exactly these two moves), and
 * the consuming constants are their result(), so the side swings and the stat is awarded as 1.21.1 did
 * ({@link CbItemResults}).
 */
public enum ItemInteractionResult {
    SUCCESS,
    CONSUME,
    CONSUME_PARTIAL,
    PASS_TO_DEFAULT_BLOCK_INTERACTION,
    SKIP_DEFAULT_BLOCK_INTERACTION,
    FAIL;

    /** SUCCESS (1.21.1 field) */
    public static final ItemInteractionResult field_47728 = SUCCESS;
    /** CONSUME (1.21.1 field) */
    public static final ItemInteractionResult field_47729 = CONSUME;
    /** CONSUME_PARTIAL (1.21.1 field) */
    public static final ItemInteractionResult field_47730 = CONSUME_PARTIAL;
    /** PASS_TO_DEFAULT_BLOCK_INTERACTION (1.21.1 field) */
    public static final ItemInteractionResult field_47731 = PASS_TO_DEFAULT_BLOCK_INTERACTION;
    /** SKIP_DEFAULT_BLOCK_INTERACTION (1.21.1 field) */
    public static final ItemInteractionResult field_47732 = SKIP_DEFAULT_BLOCK_INTERACTION;
    /** FAIL (1.21.1 field) */
    public static final ItemInteractionResult field_47733 = FAIL;

    /** consumesAction(): result().consumesAction() -- SUCCESS, CONSUME and CONSUME_PARTIAL */
    public boolean method_55643() {
        return this == SUCCESS || this == CONSUME || this == CONSUME_PARTIAL;
    }

    /** sidedSuccess(isClientSide): SUCCESS on the client, CONSUME on the server */
    public static ItemInteractionResult method_55644(boolean clientSide) {
        return clientSide ? SUCCESS : CONSUME;
    }

    /** result(): the InteractionResult of 1.21.1's switch -- the two block-interaction answers are PASS */
    public class_1269 method_55645() {
        return CbItemResults.of(switch (this) {
            case SUCCESS -> CbItemResults.SUCCESS;
            case CONSUME -> CbItemResults.CONSUME;
            case CONSUME_PARTIAL -> CbItemResults.CONSUME_PARTIAL;
            case PASS_TO_DEFAULT_BLOCK_INTERACTION, SKIP_DEFAULT_BLOCK_INTERACTION -> CbItemResults.PASS;
            case FAIL -> CbItemResults.FAIL;
        });
    }

    /** the 1.21.2 result standing for a 1.21.1 item interaction result; null for null */
    public static class_1269 toResult(ItemInteractionResult r) {
        if (r == null) {
            return null;
        }
        return switch (r) {
            case PASS_TO_DEFAULT_BLOCK_INTERACTION -> class_1269.field_52423;
            case SKIP_DEFAULT_BLOCK_INTERACTION -> class_1269.field_5811;
            default -> r.method_55645();
        };
    }

    /**
     * the 1.21.1 item interaction result a 1.21.2 result stands for: TRY_WITH_EMPTY_HAND is
     * PASS_TO_DEFAULT, PASS is SKIP_DEFAULT, FAIL is FAIL, a Success the consuming constant of its 1.21.1
     * reading (SUCCESS_NO_ITEM_USED, which has no item form, as SUCCESS); null for null
     */
    public static ItemInteractionResult fromResult(class_1269 r) {
        if (r == null) {
            return null;
        }
        if (r instanceof class_1269.class_9862) {
            return PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return switch (CbItemResults.ordinal(r)) {
            case CbItemResults.SUCCESS, CbItemResults.SUCCESS_NO_ITEM_USED -> SUCCESS;
            case CbItemResults.CONSUME -> CONSUME;
            case CbItemResults.CONSUME_PARTIAL -> CONSUME_PARTIAL;
            case CbItemResults.FAIL -> FAIL;
            default -> SKIP_DEFAULT_BLOCK_INTERACTION;
        };
    }
}
