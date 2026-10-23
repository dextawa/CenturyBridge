package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Optional;
import net.minecraft.class_1304;
import net.minecraft.class_1738;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1819;
import net.minecraft.class_2190;
import net.minecraft.class_3414;
import net.minecraft.class_4059;
import net.minecraft.class_5815;
import net.minecraft.class_6880;
import net.minecraft.class_9334;
import net.minecraft.class_10192;
import top.dext.centurybridge.step.v1_21_2.facade.ElytraItem;
import top.dext.centurybridge.step.v1_21_2.facade.Equipable;

/**
 * 1.21.1's Equipable on 1.21.2, where equipping reads the item's EQUIPPABLE component (the slot an item goes to in
 * LivingEntity.getEquipmentSlotForItem, the sound of onEquipItem, the swap of Item.use, the dispenser, armor damage).
 *
 * <p>1.21.1's vanilla Equipable classes implement facade Equipable again (mixins); their 1.21.2 components already
 * say what 1.21.1's members said. A converted 1.21.1 class that implements Equipable -- an item, or the block of a
 * block item -- is given EQUIPPABLE answered by its members on every read ({@link #equippable}, from
 * CbItemMemberComponents): its slot and sound, and for an item 1.21.1 did not otherwise equip, nothing else
 * 1.21.1 did -- not dispensed onto entities (1.21.1 registered that behaviour for armor and elytra only), not
 * swapped by Item.use (1.21.1's Item.use passed; a class swaps by calling swapWithEquipmentSlot), not damaged by
 * hits (1.21.1 damaged ArmorItems only) -- an ElytraItem subclass is dispensed and swapped, as 1.21.1's was. Where the item's class already builds an EQUIPPABLE (an ArmorItem
 * subclass overriding getEquipmentSlot / getEquipSound) only the slot and sound are its overrides'.
 */
public final class CbItemEquipable {

    /** whether an item's EQUIPPABLE is answered by 1.21.1 members: a class 1.21.2 does not equip by itself */
    public static boolean needed(class_1792 item) {
        Equipable e = source(item);
        return e != null && !vanillaEquipable(e);
    }

    /** 1.21.1's vanilla Equipable classes, which 1.21.2 equips from their own components */
    private static boolean vanillaEquipable(Equipable e) {
        Class<?> c = e.getClass();
        return c == class_1738.class || c == class_4059.class || c == class_1819.class
            || (e instanceof class_2190 || e instanceof class_5815) && c.getName().startsWith("net.minecraft.");
    }

    /** the Equipable an item's EQUIPPABLE stands for: the item, or its block */
    private static Equipable source(class_1792 item) {
        if (item instanceof Equipable e) {
            return e;
        }
        if (item instanceof class_1747 bi && bi.method_7711() instanceof Equipable e) {
            return e;
        }
        return null;
    }

    /** EQUIPPABLE of a converted 1.21.1 Equipable: its getEquipmentSlot() and getEquipSound() over what the item built */
    public static class_10192 equippable(class_1792 item, class_10192 built) {
        Equipable e = source(item);
        class_1304 slot = e.method_7685();
        class_6880<class_3414> sound = e.method_31570();
        if (built != null) {
            if (built.comp_3174() == slot && built.comp_3175() == sound) {
                return built;
            }
            return new class_10192(slot, sound, built.comp_3176(), built.comp_3306(), built.comp_3177(), built.comp_3178(),
                built.comp_3213(), built.comp_3214());
        }
        // 1.21.1's ElytraItem registered the armor dispense behaviour and swapped on use; it was no ArmorItem
        boolean elytra = e instanceof ElytraItem;
        return new class_10192(slot, sound, Optional.empty(), Optional.empty(), Optional.empty(), elytra, elytra, false);
    }

    /**
     * Equipable.get(stack) for an item 1.21.2 gives no Equipable class (the elytra, a plain item since 1.21.2): its
     * EQUIPPABLE component as an Equipable (slot, sound; the swap is the interface's), null without one
     */
    public static Equipable view(class_1792 item) {
        class_10192 eq = item.method_57347().method_57829(class_9334.field_54196);
        return eq == null ? null : new View(eq);
    }

    private record View(class_10192 equippable) implements Equipable {
        @Override
        public class_1304 method_7685() {
            return equippable.comp_3174();
        }

        @Override
        public class_6880<class_3414> method_31570() {
            return equippable.comp_3175();
        }
    }

    /** ElytraItem.use(level, player, hand) called through the class: Item.use's 1.21.1 shape (CbItemResults) */
    public static top.dext.centurybridge.step.v1_21_2.facade.InteractionResultHolder<net.minecraft.class_1799> method_7836(
            ElytraItem item, net.minecraft.class_1937 level, net.minecraft.class_1657 player, net.minecraft.class_1268 hand) {
        return CbItemResults.method_7836(item, level, player, hand);
    }

    private CbItemEquipable() {
    }
}
