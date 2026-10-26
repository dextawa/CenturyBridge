package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_3695;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestClient;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestServer;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * Minecraft.getProfiler() / getTimer() / debugFpsMeterKeyPress(int) of 1.21.1 (covers): the running
 * thread's profiler (1.21.2 keeps it thread-local, Profiler.get()), the frame's delta tracker
 * (getDeltaTracker) and the profiler pie chart's key press, now on the debug overlay's chart.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = class_310.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestMinecraftMixin {

    public class_3695 method_16011() {
        return CbRestServer.threadProfiler();
    }

    public class_9779 method_60646() {
        return CbRestClient.timer((class_310) (Object) this);
    }

    public void method_1524(int key) {
        CbRestClient.debugFpsMeterKeyPress((class_310) (Object) this, key);
    }
}
