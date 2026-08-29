package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

/**
 * Facade for 1.20.1 net/minecraft/class_1271 (TypedActionResult /
 * InteractionResultHolder&lt;T&gt;), folded into InteractionResult at 1.21.2:
 * a Success now carries the transformed held item itself (ItemContext) and
 * the other outcomes carry nothing. The facade keeps the old (result, value)
 * pair for mod code; {@link #cbFrom} / {@link #cbToVanilla} translate at the
 * boundaries vanilla still calls through (the Item.use override hook,
 * super.use / item.use from mod code, ItemUtils.startUsingInstantly, the
 * fabric UseItemCallback compat event).
 *
 * Member names are the 1.20.1 intermediary names: the converter renames the
 * dead class here and leaves the call sites' member names alone.
 */
public class class_1271<T> {

    private final InteractionResult field_5815;
    private final T field_5816;

    /** 1.20.1 TypedActionResult(ActionResult result, T value) */
    public class_1271(InteractionResult result, T value) {
        this.field_5815 = result;
        this.field_5816 = value;
    }

    /** getResult() */
    public InteractionResult method_5467() {
        return field_5815;
    }

    /** getValue() */
    public T method_5466() {
        return field_5816;
    }

    /** success(T) */
    public static <T> class_1271<T> method_22427(T value) {
        return new class_1271<>(InteractionResult.SUCCESS, value);
    }

    /** consume(T) */
    public static <T> class_1271<T> method_22428(T value) {
        return new class_1271<>(InteractionResult.CONSUME, value);
    }

    /** pass(T) */
    public static <T> class_1271<T> method_22430(T value) {
        return new class_1271<>(InteractionResult.PASS, value);
    }

    /** fail(T) */
    public static <T> class_1271<T> method_22431(T value) {
        return new class_1271<>(InteractionResult.FAIL, value);
    }

    /** success(T, boolean swingHand): 1.20.1 body = swingHand ? SUCCESS : CONSUME */
    public static <T> class_1271<T> method_29237(T value, boolean swingHand) {
        return new class_1271<>(swingHand ? InteractionResult.SUCCESS : InteractionResult.CONSUME, value);
    }

    @Override
    public String toString() {
        return "InteractionResultHolder{result=" + field_5815 + ", object=" + field_5816 + "}";
    }

    // ------------------------------------------------------------------
    // bridge boundary helpers (not 1.20.1 API; called from the runtime bridge)

    /**
     * Fold a Success back onto the singleton a 1.20.1 mod compares against
     * with ==: vanilla builds fresh Success records when it attaches an item
     * (heldItemTransformedTo), which would never be == SUCCESS. Fail / Pass
     * are records too, so they get the same treatment.
     */
    public static InteractionResult cbNormalize(InteractionResult r) {
        if (r instanceof InteractionResult.Success s) {
            switch (s.swingSource()) {
                case CLIENT:
                    return InteractionResult.SUCCESS;
                case SERVER:
                    return InteractionResult.SUCCESS_SERVER;
                default:
                    return InteractionResult.CONSUME;
            }
        }
        if (r instanceof InteractionResult.Fail) {
            return InteractionResult.FAIL;
        }
        if (r instanceof InteractionResult.Pass) {
            return InteractionResult.PASS;
        }
        return r;
    }

    /**
     * vanilla InteractionResult -> the pair a 1.20.1 caller expects. The value
     * is the transformed held item when the Success carries one, else the
     * stack the caller was holding (what 1.20.1 vanilla returned for pass /
     * fail / plain success).
     */
    public static class_1271<ItemStack> cbFrom(InteractionResult r, ItemStack held) {
        ItemStack value = held;
        if (r instanceof InteractionResult.Success s && s.heldItemTransformedTo() != null) {
            value = s.heldItemTransformedTo();
        }
        return new class_1271<>(cbNormalize(r == null ? InteractionResult.PASS : r), value);
    }

    /**
     * the pair -> vanilla InteractionResult. A Success carries the value as
     * the transformed held item (that is how 1.21.2+ applies the new stack);
     * for the other outcomes 1.20.1 vanilla also applied a differing stack,
     * which the new API cannot express -- documented loss.
     */
    public InteractionResult cbToVanilla() {
        InteractionResult r = field_5815 == null ? InteractionResult.PASS : field_5815;
        if (r instanceof InteractionResult.Success s && field_5816 instanceof ItemStack st) {
            return s.heldItemTransformedTo(st);
        }
        return r;
    }
}
