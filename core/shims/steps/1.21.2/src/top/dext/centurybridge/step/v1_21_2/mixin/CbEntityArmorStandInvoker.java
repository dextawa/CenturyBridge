package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1304;
import net.minecraft.class_1531;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** ArmorStand's private isDisabled(slot), which 1.21.1's ArmorStand.canTakeItem asked (CbEntityTake). */
@Mixin(class_1531.class)
public interface CbEntityArmorStandInvoker {

    @Invoker("method_6915")
    boolean cb$isDisabled(class_1304 slot);
}
