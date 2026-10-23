package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1304;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2315;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_6880;
import net.minecraft.class_10205;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;

/**
 * 1.21.1's ElytraItem (net/minecraft/class_1770, classRenames), gone in 1.21.2 (the elytra is a plain item with
 * GLIDER and EQUIPPABLE components). For a converted class that extends or constructs it: 1.21.1's constructor
 * (registers the armor dispense behaviour, 1.21.2's EquipmentDispenseItemBehavior) and members. It equips to the
 * chest by its Equipable answers (CbItemEquipable: EQUIPPABLE from getEquipmentSlot / getEquipSound); it repairs
 * with phantom membrane through isValidRepairItem (CbItemMembers' anvil hook asks it, LegacyRepairItem). Flight
 * is not given: 1.21.1 let only Items.ELYTRA fly (plus Fabric API's FabricElytraItem, a Fabric API matter), and
 * 1.21.2's GLIDER is not set here either. use(..) is 1.21.1's swap through Item.use's hook (facadeGaps).
 */
public class ElytraItem extends class_1792 implements Equipable, CbItemMembers.LegacyRepairItem {

    public ElytraItem(class_1792.class_1793 properties) {
        super(properties);
        class_2315.method_10009(this, class_10205.field_54201);
    }

    /** isFlyEnabled(stack): not about to break */
    public static boolean method_7804(class_1799 stack) {
        return stack.method_7919() < stack.method_7936() - 1;
    }

    /** isValidRepairItem(stack, ingredient): phantom membrane */
    @Override
    public boolean method_7878(class_1799 stack, class_1799 ingredient) {
        return ingredient.method_31574(class_1802.field_8614);
    }

    /** getEquipSound(): the elytra's */
    @Override
    public class_6880<class_3414> method_31570() {
        return class_3417.field_14966;
    }

    /** getEquipmentSlot(): the chest */
    @Override
    public class_1304 method_7685() {
        return class_1304.field_6174;
    }
}
