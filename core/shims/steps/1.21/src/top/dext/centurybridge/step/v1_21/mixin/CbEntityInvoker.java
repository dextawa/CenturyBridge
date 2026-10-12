package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Entity members the layer's 1.20.6 bodies reach that 1.21 keeps protected. */
@Mixin(class_1297.class)
public interface CbEntityInvoker {

    @Invoker("method_43261")
    class_2680 cb$getBlockStateOnLegacy();
}
