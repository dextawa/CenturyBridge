package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** AbstractMinecart's protected 1.21.2 rail physics, called virtually by CbEntityMinecartPhysics. */
@Mixin(net.minecraft.class_1688.class)
public interface CbEntityMinecartInvoker {

    @Invoker("method_7513")
    void cb$moveAlongTrack(net.minecraft.class_3218 level);

    @Invoker("method_7525")
    net.minecraft.class_243 cb$applyNaturalSlowdown(net.minecraft.class_243 velocity);
}
