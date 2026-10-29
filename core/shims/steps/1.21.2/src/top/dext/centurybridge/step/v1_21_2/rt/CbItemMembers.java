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
        return ((ItemEnchantmentValue) (Object) item).method_7837();
    }

    /**
     * 1.21.1's Item.getEnchantmentValue(), restored on every Item by CbItemMembersRestoredMixin: a call dispatches to a
     * 1.21.1 class's override or to the restored body -- with whatever a mod's 1.21.1 mixin on it does
     */
    public interface ItemEnchantmentValue {
        int method_7837();
    }

    /**
     * 1.21.1's Item.isValidRepairItem(stack, ingredient), restored on every Item by CbItemMembersRestoredMixin (and as the
     * overrides of ArmorItem / ShieldItem / MaceItem, CbItemRepairOverridesMixin)
     */
    public interface ItemRepair {
        boolean method_7878(class_1799 stack, class_1799 ingredient);
    }

    /**
     * ItemStack.isValidRepairItem(ingredient) of an item whose class 1.21.1 did not override it in a mod: the
     * restored isValidRepairItem's answer where it differs from the layer's own body (a mod's mixin on it), else null
     * (1.21.2's own REPAIRABLE answer stands)
     */
    public static Boolean repairRead(class_1799 stack, class_1799 ingredient) {
        class_1792 item = stack.method_7909();
        boolean m = ((ItemRepair) (Object) item).method_7878(stack, ingredient);
        return m != vanilla_method_7878(item, stack, ingredient) ? m : null;
    }

    /**
     * the restored isValidRepairItem(stack, ingredient) bodies (CbItemRepairOverridesMixin, CbItemShieldRepairMixin,
     * CbItemMaceMembersMixin, CbItemMembersRestoredMixin), what the restored member answers without a mod's mixin:
     * ShieldItem planks or its super body, MaceItem a breeze rod, every other item the REPAIRABLE it was built with.
     * ShieldItem and MaceItem are unrelated, so testing them in turn is their virtual dispatch.
     */
    public static boolean vanilla_method_7878(class_1792 item, class_1799 stack, class_1799 ingredient) {
        if (item instanceof net.minecraft.class_1819) {
            return ingredient.method_31573(net.minecraft.class_3489.field_15537) || super_method_7878(item, stack, ingredient);
        }
        if (item instanceof net.minecraft.class_9362) {
            return ingredient.method_31574(net.minecraft.class_1802.field_49821);
        }
        return super_method_7878(item, stack, ingredient);
    }

    /**
     * the restored getEnchantmentValue() bodies (CbItemEnchantValueOverridesMixin, CbItemEnchantValueOneMixin,
     * CbItemMaceMembersMixin, CbItemMembersRestoredMixin), what the restored member answers without a mod's mixin:
     * 1.21.1's TridentItem / ProjectileWeaponItem / FishingRodItem 1, MaceItem 15, every other item (ArmorItem's
     * material value included) the ENCHANTABLE it was built with. Those classes are unrelated, so testing them in turn
     * is their virtual dispatch.
     */
    public static int vanilla_method_7837(class_1792 item) {
        if (item instanceof net.minecraft.class_1835 || item instanceof net.minecraft.class_1811
                || item instanceof net.minecraft.class_1787) {
            return 1;
        }
        if (item instanceof net.minecraft.class_9362) {
            return 15;
        }
        return super_method_7837(item);
    }

    /**
     * 1.21.1's Item.isEnchantable(stack), restored on every Item by CbItemMembersRestoredMixin: a call dispatches to a
     * 1.21.1 class's override or to the restored body -- with whatever a mod's 1.21.1 mixin on it does
     */
    public interface ItemEnchantable {
        boolean method_7870(class_1799 stack);
    }

    /**
     * ItemStack.isEnchantable() of an item whose class 1.21.1 did not override isEnchantable(stack) in a mod: 1.21.1's
     * answer (the restored member, and no enchantments yet) where the member answers otherwise than its body (a mod's
     * mixin on it), else null (1.21.2's ENCHANTABLE answer stands)
     */
    public static Boolean enchantableStackRead(class_1799 stack) {
        boolean m = ((ItemEnchantable) (Object) stack.method_7909()).method_7870(stack);
        if (m == super_method_7870(stack.method_7909(), stack)) {
            return null;
        }
        net.minecraft.class_9304 e = stack.method_57824(class_9334.field_49633);
        return m && e != null && e.method_57543();
    }

    /**
     * ENCHANTABLE as 1.21.2's EnchantmentHelper reads it (CbItemEnchantableReadsMixin), through the item's
     * restored getEnchantmentValue() as 1.21.1's EnchantmentHelper asked it: absent at 0 (1.21.1's "not
     * enchantable"). A stack carrying an ENCHANTABLE of its own (1.21.2 only; 1.21.1 had no per-stack value) keeps
     * it.
     */
    public static net.minecraft.class_9889 enchantableRead(class_1799 stack, net.minecraft.class_9889 read) {
        class_1792 item = stack.method_7909();
        if (!java.util.Objects.equals(read, item.method_57347().method_57829(class_9334.field_53695))) {
            return read;
        }
        int v = method_7837(item);
        if (!(item instanceof LegacyEnchantmentValue) && v == vanilla_method_7837(item)) {
            return read; // the restored member answers its own body: 1.21.2's component stands
        }
        if (v <= 0) {
            return null;
        }
        return read != null && read.comp_2938() == v ? read : new net.minecraft.class_9889(v);
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
     * isEnchantable(stack): the item's restored 1.21.1 member (CbItemMembersRestoredMixin) -- 1.21.1's Item answered a
     * stack of one with MAX_DAMAGE; 1.21.2's ItemStack.isEnchantable() follows it only where a mod changed it
     * (enchantableStackRead)
     */
    public static boolean method_7870(class_1792 item, class_1799 stack) {
        // the restored member (ItemEnchantable): a 1.21.1 class's override or the body, with whatever a mod's mixin on
        // it does
        return ((ItemEnchantable) (Object) item).method_7870(stack);
    }

    /** 1.21.1's Item.isEnchantable(stack): a single damageable stack */
    public static boolean super_method_7870(class_1792 item, class_1799 stack) {
        return stack.method_7914() == 1 && stack.method_57826(class_9334.field_50072);
    }

    /** isValidRepairItem(stack, ingredient): the class's own, else 1.21.2's REPAIRABLE answer for the ingredient (by dispatch) */
    public static boolean method_7878(class_1792 item, class_1799 stack, class_1799 ingredient) {
        // the restored member (ItemRepair): a 1.21.1 class's override, a restored vanilla override or the body, with
        // whatever a mod's mixin on it does
        return ((ItemRepair) (Object) item).method_7878(stack, ingredient);
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

    /**
     * getDrinkingSound(): the restored member (ItemSounds) -- a 1.21.1 class's override or 1.21.1's GENERIC_DRINK, with
     * whatever a mod's mixin on it does
     */
    public static class_3414 method_21831(class_1792 item) {
        return ((ItemSounds) (Object) item).method_21831();
    }

    /** 1.21.1's Item.getEatingSound() / getDrinkingSound(), restored on every Item by CbItemMembersRestoredMixin */
    public interface ItemSounds {
        class_3414 method_21830();

        class_3414 method_21831();
    }

    /** 1.21.1's ItemStack.getEatingSound() / getDrinkingSound(), restored on ItemStack by CbItemStackSoundsMixin */
    public interface StackSounds {
        class_3414 method_21832();

        class_3414 method_21833();
    }

    public static class_3414 super_method_21831(class_1792 item) {
        return class_3417.field_20613.comp_349();
    }

    /**
     * getEatingSound(): the restored member (ItemSounds) -- a 1.21.1 class's override or 1.21.1's GENERIC_EAT, with
     * whatever a mod's mixin on it does
     */
    public static class_3414 method_21830(class_1792 item) {
        return ((ItemSounds) (Object) item).method_21830();
    }

    public static class_3414 super_method_21830(class_1792 item) {
        return class_3417.field_20614.comp_349();
    }

    /**
     * getDescriptionId(stack): the restored member (ItemDescriptionId) -- a 1.21.1 class's override, a restored
     * vanilla override (Cb{Compass,Potion,TippedArrow,Shield}DescriptionIdMixin) or Item's body, with whatever a mod's mixin on it does
     */
    public static String method_7866(class_1792 item, class_1799 stack) {
        return ((ItemDescriptionId) (Object) item).method_7866(stack);
    }

    public static String super_method_7866(class_1792 item, class_1799 stack) {
        return item.method_7876();
    }

    /**
     * 1.21.1's Item.getDescriptionId(stack), restored on every Item by CbItemMembersRestoredMixin (and as the overrides of
     * CompassItem / PotionItem / TippedArrowItem / ShieldItem, Cb{Compass,Potion,TippedArrow,Shield}DescriptionIdMixin)
     */
    public interface ItemDescriptionId {
        String method_7866(class_1799 stack);
    }

    /**
     * 1.21.1's getDescriptionId(stack) bodies of the vanilla classes that overrode it (the restored members' bodies in
     * Cb{Compass,Potion,TippedArrow,Shield}DescriptionIdMixin), what the restored members
     * answer without a mod's mixin: CompassItem named a lodestone-tracking compass item.minecraft.lodestone_compass;
     * PotionItem and TippedArrowItem appended ".effect." and the potion's name (Potion.getName); ShieldItem appended
     * its base colour's name; every other item its description id. Those classes are unrelated, so testing them in
     * turn is their virtual dispatch. The description id is the field's (super_method_7869), not getDescriptionId(),
     * which a mod's mixin on getOrCreateDescriptionId() or a 1.21.1 class's override may answer otherwise -- a
     * difference 1.21.1's getName(stack) showed.
     */
    public static String vanilla_method_7866(class_1792 item, class_1799 stack) {
        if (item instanceof net.minecraft.class_1759) {
            return stack.method_57826(class_9334.field_49614) ? "item.minecraft.lodestone_compass" : super_method_7869(item);
        }
        if (item instanceof net.minecraft.class_1812 || item instanceof net.minecraft.class_1833) {
            net.minecraft.class_1844 contents = stack.method_57825(class_9334.field_49651, net.minecraft.class_1844.field_49274);
            return CbItemPotions.getName(contents.comp_2378(), super_method_7869(item) + ".effect.");
        }
        if (item instanceof net.minecraft.class_1819) {
            net.minecraft.class_1767 base = stack.method_57824(class_9334.field_49620);
            return base != null ? super_method_7869(item) + "." + base.method_7792() : super_method_7869(item);
        }
        return super_method_7869(item);
    }

    /**
     * Item.getName(stack) as 1.21.1 answered it where a mod changed the answer: translatable(getDescriptionId(stack))
     * when the stack carries no ITEM_NAME of its own (1.21.1's ItemStack.getHoverName took that first) and either
     * the item's class is a 1.21.1 one overriding getDescriptionId(stack) or the restored member answers otherwise
     * than its body (a mod's mixin on it); else null -- 1.21.2's own getName stands
     */
    public static class_2561 nameRead(class_1792 item, class_1799 stack) {
        if (stack.method_57380().method_57845(class_9334.field_50239) != null) {
            return null;
        }
        String m = ((ItemDescriptionId) (Object) item).method_7866(stack);
        if (m == null) {
            return null; // an item without its id yet (CbPendingIdsItems): 1.21.2's answer
        }
        if (item instanceof LegacyDescriptionId || !m.equals(vanilla_method_7866(item, stack))) {
            return class_2561.method_43471(m);
        }
        return null;
    }

    /**
     * getDescription(): the restored member (ItemDescription) -- a 1.21.1 class's override or the body,
     * translatable(getDescriptionId()), with whatever a mod's mixin on it does
     */
    public static class_2561 method_7848(class_1792 item) {
        return ((ItemDescription) (Object) item).method_7848();
    }

    /** 1.21.1's Item.getDescription(), restored on every Item by CbItemMembersRestoredMixin */
    public interface ItemDescription {
        class_2561 method_7848();
    }

    /**
     * Item.getName() (1.21.2's, final) as 1.21.1 answered the same question with getDescription(): a 1.21.1 class's
     * override, or the restored member where it answers otherwise than its body (a mod's mixin on it); else null --
     * 1.21.2's ITEM_NAME stands
     */
    public static class_2561 descriptionRead(class_1792 item) {
        if (item instanceof LegacyDescription l) {
            return l.method_7848();
        }
        class_2561 m = ((ItemDescription) (Object) item).method_7848();
        String id = super_method_7869(item);
        // the body's answer from the description id field: getDescriptionId() inside it may answer otherwise (a mod's
        // mixin on getOrCreateDescriptionId(), a 1.21.1 class's override), which 1.21.1's getDescription() showed
        return id == null || m.equals(class_2561.method_43471(id)) ? null : m;
    }

    public static class_2561 super_method_7848(class_1792 item) {
        return class_2561.method_43471(item.method_7876());
    }

    /** getOrCreateDescriptionId(): the class's own, else 1.21.2's description id, made at construction (CbPendingIds) */
    public static String method_7869(class_1792 item) {
        // the restored member (ItemOrCreateDescriptionId): a 1.21.1 class's override or the body, with whatever a mod's
        // mixin on it does
        return ((ItemOrCreateDescriptionId) (Object) item).method_7869();
    }

    /** 1.21.1's Item.getOrCreateDescriptionId(), restored on every Item by CbItemMembersRestoredMixin */
    public interface ItemOrCreateDescriptionId {
        String method_7869();
    }

    /**
     * Item.getDescriptionId() (1.21.2's, final) of an item whose class 1.21.1 did not override it: 1.21.1's
     * getDescriptionId() answered getOrCreateDescriptionId() -- the restored member -- where that answers otherwise than
     * the description id field (a mod's mixin on it); else null, the field stands. An item without its id yet (a
     * converted item before registration, CbPendingIdsItems) keeps 1.21.2's answer.
     */
    public static String orCreateDescriptionIdRead(class_1792 item) {
        String b = super_method_7869(item);
        if (b == null) {
            return null;
        }
        String m = ((ItemOrCreateDescriptionId) (Object) item).method_7869();
        return b.equals(m) ? null : m;
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

    /** ItemStack.getDrinkingSound(): the restored member (StackSounds) */
    public static class_3414 method_21832(class_1799 stack) {
        return ((StackSounds) (Object) stack).method_21832();
    }

    /** ItemStack.getEatingSound(): the restored member (StackSounds) */
    public static class_3414 method_21833(class_1799 stack) {
        return ((StackSounds) (Object) stack).method_21833();
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

    /**
     * sound of a consumption as 1.21.1's LivingEntity asked the stack -- its restored getDrinkingSound() for a drinking
     * animation, else getEatingSound() -- where that answers otherwise than 1.21.1's Item body (GENERIC_DRINK /
     * GENERIC_EAT): a 1.21.1 class's override or a mod's mixin on the restored member; else null to keep the
     * component's
     */
    public static class_3414 consumeSound(class_1799 stack, net.minecraft.class_1839 animation) {
        class_1792 item = stack.method_7909();
        if (animation == net.minecraft.class_1839.field_8946) {
            class_3414 m = ((StackSounds) (Object) stack).method_21832();
            return m != super_method_21831(item) ? m : null;
        }
        class_3414 m = ((StackSounds) (Object) stack).method_21833();
        return m != super_method_21830(item) ? m : null;
    }

    /**
     * UseAnim's constructor (name, ordinal) became (name, ordinal, id, serializedName) in 1.21.2. A constant a 1.21.1 mod
     * makes itself (Forge's enum extension through an @Invoker("<init>"), Kilt's UseAnimAccessor) takes what every
     * 1.21.2 constant has: the id equals the ordinal, and the serialized name is the constant name in lower case
     * (UseAnim.&lt;clinit&gt;: NONE 0 "none" ... TOOT_HORN 8 "toot_horn")
     */
    public static int useAnimId(int ordinal) {
        return ordinal;
    }

    /** see {@link #useAnimId} */
    public static String useAnimName(String name) {
        return name.toLowerCase(java.util.Locale.ROOT);
    }

    private CbItemMembers() {
    }
}
