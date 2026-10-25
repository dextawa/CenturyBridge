package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1738;
import net.minecraft.class_1792;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * ArmorItem's 1.21.1 getEnchantmentValue() override (the material's value), restored so a mod's mixin on it has its
 * method: it answers the ENCHANTABLE the item was built with, which 1.21.2 builds from the same material value
 * (1.21.1's body read the material holder field 1.21.2's ArmorItem no longer has). Vanilla's enchanting reads go
 * through it (CbItemEnchantableReadsMixin) and follow it only where it answers otherwise than that body -- a mod's
 * mixin. TridentItem / ProjectileWeaponItem / FishingRodItem: CbItemEnchantValueOneMixin; MaceItem:
 * CbItemMaceMembersMixin.
 */
@Mixin(value = class_1738.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemEnchantValueOverridesMixin {

    /** getEnchantmentValue() */
    @CbVanillaReads("CbItemEnchantableReadsMixin: EnchantmentHelper.getEnchantmentCost / selectEnchantment take the ENCHANTABLE they read from it")
    public int method_7837() {
        return CbItemMembers.super_method_7837((class_1792) (Object) this);
    }
}
