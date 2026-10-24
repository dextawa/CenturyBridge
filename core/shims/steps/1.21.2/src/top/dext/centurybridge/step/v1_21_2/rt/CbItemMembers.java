package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_9285;
import net.minecraft.class_9334;

/**
 * 1.21.1's overridable Item members that 1.21.2 turned into item components or final methods, on 1.21.2.
 *
 * <p>Each was a question 1.21.1's game asked the item, which a mod item answered by overriding it:
 * <ul>
 * <li>getEnchantmentValue() -- the ENCHANTABLE component (EnchantmentHelper's enchanting-table cost and
 *     selection read it);</li>
 * <li>isEnchantable(stack) -- also ENCHANTABLE (ItemStack.isEnchantable: present, and the stack not
 *     enchanted yet);</li>
 * <li>isValidRepairItem(stack, ingredient) -- the REPAIRABLE component (the anvil);</li>
 * <li>getDefaultAttributeModifiers() -- the ATTRIBUTE_MODIFIERS component's default (ItemStack's
 *     forEachModifier read the item's when the stack's component was empty);</li>
 * <li>getDrinkingSound() / getEatingSound() -- the CONSUMABLE component's sound;</li>
 * <li>getDescriptionId(stack) / getDescription() / getOrCreateDescriptionId() -- the ITEM_NAME component
 *     and Item.getName(stack) (final getDescriptionId()).</li>
 * </ul>
 * A converted 1.21.1 item class that overrides one of these is given 1.21.1's answer back where 1.21.2
 * reads it (CbItemMembersMixin and the ItemStack / EnchantmentHelper mixins: the override, reached through
 * the Legacy* interface an adapter hook gives the class). A call of the member takes the static here: the
 * class's own override when it has one, else what 1.21.1's Item answered, computed on 1.21.2 -- which for
 * an item built with 1.21.1's API is 1.21.1's value (its components are what 1.21.1's Properties put
 * there: the values these members' defaults read).
 */
public final class CbItemMembers {

    // ---- the members a 1.21.1 item class overrides (adapterHooks) ----

    public interface LegacyEnchantmentValue {
        int method_7837();
    }

    public interface LegacyEnchantable {
        boolean method_7870(class_1799 stack);
    }

    public interface LegacyRepairItem {
        boolean method_7878(class_1799 stack, class_1799 ingredient);
    }

    public interface LegacyDefaultModifiers {
        class_9285 method_7844();
    }

    public interface LegacyDrinkingSound {
        class_3414 method_21831();
    }

    public interface LegacyEatingSound {
        class_3414 method_21830();
    }

    public interface LegacyDescriptionId {
        String method_7866(class_1799 stack);
    }

    public interface LegacyDescription {
        class_2561 method_7848();
    }

    /** a 1.21.1 item class's getDescriptionId() override, renamed (overrideRenames: 1.21.2's is final) */
    public interface LegacyDescriptionIdOverride {
        String cb$getDescriptionId();
    }

    /** a 1.21.1 item class's getOrCreateDescriptionId() override (adapterHooks) */
    public interface LegacyOrCreateDescriptionId {
        String method_7869();
    }

    public interface LegacyComplex {
        boolean method_16698();
    }

    public interface LegacyHasRemaining {
        boolean method_7857();
    }

    /**
     * what a 1.21.1 releaseUsing override answers 1.21.2's releaseUsing (the override hook's unwrap): false, no
     * after-use component effects -- a 1.21.1 item had none to apply
     */
    public static boolean releasedWithoutEffects() {
        return false;
    }

    // ---- calls ----

    /** getEnchantmentValue(): the item's ENCHANTABLE value, 0 without (1.21.1's Item answered 0) */
    public static int method_7837(class_1792 item) {
        if (item instanceof LegacyEnchantmentValue l) {
            return l.method_7837();
        }
        return super_method_7837(item);
    }

    /**
     * the components the item was built with -- not the view a 1.21.1 class's overrides answer
     * (CbItemMemberComponents), which a super call from such an override would otherwise come back through
     */
    public static net.minecraft.class_9323 built(class_1792 item) {
        net.minecraft.class_9323 c = item.method_57347();
        return c instanceof CbItemMemberComponents v ? v.built() : c;
    }

    /** 1.21.1's Item.getEnchantmentValue() on 1.21.2: what the item's components say */
    public static int super_method_7837(class_1792 item) {
        net.minecraft.class_9889 e = built(item).method_57829(class_9334.field_53695);
        return e == null ? 0 : e.comp_2938();
    }

    /**
     * isEnchantable(stack): 1.21.1's Item answered a stack of one with MAX_DAMAGE -- on 1.21.2 its ENCHANTABLE
     * component is the item's verdict, and a damageable 1.21.1 item has one by its enchantment value
     */
    public static boolean method_7870(class_1792 item, class_1799 stack) {
        if (item instanceof LegacyEnchantable l) {
            return l.method_7870(stack);
        }
        return super_method_7870(item, stack);
    }

    /** 1.21.1's Item.isEnchantable(stack): a single damageable stack */
    public static boolean super_method_7870(class_1792 item, class_1799 stack) {
        return stack.method_7914() == 1 && stack.method_57826(class_9334.field_50072);
    }

    /** isValidRepairItem(stack, ingredient): the class's own, else 1.21.2's REPAIRABLE answer for the ingredient */
    public static boolean method_7878(class_1792 item, class_1799 stack, class_1799 ingredient) {
        if (item instanceof LegacyRepairItem l) {
            return l.method_7878(stack, ingredient);
        }
        return super_method_7878(item, stack, ingredient);
    }

    /**
     * 1.21.1's Item.isValidRepairItem answered false; a 1.21.1 tool / armour piece repaired with its tier's or
     * material's ingredient -- on 1.21.2 that is the REPAIRABLE component the item was built with
     */
    public static boolean super_method_7878(class_1792 item, class_1799 stack, class_1799 ingredient) {
        net.minecraft.class_9890 r = built(item).method_57829(class_9334.field_53696);
        return r != null && r.method_61713(ingredient);
    }

    /** getDefaultAttributeModifiers(): the class's own, else the item's ATTRIBUTE_MODIFIERS component */
    public static class_9285 method_7844(class_1792 item) {
        if (item instanceof LegacyDefaultModifiers l) {
            return l.method_7844();
        }
        return super_method_7844(item);
    }

    /** 1.21.1's Item.getDefaultAttributeModifiers(): the EMPTY modifiers; the item's built components on 1.21.2 */
    public static class_9285 super_method_7844(class_1792 item) {
        class_9285 m = built(item).method_57829(class_9334.field_49636);
        return m == null ? class_9285.field_49326 : m;
    }

    /** getDrinkingSound(): the class's own, else 1.21.1's GENERIC_DRINK */
    public static class_3414 method_21831(class_1792 item) {
        return item instanceof LegacyDrinkingSound l ? l.method_21831() : super_method_21831(item);
    }

    public static class_3414 super_method_21831(class_1792 item) {
        return class_3417.field_20613.comp_349();
    }

    /** getEatingSound(): the class's own, else 1.21.1's GENERIC_EAT */
    public static class_3414 method_21830(class_1792 item) {
        return item instanceof LegacyEatingSound l ? l.method_21830() : super_method_21830(item);
    }

    public static class_3414 super_method_21830(class_1792 item) {
        return class_3417.field_20614.comp_349();
    }

    /** getDescriptionId(stack): the class's own, else the item's description id (1.21.1's Item answered that) */
    public static String method_7866(class_1792 item, class_1799 stack) {
        return item instanceof LegacyDescriptionId l ? l.method_7866(stack) : super_method_7866(item, stack);
    }

    public static String super_method_7866(class_1792 item, class_1799 stack) {
        return item.method_7876();
    }

    /** getDescription(): the class's own, else translatable(getDescriptionId()) -- 1.21.2's getName() */
    public static class_2561 method_7848(class_1792 item) {
        return item instanceof LegacyDescription l ? l.method_7848() : super_method_7848(item);
    }

    public static class_2561 super_method_7848(class_1792 item) {
        return class_2561.method_43471(item.method_7876());
    }

    /** getOrCreateDescriptionId(): the class's own, else 1.21.2's description id, made at construction (CbPendingIds) */
    public static String method_7869(class_1792 item) {
        return item instanceof LegacyOrCreateDescriptionId l ? l.method_7869() : super_method_7869(item);
    }

    /** 1.21.1's Item.getOrCreateDescriptionId(): "item." + the registry id, made once -- 1.21.2's descriptionId field */
    public static String super_method_7869(class_1792 item) {
        return ((CbPendingIdsItems.ItemFields) item).cb$descriptionId();
    }

    /** a 1.21.1 class's super.getDescriptionId() (from its renamed override): Item's body, the description id field */
    public static String super_method_7876(class_1792 item) {
        return item instanceof LegacyOrCreateDescriptionId l ? l.method_7869() : super_method_7869(item);
    }

    private static final ThreadLocal<java.util.Set<Object>> IN_DESCRIPTION_ID =
        ThreadLocal.withInitial(() -> java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>()));

    /** CbItemMembersMixin: whether the item's getDescriptionId is not already answering through its 1.21.1 override */
    public static boolean enterDescriptionId(Object item) {
        return IN_DESCRIPTION_ID.get().add(item);
    }

    public static void exitDescriptionId(Object item) {
        IN_DESCRIPTION_ID.get().remove(item);
    }

    /** isComplex(): the class's own, else 1.21.1's Item's false */
    public static boolean method_16698(class_1792 item) {
        return item instanceof LegacyComplex l ? l.method_16698() : super_method_16698(item);
    }

    /** 1.21.1's Item.isComplex(): false */
    public static boolean super_method_16698(class_1792 item) {
        return false;
    }

    /** getCraftingRemainingItem(): the item of 1.21.2's remainder stack, null when it is empty (1.21.1 answered null) */
    public static class_1792 method_7858(class_1792 item) {
        class_1799 r = item.method_7858();
        return r.method_7960() ? null : r.method_7909();
    }

    /** hasCraftingRemainingItem(): the class's own, else whether there is a remainder */
    public static boolean method_7857(class_1792 item) {
        return item instanceof LegacyHasRemaining l ? l.method_7857() : super_method_7857(item);
    }

    /** 1.21.1's Item.hasCraftingRemainingItem(): a remainder item set -- 1.21.2's remainder stack not empty */
    public static boolean super_method_7857(class_1792 item) {
        return !item.method_7858().method_7960();
    }

    /** ItemStack.getDescriptionId(): its item's getDescriptionId(stack) */
    public static String method_7922(class_1799 stack) {
        return method_7866(stack.method_7909(), stack);
    }

    /** ItemStack.getDrinkingSound() */
    public static class_3414 method_21832(class_1799 stack) {
        return method_21831(stack.method_7909());
    }

    /** ItemStack.getEatingSound() */
    public static class_3414 method_21833(class_1799 stack) {
        return method_21830(stack.method_7909());
    }

    /** ItemStack.hurtEnemy(target, player): 1.21.2's hurtEnemy(target, attacker) with the player as attacker */
    public static boolean method_7979(class_1799 stack, class_1309 target, class_1657 player) {
        return stack.method_7979(target, player);
    }

    /** ItemStack.postHurtEnemy(target, player) */
    public static void method_59979(class_1799 stack, class_1309 target, class_1657 player) {
        stack.method_59979(target, player);
    }

    // ---- where 1.21.2 reads what 1.21.1 asked the item (mixins) ----

    /** ENCHANTABLE as 1.21.1's class answers it: its getEnchantmentValue, absent at 0 (1.21.1's "not enchantable") */
    public static net.minecraft.class_9889 enchantable(class_1792 item, net.minecraft.class_9889 built) {
        if (item instanceof LegacyEnchantmentValue l) {
            int v = l.method_7837();
            return v > 0 ? new net.minecraft.class_9889(v) : null;
        }
        return built;
    }

    /** sound of a consumption as 1.21.1's class answered it, or null to keep the component's */
    public static class_3414 consumeSound(class_1799 stack, net.minecraft.class_1839 animation) {
        class_1792 item = stack.method_7909();
        if (animation == net.minecraft.class_1839.field_8946) {
            return item instanceof LegacyDrinkingSound l ? l.method_21831() : null;
        }
        return item instanceof LegacyEatingSound l ? l.method_21830() : null;
    }

    private CbItemMembers() {
    }
}
