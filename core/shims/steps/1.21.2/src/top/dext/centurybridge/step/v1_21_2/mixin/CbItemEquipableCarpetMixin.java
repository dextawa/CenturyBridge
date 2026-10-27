package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1304;
import net.minecraft.class_5815;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.facade.Equipable;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's WoolCarpetBlock was an Equipable (a llama's body, the llama swag sound); 1.21.2 equips it by its item's EQUIPPABLE component. Implementing the facade again gives
 * 1.21.1 callers its answers (Equipable.get, instanceof, the interface calls).
 */
@Mixin(value = class_5815.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemEquipableCarpetMixin implements Equipable {

    /** getEquipmentSlot(): BODY */
    @Override
    public class_1304 method_7685() {
        return class_1304.field_48824;
    }

    /** getEquipSound(): the llama swag sound */
    @Override
    public net.minecraft.class_6880<net.minecraft.class_3414> method_31570() {
        return net.minecraft.class_3417.field_14554;
    }
}
