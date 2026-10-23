package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1889;
import net.minecraft.class_1890;
import net.minecraft.class_9323;
import net.minecraft.class_9334;
import net.minecraft.class_10128;
import net.minecraft.class_10129;
import net.minecraft.class_10130;
import net.minecraft.class_10131;

/**
 * The components that are 1.21.1's consumable item classes (MilkBucketItem, HoneyBottleItem, OminousBottleItem,
 * ChorusFruitItem: facades) on 1.21.2, where the vanilla items of the same name are plain items with exactly these
 * components: put over what the item's Properties built, after Item's constructor (1.21.1's class answered its
 * use time, animation, sound and effects regardless of its Properties). The registration backfill and the members
 * view keep them (CbItemMemberComponents.setBuilt).
 */
public final class CbItemConsumables {

    private static void over(class_1792 item, java.util.function.Consumer<class_9323.class_9324> put) {
        class_9323.class_9324 b = class_9323.method_57827().method_57839(CbItemMembers.built(item));
        put.accept(b);
        CbItemMemberComponents.setBuilt(item, b.method_57838());
    }

    /** milk bucket: Consumables.MILK_BUCKET, a bucket left */
    public static void milk(class_1792 item) {
        over(item, b -> {
            b.method_57840(class_9334.field_53964, class_10128.field_53791);
            b.method_57840(class_9334.field_53965, new class_10131(new class_1799(class_1802.field_8550)));
        });
    }

    /** honey bottle: Consumables.HONEY_BOTTLE, a glass bottle left */
    public static void honey(class_1792 item) {
        over(item, b -> {
            b.method_57840(class_9334.field_53964, class_10128.field_53781);
            b.method_57840(class_9334.field_53965, new class_10131(new class_1799(class_1802.field_8469)));
        });
    }

    /** ominous bottle: Consumables.OMINOUS_BOTTLE, amplifier 0 unless the item has one */
    public static void ominous(class_1792 item) {
        boolean hasAmplifier = CbItemMembers.built(item).method_57829(class_9334.field_50238) != null;
        over(item, b -> {
            b.method_57840(class_9334.field_53964, class_10128.field_53782);
            if (!hasAmplifier) {
                b.method_57840(class_9334.field_50238, new class_10129(0));
            }
        });
    }

    /** chorus fruit: Consumables.CHORUS_FRUIT (teleport), a 1-second (20-tick) cooldown */
    public static void chorus(class_1792 item) {
        over(item, b -> {
            b.method_57840(class_9334.field_53964, class_10128.field_53792);
            b.method_57840(class_9334.field_53966, new class_10130(1.0F));
        });
    }

    /** EnchantedBookItem.createForEnchantment(instance): 1.21.2's EnchantmentHelper.createBook */
    public static class_1799 enchantedBook(class_1889 instance) {
        return class_1890.method_61711(instance);
    }

    /** MilkBucketItem.use(level, player, hand) called through the class: Item.use's 1.21.1 shape (CbItemResults) */
    public static top.dext.centurybridge.step.v1_21_2.facade.InteractionResultHolder<class_1799> method_7836(
            top.dext.centurybridge.step.v1_21_2.facade.MilkBucketItem item, net.minecraft.class_1937 level, net.minecraft.class_1657 player,
            net.minecraft.class_1268 hand) {
        return CbItemResults.method_7836(item, level, player, hand);
    }

    /** HoneyBottleItem.use(level, player, hand) called through the class: Item.use's 1.21.1 shape (CbItemResults) */
    public static top.dext.centurybridge.step.v1_21_2.facade.InteractionResultHolder<class_1799> method_7836(
            top.dext.centurybridge.step.v1_21_2.facade.HoneyBottleItem item, net.minecraft.class_1937 level, net.minecraft.class_1657 player,
            net.minecraft.class_1268 hand) {
        return CbItemResults.method_7836(item, level, player, hand);
    }

    /** OminousBottleItem.use(level, player, hand) called through the class: Item.use's 1.21.1 shape (CbItemResults) */
    public static top.dext.centurybridge.step.v1_21_2.facade.InteractionResultHolder<class_1799> method_7836(
            top.dext.centurybridge.step.v1_21_2.facade.OminousBottleItem item, net.minecraft.class_1937 level, net.minecraft.class_1657 player,
            net.minecraft.class_1268 hand) {
        return CbItemResults.method_7836(item, level, player, hand);
    }

    private CbItemConsumables() {
    }
}
