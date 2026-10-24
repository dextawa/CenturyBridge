package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** EnderDragon's protected 1.21.2 reallyHurt, called virtually by CbEntityDragon. */
@Mixin(net.minecraft.class_1510.class)
public interface CbEntityDragonInvoker {

    @Invoker("method_6819")
    void cb$reallyHurt(net.minecraft.class_3218 level, net.minecraft.class_1282 source, float amount);
}
