package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.facade.v1_20_2.class_3517;
import top.dext.centurybridge.rt.v1_20_2.CbServerMetricsHolder;

/**
 * A MinecraftServer's 1.20.1 tick-time buffer (getMetricsData()): 1.20.1 pushed each tick's
 * duration into it at the end of tick(); 1.20.2 hands the same value to pushTickLog(nanos). The
 * sample is recorded there, then the 1.20.2 call proceeds.
 */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class CbServerMetricsRecorder implements CbServerMetricsHolder {

    @Unique
    private final class_3517 cb$metrics = new class_3517();

    @Shadow
    protected abstract void method_53620(long nanos);

    @Override
    public class_3517 cb$metrics() {
        return this.cb$metrics;
    }

    @Redirect(method = "method_3748", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/server/MinecraftServer;method_53620(J)V"))
    private void cb$recordTick(net.minecraft.server.MinecraftServer server, long nanos) {
        this.cb$metrics.method_15247(nanos);
        this.method_53620(nanos);
    }
}
