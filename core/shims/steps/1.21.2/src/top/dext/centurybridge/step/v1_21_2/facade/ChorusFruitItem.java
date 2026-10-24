package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1792;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemConsumables;

/**
 * 1.21.1's ChorusFruitItem (net/minecraft/class_1757, classRenames), gone in 1.21.2. 1.21.1's class ate its food,
 * teleported the eater up to 16 blocks on the server and put the item on a 20-tick cooldown for a player: on 1.21.2
 * that is Consumables.CHORUS_FRUIT (TeleportRandomly 16) and a 1-second USE_COOLDOWN, over the item's own
 * (CbItemConsumables).
 */
public class ChorusFruitItem extends class_1792 {

    public ChorusFruitItem(class_1792.class_1793 properties) {
        super(properties);
        CbItemConsumables.chorus(this);
    }
}
