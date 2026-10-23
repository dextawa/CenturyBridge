package top.dext.centurybridge.step.v1_21.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.class_332;
import net.minecraft.class_9779;

/**
 * Fabric API 1.20.6's rendering-v1 members 1.21's module reshaped: HudRenderCallback took the frame's partial tick
 * where 1.21's takes the DeltaTracker, and WorldRenderContext answered tickDelta() and limitTime() where 1.21's
 * answers tickCounter(). The float is the one 1.20.6 handed down the frame (CbClientTime). Wired from s6fin-fapi.json.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiRendering {

    private CbFapiRendering() {
    }

    /** a 1.20.6 HUD listener: onHudRender(graphics, tickDelta) */
    @FunctionalInterface
    public interface LegacyHud {
        void onHudRender(class_332 graphics, float tickDelta);
    }

    /** a 1.20.6 HUD listener as 1.21's: handed the partial tick of the tracker 1.21 renders the HUD with */
    public static HudRenderCallback hud(LegacyHud legacy) {
        return (graphics, tracker) -> legacy.onHudRender(graphics, CbClientTime.cbPartialTick(tracker));
    }

    /** 1.21's onHudRender(graphics, tracker) of a 1.20.6 class implementing the callback: its own, with the partial tick */
    public static float partialTick(class_9779 tracker) {
        return CbClientTime.cbPartialTick(tracker);
    }

    /** callback.onHudRender(graphics, tickDelta) of 1.20.6: 1.21's with a tracker answering that partial tick */
    public static class_9779 tracker(float tickDelta) {
        return CbClientTime.cbTracker(tickDelta);
    }

    /** 1.20.6 WorldRenderContext.tickDelta(): the partial tick the level renders with, off 1.21's tickCounter() */
    public static float tickDelta(WorldRenderContext context) {
        return CbClientTime.cbPartialTick(context.tickCounter());
    }

    /** 1.20.6 WorldRenderContext.limitTime(): the nanoTime the frame started at, which 1.20.6 handed renderLevel */
    public static long limitTime(WorldRenderContext context) {
        return CbClientTime.cbFrameNanos();
    }
}
