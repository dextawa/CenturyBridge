package top.dext.centurybridge.step.v26_2.mixin;

import java.util.function.Supplier;
import net.minecraft.CrashReport;
import net.minecraft.util.thread.BlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * 26.1.2's protected BlockableEventLoop.hasDelayedCrash(), which 26.2 dropped while keeping the static delayedCrash
 * supplier it tested (javap of both: delayCrash / relayDelayCrash set it, the loop throws it). 26.1.2's only vanilla
 * reader was Minecraft.destroy(), which 26.2 removed with its exit there; so calls and super calls link and answer as on
 * 26.1.2, while an override no longer steers 26.2's shutdown.
 */
@Mixin(value = BlockableEventLoop.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26DelayedCrashMixin {
    @Shadow
    private static volatile Supplier<CrashReport> delayedCrash;

    /** 26.1.2 BlockableEventLoop.hasDelayedCrash() */
    protected boolean hasDelayedCrash() {
        return delayedCrash != null;
    }
}
