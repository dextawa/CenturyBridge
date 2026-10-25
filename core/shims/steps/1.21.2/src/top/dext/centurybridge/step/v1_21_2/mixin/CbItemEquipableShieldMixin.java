package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1304;
import net.minecraft.class_1819;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.facade.Equipable;

/**
 * 1.21.1's ShieldItem was an Equipable (the off hand, the generic equip sound); 1.21.2 equips it by its use and the off hand slot. Implementing the facade again gives
 * 1.21.1 callers its answers (Equipable.get, instanceof, the interface calls).
 */
@Mixin(class_1819.class)
public abstract class CbItemEquipableShieldMixin implements Equipable {

    /** getEquipmentSlot(): OFFHAND */
    @top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads("CbItemMembersMixin: the item's EQUIPPABLE (CbItemMemberComponents) follows it, which LivingEntity.getEquipmentSlotForItem / onEquipItem, the dispenser and Item.use read")
    @Override
    public class_1304 method_7685() {
        return class_1304.field_6171;
    }
}
