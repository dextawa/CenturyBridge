package top.dext.centurybridge.facade.v1_20_2;

/**
 * 1.20.1 MetricsData / FrameTimer (class_3517), gone in 1.20.2 (the debug charts keep their own
 * sample logs): a ring buffer of the last 240 frame or tick times, ported from the 1.20.1 class
 * as it was. Converted code naming class_3517 is renamed onto this class (classRenames); a
 * MinecraftServer's getMetricsData() is one of these, fed with every tick's duration
 * (CbServerMetrics). The 1.20.1 fields were private; here they are public because an access
 * widener never reaches a CenturyBridge class, so a mod widening them gets them this way.
 */
public class class_3517 {

    /** SIZE */
    public static final int field_29839 = 240;

    public final long[] field_15653 = new long[240];
    public int field_15656;
    public int field_15655;
    public int field_15654;

    /** pushSample(time) */
    public void method_15247(long time) {
        this.field_15653[this.field_15654] = time;
        this.field_15654++;
        if (this.field_15654 == 240) {
            this.field_15654 = 0;
        }
        if (this.field_15655 < 240) {
            this.field_15656 = 0;
            this.field_15655++;
        } else {
            this.field_15656 = this.method_15251(this.field_15654 + 1);
        }
    }

    /** the average of the first {@code count} samples from the start index */
    public long method_34912(int count) {
        int end = (this.field_15656 + count) % 240;
        int i = this.field_15656;
        long sum = 0L;
        while (i != end) {
            sum += this.field_15653[i];
            i++;
        }
        return sum / count;
    }

    /** method_15248(average over count, height, 60) */
    public int method_34913(int count, int height) {
        return this.method_15248(this.method_34912(count), height, 60);
    }

    /** scaleSample(nanos, height, fps) */
    public int method_15248(long nanos, int height, int fps) {
        double fraction = (double) nanos / (double) (1000000000L / fps);
        return (int) (fraction * height);
    }

    /** getStartIndex() */
    public int method_15249() {
        return this.field_15656;
    }

    /** getCurrentIndex() */
    public int method_15250() {
        return this.field_15654;
    }

    /** wrapIndex(index) */
    public int method_15251(int index) {
        return index % 240;
    }

    /** getSamples() */
    public long[] method_15246() {
        return this.field_15653;
    }
}
