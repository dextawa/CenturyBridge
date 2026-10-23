package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ArmorItem.material (1.20.4's protected field, a holder in 1.20.5; rt/CbFinItemArmor) */
@Mixin(net.minecraft.class_1738.class)
public interface CbFinItemArmorAccess {

    @Accessor("field_7881")
    net.minecraft.class_6880<net.minecraft.class_1741> cbFin$material();
}
