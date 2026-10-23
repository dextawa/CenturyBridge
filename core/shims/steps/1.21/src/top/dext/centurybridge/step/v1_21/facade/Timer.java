package top.dext.centurybridge.step.v1_21.facade;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

/**
 * 1.20.6's net.minecraft.client.Timer (class_317), gone in 1.21 (the game's own clock is
 * DeltaTracker.Timer, which also keeps real time and pause state). A 1.20.6 Timer is a standalone clock a
 * mod makes and advances itself; this is its body as 1.20.6 had it: advanceTime(now) turns the time since
 * the last call into ticks at the target milliseconds-per-tick the provider answers, keeps the fraction in
 * partialTick and the last step in tickDelta, and answers the whole ticks passed.
 */
public class Timer {

    /** partialTick (field_1970): the fraction of a tick past the last whole one */
    public float field_1970;

    /** tickDelta (field_1969): ticks the last advanceTime covered */
    public float field_1969;

    /** lastMs (private in 1.20.6; public here because a mod's access widener may open it, and wideners never reach a CenturyBridge class) */
    public long field_1971;

    /** msPerTick */
    public float field_1968;

    /** targetMsptProvider */
    public FloatUnaryOperator field_47102;

    public Timer(float ticksPerSecond, long now, FloatUnaryOperator targetMsptProvider) {
        this.field_1968 = 1000.0F / ticksPerSecond;
        this.field_1971 = now;
        this.field_47102 = targetMsptProvider;
    }

    /** advanceTime(now) (method_1658) */
    public int method_1658(long now) {
        this.field_1969 = (float) (now - this.field_1971) / this.field_47102.apply(this.field_1968);
        this.field_1971 = now;
        this.field_1970 += this.field_1969;
        int ticks = (int) this.field_1970;
        this.field_1970 -= (float) ticks;
        return ticks;
    }
}
