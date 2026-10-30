package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.facade.DyeableLeatherItem;

/**
 * 1.20.5's ArmorItem (every vanilla leather armor and leather horse armor item, which were DyeableArmorItem /
 * DyeableHorseArmorItem in 1.20.4, is one) implements 1.20.4's DyeableLeatherItem, so a 1.20.4 cast of such an
 * item after its instanceof check (rt/CbFinItemArmor.cbInstanceOf: 1.20.4's truth) holds and its color methods
 * answer from the stack's display.color.
 */
@Mixin(net.minecraft.class_1738.class)
public abstract class CbFinItemDyeableArmorMixin implements DyeableLeatherItem, top.dext.centurybridge.step.v1_20_5.rt.CbFinItemArmor.VanillaDyeable {
}
