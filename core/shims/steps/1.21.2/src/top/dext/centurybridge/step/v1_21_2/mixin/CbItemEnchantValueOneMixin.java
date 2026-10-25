package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1787;
import net.minecraft.class_1811;
import net.minecraft.class_1835;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's getEnchantmentValue() overrides of TridentItem, ProjectileWeaponItem (bow, crossbow) and FishingRodItem,
 * restored with 1.21.1's body (1) so a mod's mixin on them finds its method and anchors. Vanilla's enchanting reads go
 * through it (CbItemEnchantableReadsMixin) and follow it only where it answers otherwise than that body -- a mod's
 * mixin (CbItemMembers.vanilla_method_7837). Priority below every mod's (CbVanillaReads.PRIORITY).
 */
@Mixin(value = {class_1835.class, class_1811.class, class_1787.class}, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemEnchantValueOneMixin {

    /** getEnchantmentValue() */
    @CbVanillaReads("CbItemEnchantableReadsMixin: EnchantmentHelper.getEnchantmentCost / selectEnchantment take the ENCHANTABLE they read from it")
    public int method_7837() {
        return 1;
    }
}
