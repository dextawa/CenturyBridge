package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Commands' execution context of this thread (CURRENT_EXECUTION_CONTEXT), for running a command or function at the call as 1.20.2 did */
@Mixin(net.minecraft.class_2170.class)
public interface CbCommandsAccess {

    @Accessor("field_46823")
    static ThreadLocal<net.minecraft.class_8854<net.minecraft.class_2168>> cb$currentContext() {
        throw new AssertionError("mixin accessor");
    }
}
