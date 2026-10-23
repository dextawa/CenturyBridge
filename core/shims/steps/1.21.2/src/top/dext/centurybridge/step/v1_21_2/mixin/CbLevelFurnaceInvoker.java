package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelBlockEntities;

/** AbstractFurnaceBlockEntity's protected getBurnDuration(fuelValues, stack), for 1.21.1's getBurnDuration(stack) */
@Mixin(net.minecraft.class_2609.class)
public interface CbLevelFurnaceInvoker extends CbLevelBlockEntities.FurnaceAccess {

    @Override
    @Invoker("method_11200")
    int cb$burnDuration(net.minecraft.class_9895 fuel, net.minecraft.class_1799 stack);
}
