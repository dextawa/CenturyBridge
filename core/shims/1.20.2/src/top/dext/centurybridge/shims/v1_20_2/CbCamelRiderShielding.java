package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.facade.v1_20_2.class_7997;

/**
 * The camel is a RiderShieldingMount again (1.20.1's only vanilla one, shielding height 0.5):
 * 1.20.1's LivingEntity.getMeleeAttackReferencePosition raises its riders by it, and a converted
 * subclass's override of getRiderShieldingHeight overrides this.
 */
@Mixin(value = net.minecraft.class_7689.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbCamelRiderShielding implements class_7997 {

    @Override
    public double method_47923() {
        return 0.5;
    }
}
