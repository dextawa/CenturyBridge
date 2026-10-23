package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1792;
import net.minecraft.class_1796;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

/**
 * 1.21.1's ItemCooldowns on 1.21.2.
 *
 * <p>1.21.1 kept cooldowns per Item; 1.21.2 keeps them per cooldown group, which for an item without a
 * USE_COOLDOWN group is the item's registry id (getCooldownGroup(stack)) -- every item a 1.21.1 mod has, so an
 * Item's group is its cooldowns entry exactly. A 1.21.1 call takes the group of the item's default stack; an
 * override of 1.21.1's shape in a converted subclass is reached where 1.21.2 calls the group shape
 * (overrideHooks: the stack's item, the group's item -- the item whose id the group is), and its super call
 * reaches 1.21.2's group shape again (the hook's guard lets that one through to super).
 */
public final class CbItemCooldowns {

    /** the cooldown group 1.21.1's per-item entry is on 1.21.2 */
    private static class_2960 group(class_1796 cooldowns, class_1792 item) {
        return cooldowns.method_62836(new class_1799(item));
    }

    // ---- the hooks' argument conversions (1.21.2 value -> what 1.21.1's override took) ----

    /** the stack 1.21.2 asks about, as the item 1.21.1 asked about */
    public static class_1792 item(class_1799 stack) {
        return stack.method_7909();
    }

    /** a cooldown group, as the item whose id it is (1.21.1's entries were items) */
    public static class_1792 item(class_2960 group) {
        return class_7923.field_41178.method_63535(group);
    }

    // ---- calls and super calls of 1.21.1's shapes ----

    /** isOnCooldown(item) */
    public static boolean method_7904(class_1796 cooldowns, class_1792 item) {
        return cooldowns.method_7904(new class_1799(item));
    }

    /** getCooldownPercent(item, partialTick) */
    public static float method_7905(class_1796 cooldowns, class_1792 item, float partialTick) {
        return cooldowns.method_7905(new class_1799(item), partialTick);
    }

    /** addCooldown(item, ticks) */
    public static void method_7906(class_1796 cooldowns, class_1792 item, int ticks) {
        cooldowns.method_7906(group(cooldowns, item), ticks);
    }

    /** removeCooldown(item) */
    public static void method_7900(class_1796 cooldowns, class_1792 item) {
        cooldowns.method_7900(group(cooldowns, item));
    }

    /** onCooldownStarted(item, ticks) (protected: through CbItemCooldownsInvoker) */
    public static void method_7902(class_1796 cooldowns, class_1792 item, int ticks) {
        ((top.dext.centurybridge.step.v1_21_2.mixin.CbItemCooldownsInvoker) cooldowns).cb$onCooldownStarted(group(cooldowns, item), ticks);
    }

    /** onCooldownEnded(item) (protected: through CbItemCooldownsInvoker) */
    public static void method_7901(class_1796 cooldowns, class_1792 item) {
        ((top.dext.centurybridge.step.v1_21_2.mixin.CbItemCooldownsInvoker) cooldowns).cb$onCooldownEnded(group(cooldowns, item));
    }

    /** ServerItemCooldowns.onCooldownStarted(item, ticks): sends the group's cooldown to the player */
    public static void method_7902(net.minecraft.class_1817 cooldowns, class_1792 item, int ticks) {
        method_7902((class_1796) cooldowns, item, ticks);
    }

    /** ServerItemCooldowns.onCooldownEnded(item) */
    public static void method_7901(net.minecraft.class_1817 cooldowns, class_1792 item) {
        method_7901((class_1796) cooldowns, item);
    }

    private CbItemCooldowns() {
    }
}
