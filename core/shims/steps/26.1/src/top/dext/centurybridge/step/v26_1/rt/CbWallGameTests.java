package top.dext.centurybridge.step.v26_1.rt;

/**
 * 1.21.11's game test data on 26.1: TestData gained padding (javap 26.1 TestData: an int after skyAccess, codec
 * optionalFieldOf("padding", 0), the short constructors pass 0). A 1.21.11 TestData had none: 0.
 */
public final class CbWallGameTests {
    private CbWallGameTests() {
    }

    /** TestData's padding for a 1.21.11-built TestData (1.21.11 had none; 26.1's own default) */
    public static int noPadding() {
        return 0;
    }
}
