package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1528;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.facade.PowerableMob;

/** WitherBoss is 1.21.1's PowerableMob again; 1.21.2 kept its isPowered() under the same name. */
@Mixin(class_1528.class)
public abstract class CbEntityPowerableWitherMixin implements PowerableMob {
}
