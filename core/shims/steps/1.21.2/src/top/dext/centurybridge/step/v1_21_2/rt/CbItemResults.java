package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import top.dext.centurybridge.step.v1_21_2.facade.InteractionResultHolder;

/**
 * 1.21.1's InteractionResult on 1.21.2. 1.21.1 had an enum of six constants (SUCCESS,
 * SUCCESS_NO_ITEM_USED, CONSUME, CONSUME_PARTIAL, PASS, FAIL); 1.21.2 has the sealed interface
 * InteractionResult with the records Success(swingSource, itemContext), Pass, Fail and
 * TryEmptyHandInteraction. The class keeps its name, so a 1.21.1 mod's values are 1.21.2 results;
 * what is gone is the enum surface, given back here.
 *
 * <p>What each 1.21.1 constant did, and the 1.21.2 result that does the same:
 * <ul>
 * <li>SUCCESS: consumes the action, swings the hand on the side that answered it (1.21.1's
 *     ServerGamePacketListenerImpl swung on the server for shouldSwing(), the client's Minecraft on the
 *     client), awards the item's use stat. 1.21.2 swings on the side its SwingSource names: on the
 *     server thread SUCCESS_SERVER, elsewhere SUCCESS (client swing).</li>
 * <li>SUCCESS_NO_ITEM_USED: SUCCESS without the stat: that result withoutItem() (1.21.2's Wolf made
 *     the same move).</li>
 * <li>CONSUME: consumes, no swing, awards the stat: 1.21.2's CONSUME.</li>
 * <li>CONSUME_PARTIAL: consumes, no swing, no stat (shouldAwardStats was SUCCESS or CONSUME):
 *     CONSUME.withoutItem().</li>
 * <li>PASS, FAIL: PASS, FAIL.</li>
 * </ul>
 * Back from a 1.21.2 result ({@link #ordinal}): a Success is SUCCESS when it swings on the side
 * asking and CONSUME when it does not, either without "_NO_ITEM_USED"/"_PARTIAL" exactly when its
 * item context says the item was used; Fail is FAIL; Pass and TryEmptyHandInteraction are PASS
 * (1.21.1's ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION.result() was PASS). The two
 * directions undo each other on each side, so values(), ordinal() and a switch over the enum keep
 * working.
 *
 * <p>1.21.1's InteractionResultHolder (gone in 1.21.2, {@link InteractionResultHolder}) and Item.use,
 * which answered one, are bridged here too: 1.21.2's Item.use answers an InteractionResult whose
 * Success carries the stack the hand turns into.
 */
public final class CbItemResults {

    /** 1.21.1's ordinals */
    public static final int SUCCESS = 0, SUCCESS_NO_ITEM_USED = 1, CONSUME = 2, CONSUME_PARTIAL = 3, PASS = 4, FAIL = 5;

    private static final String[] NAMES = {"SUCCESS", "SUCCESS_NO_ITEM_USED", "CONSUME", "CONSUME_PARTIAL", "PASS", "FAIL"};

    /** one instance per 1.21.1 constant and side, so that a constant read twice is the same object, as an enum's was */
    private static final class_1269 SUCCESS_CLIENT = class_1269.field_5812;
    private static final class_1269 SUCCESS_SERVER = class_1269.field_52422;
    private static final class_1269 NO_ITEM_CLIENT = class_1269.field_5812.method_61394();
    private static final class_1269 NO_ITEM_SERVER = class_1269.field_52422.method_61394();
    private static final class_1269 CONSUME_ALL = class_1269.field_21466;
    private static final class_1269 CONSUME_PART = class_1269.field_21466.method_61394();

    /** the thread the running (dedicated or integrated) server ticks on, set by CbItemServerThreadMixin */
    private static volatile Thread serverThread;

    public static void serverThread(Thread t) {
        serverThread = t;
    }

    /** whether the result being made or read belongs to the logical server */
    public static boolean onServer() {
        return Thread.currentThread() == serverThread;
    }

    /** the 1.21.2 result of a 1.21.1 constant, by ordinal, for the side asking */
    public static class_1269 of(int ordinal) {
        boolean server = onServer();
        return switch (ordinal) {
            case SUCCESS -> server ? SUCCESS_SERVER : SUCCESS_CLIENT;
            case SUCCESS_NO_ITEM_USED -> server ? NO_ITEM_SERVER : NO_ITEM_CLIENT;
            case CONSUME -> CONSUME_ALL;
            case CONSUME_PARTIAL -> CONSUME_PART;
            case PASS -> class_1269.field_5811;
            case FAIL -> class_1269.field_5814;
            default -> throw new IllegalArgumentException("no 1.21.1 InteractionResult of ordinal " + ordinal);
        };
    }

    /**
     * the 1.21.1 constant (its ordinal) that does what this 1.21.2 result does on the side asking; also
     * ordinal() called on a result (instance redirect)
     */
    public static int ordinal(class_1269 r) {
        java.util.Objects.requireNonNull(r);
        if (r instanceof class_1269.class_9860 s) {
            boolean swings = s.comp_2909() == (onServer() ? class_1269.class_9861.field_52428 : class_1269.class_9861.field_52427);
            boolean item = s.method_61395();
            return swings ? (item ? SUCCESS : SUCCESS_NO_ITEM_USED) : (item ? CONSUME : CONSUME_PARTIAL);
        }
        return r instanceof class_1269.class_9857 ? FAIL : PASS;
    }

    // ---- the enum constants (fieldGetters) ----

    /** SUCCESS */
    public static class_1269 field_5812() {
        return of(SUCCESS);
    }

    /** SUCCESS_NO_ITEM_USED */
    public static class_1269 field_51370() {
        return of(SUCCESS_NO_ITEM_USED);
    }

    /** CONSUME */
    public static class_1269 field_21466() {
        return of(CONSUME);
    }

    /** CONSUME_PARTIAL */
    public static class_1269 field_33562() {
        return of(CONSUME_PARTIAL);
    }

    /** PASS */
    public static class_1269 field_5811() {
        return of(PASS);
    }

    /** FAIL */
    public static class_1269 field_5814() {
        return of(FAIL);
    }

    // ---- the enum's members ----

    /** values() */
    public static class_1269[] values() {
        class_1269[] out = new class_1269[NAMES.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = of(i);
        }
        return out;
    }

    /** valueOf(name), as Enum.valueOf answers */
    public static class_1269 valueOf(String name) {
        java.util.Objects.requireNonNull(name, "Name is null");
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equals(name)) {
                return of(i);
            }
        }
        throw new IllegalArgumentException("No enum constant net.minecraft.class_1269." + name);
    }

    /** name() */
    public static String name(class_1269 self) {
        return NAMES[ordinal(self)];
    }

    /** toString(): an enum constant's name */
    public static String toString(class_1269 self) {
        return NAMES[ordinal(self)];
    }

    /** shouldSwing(): SUCCESS and SUCCESS_NO_ITEM_USED */
    public static boolean method_23666(class_1269 self) {
        int o = ordinal(self);
        return o == SUCCESS || o == SUCCESS_NO_ITEM_USED;
    }

    /** shouldAwardStats(): SUCCESS and CONSUME */
    public static boolean method_36360(class_1269 self) {
        int o = ordinal(self);
        return o == SUCCESS || o == CONSUME;
    }

    /** sidedSuccess(isClientSide): the SUCCESS constant when the caller says client side, else CONSUME */
    public static class_1269 method_29236(boolean clientSide) {
        return of(clientSide ? SUCCESS : CONSUME);
    }

    // ---- InteractionResultHolder and Item.use ----

    /** the 1.21.1 Item.use a mod item class overrides (adapter of the override hook) */
    public interface LegacyUse {
        InteractionResultHolder<class_1799> method_7836(class_1937 level, class_1657 player, class_1268 hand);
    }

    /** super's 1.21.2 Item.use, reached from a mod item's super.use(..) */
    public interface SuperUse {
        class_1269 cb$super$method_7836(class_1937 level, class_1657 player, class_1268 hand);
    }

    /**
     * A 1.21.2 use result as 1.21.1's holder: the result, and the stack the hand holds afterwards -- the
     * stack a Success says the hand turns into, else the one in the hand (what 1.21.1's Item.use put in
     * its holder unless it replaced the stack). The result is the 1.21.1 constant it reads as ({@link #of}),
     * so that a mod comparing it with a constant by identity -- as one did an enum -- sees the same
     * object; what the 1.21.2 Success carried beyond that is the holder's stack.
     */
    public static InteractionResultHolder<class_1799> holder(class_1269 r, class_1657 player, class_1268 hand) {
        class_1799 v = r instanceof class_1269.class_9860 s && s.method_61396() != null ? s.method_61396() : player.method_5998(hand);
        return new InteractionResultHolder<>(of(ordinal(r)), v);
    }

    /**
     * What a mod's 1.21.1 use answered, as 1.21.2's result. A Success carries the holder's stack as the
     * stack the hand turns into, keeping its swing and whether the item was used (1.21.2's game modes
     * put it in the hand, as 1.21.1's put the holder's object there). For PASS and FAIL 1.21.2 keeps the
     * hand as it is, where 1.21.1's game modes put the holder's object in it when it was another stack
     * -- the server except when a FAIL's stack has a use duration and the player is not using an item,
     * the client always -- so that is done here.
     */
    public static class_1269 result(InteractionResultHolder<?> h, class_1657 player, class_1268 hand) {
        class_1269 r = h.method_5467();
        if (!(h.method_5466() instanceof class_1799 v)) {
            return r;
        }
        if (r instanceof class_1269.class_9860 s) {
            return new class_1269.class_9860(s.comp_2909(), new class_1269.class_9858(s.method_61395(), v));
        }
        if (v != player.method_5998(hand)) {
            boolean keep = onServer() && r instanceof class_1269.class_9857 && v.method_7935(player) > 0 && !player.method_6115();
            if (!keep) {
                player.method_6122(hand, v);
            }
        }
        return r;
    }

    /** Item.use of a mod item whose class overrides the 1.21.1 shape (override hook bridge) */
    public static class_1269 use(LegacyUse self, class_1937 level, class_1657 player, class_1268 hand) {
        return result(self.method_7836(level, player, hand), player, hand);
    }

    /** a mod item's super.use(..): super's 1.21.2 use, as a holder */
    public static InteractionResultHolder<class_1799> superUse(SuperUse self, class_1937 level, class_1657 player, class_1268 hand) {
        return holder(self.cb$super$method_7836(level, player, hand), player, hand);
    }

    /**
     * item.use(level, player, hand) called on any item: a converted 1.21.1 item answers its own holder,
     * as it did; any other item its 1.21.2 use, as a holder
     */
    public static InteractionResultHolder<class_1799> method_7836(class_1792 self, class_1937 level, class_1657 player, class_1268 hand) {
        if (self instanceof LegacyUse legacy) {
            return legacy.method_7836(level, player, hand);
        }
        return holder(self.method_7836(level, player, hand), player, hand);
    }

    /** stack.use(level, player, hand): 1.21.1's was its item's use */
    public static InteractionResultHolder<class_1799> method_7913(class_1799 self, class_1937 level, class_1657 player, class_1268 hand) {
        if (self.method_7909() instanceof LegacyUse legacy) {
            return legacy.method_7836(level, player, hand);
        }
        return holder(self.method_7913(level, player, hand), player, hand);
    }

    /** ItemUtils.startUsingInstantly: 1.21.1 answered consume(the stack in the hand) */
    public static InteractionResultHolder<class_1799> method_29282(class_1937 level, class_1657 player, class_1268 hand) {
        return holder(net.minecraft.class_5328.method_29282(level, player, hand), player, hand);
    }

    private CbItemResults() {
    }
}
