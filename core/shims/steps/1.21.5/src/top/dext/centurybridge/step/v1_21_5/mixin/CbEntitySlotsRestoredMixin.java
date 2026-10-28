package top.dext.centurybridge.step.v1_21_5.mixin;

import com.google.common.collect.Iterables;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_5.rt.CbEntitySlots;
import top.dext.centurybridge.step.v1_21_5.rt.CbVanillaReads;

/**
 * 1.21.4's LivingEntity getArmorSlots / getHandSlots / getArmorAndBodyArmorSlots / getAllSlots, gone in 1.21.5
 * (EntityEquipment), restored on LivingEntity: a 1.21.4 call links, a 1.21.4 class's override overrides, and the
 * composed members call the others as 1.21.4's did (javap 1.21.4 class_1309: getArmorAndBodyArmorSlots =
 * getArmorSlots(), getAllSlots = Iterables.concat(getHandSlots(), getArmorAndBodyArmorSlots())). 1.21.5's vanilla
 * reads slots through getItemBySlot, so these members carry no vanilla read. Priority below every mod's.
 */
@Mixin(value = class_1309.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntitySlotsRestoredMixin {

    /** getArmorSlots() (abstract in 1.21.4; every vanilla subclass answered its four armor slots) */
    public Iterable<class_1799> method_5661() {
        return CbEntitySlots.armor((class_1309) (Object) this);
    }

    /** getHandSlots() */
    public Iterable<class_1799> method_5877() {
        return CbEntitySlots.hands((class_1309) (Object) this);
    }

    /** getArmorAndBodyArmorSlots() */
    public Iterable<class_1799> method_56674() {
        return CbEntitySlots.armorAndBody((class_1309) (Object) this, this.method_5661());
    }

    /** getAllSlots() */
    public Iterable<class_1799> method_56675() {
        return Iterables.concat(this.method_5877(), this.method_56674());
    }
}
