package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1792;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemConsumables;

/**
 * 1.21.1's HoneyBottleItem (net/minecraft/class_4480, classRenames), gone in 1.21.2. 1.21.1's class drank in 40
 * ticks with the honey drinking sound, ate its food, removed poison on the server and left a glass bottle: on
 * 1.21.2 that is the honey bottle's CONSUMABLE (Consumables.HONEY_BOTTLE: drink, 2 s, honey sound,
 * RemoveStatusEffects poison) and USE_REMAINDER (a glass bottle), over the item's own (CbItemConsumables).
 */
public class HoneyBottleItem extends class_1792 {

    /** DRINK_DURATION */
    public static final int field_30885 = 40;

    public HoneyBottleItem(class_1792.class_1793 properties) {
        super(properties);
        CbItemConsumables.honey(this);
    }

    /** getDrinkingSound(): the honey drinking sound (also its CONSUMABLE's) */
    public net.minecraft.class_3414 method_21831() {
        return net.minecraft.class_3417.field_20615.comp_349();
    }

    /** getEatingSound(): the honey drinking sound */
    public net.minecraft.class_3414 method_21830() {
        return net.minecraft.class_3417.field_20615.comp_349();
    }
}
