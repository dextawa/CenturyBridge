package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** BreezeAi's core and fight activity set-up, the same in 1.20.6 and 1.21 (CbEntityRest.method_55016) */
@Mixin(net.minecraft.class_8950.class)
public interface CbEntityBreezeAiInvoker {

    @Invoker("method_55018")
    static void cb$initCoreActivity(net.minecraft.class_4095<net.minecraft.class_8949> brain) {
        throw new AssertionError();
    }

    @Invoker("method_55750")
    static void cb$initFightActivity(net.minecraft.class_4095<net.minecraft.class_8949> brain) {
        throw new AssertionError();
    }
}
