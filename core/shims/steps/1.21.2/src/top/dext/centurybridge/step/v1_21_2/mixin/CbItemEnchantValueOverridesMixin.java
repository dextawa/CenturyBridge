package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1738;
import net.minecraft.class_1787;
import net.minecraft.class_1792;
import net.minecraft.class_1811;
import net.minecraft.class_1835;
import net.minecraft.class_9362;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's getEnchantmentValue() overrides of ArmorItem (the material's), TridentItem, ProjectileWeaponItem,
 * FishingRodItem (1) and MaceItem (15), restored on those classes so a mod's mixin on them has its method: each
 * answers the ENCHANTABLE the item was built with, which is what 1.21.1's override answered (1.21.2 builds the
 * component from the same values). Vanilla's enchanting reads go through it (CbItemEnchantableReadsMixin) and follow
 * it only where it answers otherwise -- a mod's mixin.
 */
@Mixin(value = {class_1738.class, class_1835.class, class_1811.class, class_1787.class, class_9362.class}, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemEnchantValueOverridesMixin {

    /** getEnchantmentValue() */
    @CbVanillaReads("CbItemEnchantableReadsMixin: EnchantmentHelper.getEnchantmentCost / selectEnchantment take the ENCHANTABLE they read from it")
    public int method_7837() {
        return CbItemMembers.super_method_7837((class_1792) (Object) this);
    }
}
