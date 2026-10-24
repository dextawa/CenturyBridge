package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.function.Supplier;
import net.minecraft.class_10209;
import net.minecraft.class_3695;
import net.minecraft.class_6757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemTicks;

/**
 * 1.21.1's LevelTicks(isTickable, profiler) profiled its tick on the supplier it was built with; 1.21.2's asks the
 * thread's profiler. A LevelTicks built by a 1.21.1 constructor (CbItemTicks.keepProfiler) profiles on its own
 * supplier again; any other keeps 1.21.2's.
 */
@Mixin(class_6757.class)
public abstract class CbItemLevelTicksMixin implements CbItemTicks.Profiled {

    @Unique
    private Supplier<class_3695> cb$profiler;

    @Override
    public void cb$setProfiler(Supplier<class_3695> profiler) {
        this.cb$profiler = profiler;
    }

    @Redirect(method = "method_39377(JILjava/util/function/BiConsumer;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_10209;method_64146()Lnet/minecraft/class_3695;"))
    private class_3695 cb$profiler() {
        return this.cb$profiler != null ? this.cb$profiler.get() : class_10209.method_64146();
    }
}
