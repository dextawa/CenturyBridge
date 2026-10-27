package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;

/**
 * 1.21.11's DeltaTracker.Timer.advanceTime(timeMillis, advanceGameTime) on 26.1, which split it: 1.21.11's body
 * (javap class_9779$class_9781.method_60640) advanced the real time, then the game time when asked, answering
 * the ticks to run (0 when not); 26.1 has both halves as advanceRealTime(ms) and advanceGameTime(ms).
 */
@Environment(EnvType.CLIENT)
public final class CbWallTimer {
    private CbWallTimer() {
    }

    /** 1.21.11's advanceTime(ms, advanceGameTime), declared by a mod's timer subclass (adapterHooks) */
    public interface LegacyAdvanceTime {
        int advanceTime(long timeMillis, boolean advanceGameTime);
    }

    /** a mod's call of advanceTime: a 1.21.11 override's answer, else 1.21.11's body */
    public static int advanceTime(DeltaTracker.Timer timer, long timeMillis, boolean advanceGameTime) {
        if (timer instanceof LegacyAdvanceTime legacy) {
            return legacy.advanceTime(timeMillis, advanceGameTime);
        }
        return superAdvanceTime(timer, timeMillis, advanceGameTime);
    }

    /** 1.21.11's advanceTime body */
    public static int superAdvanceTime(DeltaTracker.Timer timer, long timeMillis, boolean advanceGameTime) {
        timer.advanceRealTime(timeMillis);
        return advanceGameTime ? timer.advanceGameTime(timeMillis) : 0;
    }
}
