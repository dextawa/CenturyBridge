package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1792;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemConsumables;

/**
 * 1.21.1's MilkBucketItem (net/minecraft/class_1805, classRenames), gone in 1.21.2 (the milk bucket is a plain item
 * with components). 1.21.1's class drank in 32 ticks, cleared every effect on the server, awarded the consume
 * criterion and use stat and left a bucket: on 1.21.2 that is the milk bucket's CONSUMABLE (Consumables.MILK_BUCKET:
 * drink, 1.6 s, ClearAllStatusEffects) and USE_REMAINDER (a bucket), which this class's items get over their own
 * (CbItemConsumables). use / finishUsingItem / getUseDuration / getUseAnimation are Item's, which read them.
 */
public class MilkBucketItem extends class_1792 {

    /** DRINK_DURATION */
    public static final int field_30911 = 32;

    public MilkBucketItem(class_1792.class_1793 properties) {
        super(properties);
        CbItemConsumables.milk(this);
    }
}
