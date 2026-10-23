package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_4346;
import net.minecraft.class_4355;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** RealmsClient's url(path) and execute(request), which 1.20.6's getLiveStats ran (CbRealms) */
@Mixin(net.minecraft.class_4341.class)
public interface CbRealmsClientInvoker {

    @Invoker("method_21011")
    String cb$url(String path);

    @Invoker("method_20998")
    String cb$execute(class_4346<?> request) throws class_4355;
}
