package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1548;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.facade.PowerableMob;

/** Creeper is 1.21.1's PowerableMob again: isPowered() is 1.21.2's isPowered (renamed, same body). */
@Mixin(class_1548.class)
public abstract class CbEntityPowerableCreeperMixin implements PowerableMob {

    @Override
    public boolean method_6872() {
        return ((class_1548) (Object) this).method_61492();
    }
}
