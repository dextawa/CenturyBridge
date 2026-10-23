package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1792;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemConsumables;

/**
 * 1.21.1's OminousBottleItem (net/minecraft/class_9462, classRenames), gone in 1.21.2. 1.21.1's class drank in 32
 * ticks, played the ominous bottle sound and gave Bad Omen of the stack's amplifier for 120000 ticks: on 1.21.2
 * that is Consumables.OMINOUS_BOTTLE (the sound) and the OMINOUS_BOTTLE_AMPLIFIER component, whose consume listener
 * gives the effect (amplifier 0 when the item has none), over the item's own (CbItemConsumables).
 */
public class OminousBottleItem extends class_1792 {

    /** EFFECT_DURATION */
    public static final int field_50143 = 120000;
    /** MIN_AMPLIFIER */
    public static final int field_50144 = 0;
    /** MAX_AMPLIFIER */
    public static final int field_50145 = 4;
    /** DRINK_DURATION */
    public static final int field_50146 = 32;

    public OminousBottleItem(class_1792.class_1793 properties) {
        super(properties);
        CbItemConsumables.ominous(this);
    }
}
