package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * DebugScreenOverlay.logTickDuration(nanos): 1.20.4's integrated server handed the overlay each tick's duration
 * and the overlay logged it into its tick time logger. 1.20.5's server logs into that logger itself (it is the
 * integrated server's getTickTimeLogger()); the method comes back with 1.20.4's body for 1.20.4 callers.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_340.class)
public abstract class CbClientDebugOverlayMixin implements top.dext.centurybridge.step.v1_20_5.rt.CbClientGui.DebugOverlayApi {

    @Shadow
    public abstract net.minecraft.class_9191 method_56577();

    /** 1.20.4 DebugScreenOverlay.logTickDuration(nanos) */
    @Override
    public void method_53535(long nanos) {
        this.method_56577().method_53066(nanos);
    }
}
