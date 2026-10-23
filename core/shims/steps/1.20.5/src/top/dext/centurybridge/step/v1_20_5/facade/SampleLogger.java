package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4's SampleLogger (class_8743): a concrete ring of the last 240 long samples -- logSample(value),
 * capacity(), size(), get(index) oldest first, reset(). 1.20.5 reuses the name class_8743 for an interface
 * (logFullSample / logSample / logPartialSample), keeps the storage behind SampleStorage (class_9194) and
 * implements both, one value per sample, in LocalSampleLogger (class_9191).
 *
 * <p>So every 1.20.4 reference to the type stays the 1.20.5 interface -- the value is a 1.20.5 sample
 * logger either way -- and only what needs the 1.20.4 class takes this one: a 1.20.4 subclass extends it
 * (superRenames, its super calls included), and {@code new SampleLogger()} makes one (ctorRedirects,
 * rt/CbFinNetLoggers). This class IS a 1.20.5 LocalSampleLogger of dimension 1 -- 1.20.4's ring exactly
 * (240 entries, the oldest dropped once full; LocalSampleLogger.useSample is 1.20.4's logSample with one
 * column) -- so it goes wherever 1.20.5 takes a logger or a SampleStorage (a connection, a ping or bandwidth
 * monitor, a debug chart). 1.20.4's methods have 1.20.4's bodies over that ring; 1.20.5's SampleStorage
 * methods answer through them, so a 1.20.4 subclass's overrides of get / size / capacity / reset are what
 * 1.20.5's charts read, as 1.20.4's charts read them.
 */
public class SampleLogger extends net.minecraft.class_9191 {

    /** CAPACITY */
    public static final int field_45719 = 240;

    public SampleLogger() {
        super(1);
    }

    /** capacity(): 240 */
    public int method_53064() {
        return super.method_56662();
    }

    /** size(): the samples kept */
    public int method_53067() {
        return super.method_56663();
    }

    /** get(index): the index-th sample, oldest first; IndexOutOfBoundsException outside [0, size) */
    public long method_53065(int index) {
        return super.method_56659(index);
    }

    /** reset(): no samples */
    public void method_53069() {
        super.method_56664();
    }

    // ---------------------------------------------------------------- 1.20.5's SampleStorage, through them

    @Override
    public int method_56662() {
        return method_53064();
    }

    @Override
    public int method_56663() {
        return method_53067();
    }

    @Override
    public long method_56659(int index) {
        return method_53065(index);
    }

    /** get(index, dimension): a 1.20.4 logger has the one dimension */
    @Override
    public long method_56660(int index, int dimension) {
        if (dimension != 0) {
            throw new IndexOutOfBoundsException(dimension + " out of bounds for dimensions 1");
        }
        return method_53065(index);
    }

    @Override
    public void method_56664() {
        method_53069();
    }
}
