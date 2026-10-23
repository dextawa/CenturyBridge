package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1268;
import net.minecraft.class_1304;
import net.minecraft.class_1657;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1890;
import net.minecraft.class_1937;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3468;
import net.minecraft.class_6880;
import net.minecraft.class_9701;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemEquipable;

/**
 * 1.21.1's Equipable (net/minecraft/class_5151, classRenames), gone in 1.21.2, where an item is equipped by its
 * EQUIPPABLE component: what slot an item or block goes to, the sound it makes, and the swap into that slot.
 *
 * <p>1.21.1's vanilla Equipables -- ArmorItem (AnimalArmorItem with it), ShieldItem, AbstractSkullBlock,
 * WoolCarpetBlock, EquipableCarvedPumpkinBlock -- implement it again on 1.21.2 with 1.21.1's answers (the
 * CbItemEquipable*Mixin classes). ElytraItem became a plain item with components on 1.21.2; {@link #method_48957}
 * answers a view of its EQUIPPABLE component. A converted 1.21.1 class implementing this is given an EQUIPPABLE
 * component answered by its overrides (CbItemMemberComponents), so 1.21.2's game equips it as 1.21.1's did.
 */
public interface Equipable {

    /** getEquipmentSlot() */
    class_1304 method_7685();

    /** getEquipSound(): the generic armor equip sound */
    default class_6880<class_3414> method_31570() {
        return class_3417.field_14883;
    }

    /**
     * swapWithEquipmentSlot(item, level, player, hand): 1.21.1's body -- the held stack into the slot it equips to,
     * what was there into the hand; refused for a slot the player cannot use, a binding-cursed piece outside
     * creative, or the same stack; the use stat on the server; creative keeps a copy in hand
     */
    default InteractionResultHolder<class_1799> method_48576(class_1792 item, class_1937 level, class_1657 player, class_1268 hand) {
        class_1799 stack = player.method_5998(hand);
        class_1304 slot = player.method_32326(stack);
        if (!player.method_56991(slot)) {
            return InteractionResultHolder.method_22431(stack);
        }
        class_1799 current = player.method_6118(slot);
        if (class_1890.method_60142(current, class_9701.field_51656) && !player.method_7337()
                || class_1799.method_7973(stack, current)) {
            return InteractionResultHolder.method_22431(stack);
        }
        if (!level.method_8608()) {
            player.method_7259(class_3468.field_15372.method_14956(item));
        }
        class_1799 toHand = current.method_7960() ? stack : current.method_51164();
        class_1799 toSlot = player.method_7337() ? stack.method_7972() : stack.method_51164();
        player.method_5673(slot, toSlot);
        return InteractionResultHolder.method_29237(toHand, level.method_8608());
    }

    /**
     * get(stack): the item if it is Equipable, else the block of a block item if that is, else (1.21.2) a view of the
     * stack's item's EQUIPPABLE component for an item 1.21.2 no longer gives a class for (the elytra); null otherwise
     */
    static Equipable method_48957(class_1799 stack) {
        class_1792 item = stack.method_7909();
        if (item instanceof Equipable e) {
            return e;
        }
        if (item instanceof class_1747 bi && bi.method_7711() instanceof Equipable e) {
            return e;
        }
        return CbItemEquipable.view(item);
    }
}
