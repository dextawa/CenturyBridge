package top.dext.centurybridge.step.v1_21.rt;

/**
 * GameTest runner arguments 1.21 added, with what 1.20.6 did. Wired from s6world.json.
 */
public final class CbWorldGameTests {

    /**
     * GameTestRunner's haltOnError: 1.21 can stop the run at the first failed test; 1.20.6's runner always ran
     * every batch to the end.
     */
    public static boolean noHaltOnError() {
        return false;
    }

    /**
     * StructureGridSpawner's clearOnBatch: 1.21 can clear the previous batch's structures when a batch starts;
     * 1.20.6's spawner had no onBatchStart and left them standing.
     */
    public static boolean noClearOnBatch() {
        return false;
    }

    private CbWorldGameTests() {
    }
}
