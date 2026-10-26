package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1304;
import net.minecraft.class_2190;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.facade.Equipable;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's AbstractSkullBlock was an Equipable (the head, the generic equip sound); 1.21.2 equips it by its item's EQUIPPABLE component. Implementing the facade again gives
 * 1.21.1 callers its answers (Equipable.get, instanceof, the interface calls).
 */
@Mixin(value = class_2190.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemEquipableSkullMixin implements Equipable {

    /** getEquipmentSlot(): HEAD */
    @Override
    public class_1304 method_7685() {
        return class_1304.field_6169;
    }
}
