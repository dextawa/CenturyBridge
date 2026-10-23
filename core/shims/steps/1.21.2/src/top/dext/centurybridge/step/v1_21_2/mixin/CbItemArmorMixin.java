package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1738;
import net.minecraft.class_1741;
import net.minecraft.class_1792;
import net.minecraft.class_8051;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemArmor;

/**
 * 1.21.2's ArmorItem keeps the material and type it was built with (1.21.2 drops both after putting them into
 * the components): 1.21.1's getMaterial / getType / getDefense / ... read them (CbItemArmor).
 */
@Mixin(class_1738.class)
public abstract class CbItemArmorMixin implements CbItemArmor.ArmorFields {

    @Unique
    private class_1741 cb$material;

    @Unique
    private class_8051 cb$type;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$keep(class_1741 material, class_8051 type, class_1792.class_1793 props, CallbackInfo ci) {
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
}
