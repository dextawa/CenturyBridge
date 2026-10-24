package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's game test API on 1.20.5: GameTestInfo.tick() took no runner; 1.20.5's tick(runner) hands it on to
 * the test's listeners (a passed or failed test may be rerun through it). A 1.20.4 GameTestInfo subclass's own
 * tick() entered directly -- not from 1.20.5's ticker -- has no frame with the runner; its super call gets
 * the runner 1.20.5's GameTestTicker hands every test it ticks (GameTestTicker.tick: info.tick(this.runner)).
 */
public final class CbGameTests {

    private CbGameTests() {
    }

    /** frameDefaults: GameTestTicker.SINGLETON's current runner (null before any batch ran, as there 1.20.5 ticks nothing) */
    public static net.minecraft.class_4520 tickerRunner() {
        return ((top.dext.centurybridge.step.v1_20_5.mixin.CbGameTestTickerAccessor) (Object) net.minecraft.class_4521.field_20574).cb$runner();
    }
}
