package cb.smx6.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** FilledProfileResults.getProfilerResults(long, int) (method_16063, protected): the profiler report text */
@Mixin(net.minecraft.class_3692.class)
public interface ProfileReportInvoker {
    @Invoker("method_16063")
    String cbsmx6$report(long timeSpan, int tickSpan);
}
