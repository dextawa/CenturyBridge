package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_243;
import net.minecraft.class_9236;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** AbstractWindCharge members the layer reaches that 1.21 keeps protected. */
@Mixin(class_9236.class)
public interface CbEntityWindChargeInvoker {

    @Invoker("method_55055")
    void cb$explode(class_243 position);
}
