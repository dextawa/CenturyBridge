package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1792;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemConsumables;

/**
 * 1.21.1's SuspiciousStewItem (net/minecraft/class_1830, classRenames), gone in 1.21.2. 1.21.1's class listed the
 * stew's effects in its tooltip, applied them when eaten and then ate its food: on 1.21.2 the
 * SUSPICIOUS_STEW_EFFECTS component does both (its tooltip and consume listener) for any item that has it, and the
 * food (1.21.1's stew food leaves a bowl, CbItemFood) the rest. Item's members serve the rest.
 */
public class SuspiciousStewItem extends class_1792 {

    /** DEFAULT_DURATION */
    public static final int field_41069 = 160;

    public SuspiciousStewItem(class_1792.class_1793 properties) {
        super(properties);
    }
}
