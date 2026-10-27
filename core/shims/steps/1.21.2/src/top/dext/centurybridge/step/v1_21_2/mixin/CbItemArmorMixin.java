package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1738;
import net.minecraft.class_1741;
import net.minecraft.class_8051;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.facade.Equipable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemArmor;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplit;

/**
 * 1.21.2's ArmorItem keeps the material and type it was built with (1.21.2 drops both after putting them into
 * the components): 1.21.1's getMaterial / getType / getDefense / ... read them (CbItemArmor). The constructor's values
 * are kept by CbItemArmorMixinHooks (default priority).
 */
@Mixin(value = class_1738.class, priority = top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads.PRIORITY)
public abstract class CbItemArmorMixin implements CbItemArmor.ArmorFields, Equipable, CbItemArmor.ArmorValues, CbPrioritySplit.ArmorKeep {

    @Unique
    private class_1741 cb$material;

    @Unique
    private class_8051 cb$type;

    /** the constructor's material and type (CbItemArmorMixinHooks) */
    @Override
    public void cb$v1_21_2$keepArmor(class_1741 material, class_8051 type) {
        this.cb$material = material;
        this.cb$type = type;
    }

    @Override
    public class_1741 cb$armorMaterial() {
        return cb$material;
    }

    @Override
    public class_8051 cb$armorType() {
        return cb$type;
    }

    /** Equipable.getEquipmentSlot(), as 1.21.1's ArmorItem answered it (CbItemArmor) */
    @top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads("CbItemMembersMixin: the item's EQUIPPABLE (CbItemMemberComponents) follows it, which LivingEntity.getEquipmentSlotForItem / onEquipItem, the dispenser and Item.use read")
    @Override
    public net.minecraft.class_1304 method_7685() {
        return CbItemArmor.method_7685((class_1738) (Object) this);
    }

    /** Equipable.getEquipSound(), as 1.21.1's ArmorItem answered it (CbItemArmor) */
    @top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads("CbItemMembersMixin: the item's EQUIPPABLE (CbItemMemberComponents) follows it, which LivingEntity.getEquipmentSlotForItem / onEquipItem, the dispenser and Item.use read")
    @Override
    public net.minecraft.class_6880<net.minecraft.class_3414> method_31570() {
        return CbItemArmor.method_31570((class_1738) (Object) this);
    }

    /** 1.21.1's ArmorItem.getDefense(): the material's defense for the type */
    @top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads("CbItemMobArmorMixin: Mob.canReplaceCurrentItem compares armor by it where it answers otherwise than its body")
    @Override
    public int method_7687() {
        return CbItemArmor.super_method_7686((class_1738) (Object) this).comp_349().method_48403(this.cb$type);
    }

    /** 1.21.1's ArmorItem.getToughness(): the material's toughness */
    @top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads("CbItemMobArmorMixin: Mob.canReplaceCurrentItem compares armor by it where it answers otherwise than its body")
    @Override
    public float method_26353() {
        return CbItemArmor.super_method_7686((class_1738) (Object) this).comp_349().comp_2303();
    }
}
