package top.dext.centurybridge.step.v1_21_5.facade;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.class_2470;
import net.minecraft.class_4516;

/**
 * 1.21.4's net.minecraft.gametest.framework.TestFunction (net/minecraft/class_4529), gone in 1.21.5 (a test there is a
 * GameTestInstance with TestData). Components, constructors and members as javap -c -p 1.21.4 class_4529: (batch,
 * testName, structure, rotation, timeoutTicks, setupTicks, required, manualOnly, maxAttempts, requiredSuccesses,
 * skyAccess, function); the short constructors default rotation NONE, manualOnly false, maxAttempts 1,
 * requiredSuccesses 1, skyAccess false; run(helper) feeds the function; isFlaky is maxAttempts &gt; 1; toString is the
 * test name; equals / hashCode are the record's (every component). A final class rather than a record: a facade's
 * members stand in for the class's and must be public (access wideners never reach a CenturyBridge class), and a
 * record's component fields are always private. rt/CbLegacyGameTests turns one into 1.21.5's FunctionGameTestInstance.
 */
public final class TestFunction {
    /** batchName */
    public final String comp_2218;
    /** testName */
    public final String comp_2219;
    /** structureName */
    public final String comp_2220;
    /** rotation */
    public final class_2470 comp_2221;
    /** maxTicks */
    public final int comp_2222;
    /** setupTicks */
    public final long comp_2223;
    /** required */
    public final boolean comp_2224;
    /** manualOnly */
    public final boolean comp_2445;
    /** maxAttempts */
    public final int comp_2225;
    /** requiredSuccesses */
    public final int comp_2226;
    /** skyAccess */
    public final boolean comp_2362;
    /** function */
    public final Consumer<class_4516> comp_2227;

    public TestFunction(String batch, String testName, String structure, class_2470 rotation, int maxTicks, long setupTicks, boolean required,
                        boolean manualOnly, int maxAttempts, int requiredSuccesses, boolean skyAccess, Consumer<class_4516> function) {
        this.comp_2218 = batch;
        this.comp_2219 = testName;
        this.comp_2220 = structure;
        this.comp_2221 = rotation;
        this.comp_2222 = maxTicks;
        this.comp_2223 = setupTicks;
        this.comp_2224 = required;
        this.comp_2445 = manualOnly;
        this.comp_2225 = maxAttempts;
        this.comp_2226 = requiredSuccesses;
        this.comp_2362 = skyAccess;
        this.comp_2227 = function;
    }

    public TestFunction(String batch, String testName, String structure, int timeoutTicks, long setupTicks, boolean required,
                        Consumer<class_4516> function) {
        this(batch, testName, structure, class_2470.field_11467, timeoutTicks, setupTicks, required, false, 1, 1, false, function);
    }

    public TestFunction(String batch, String testName, String structure, class_2470 rotation, int timeoutTicks, long setupTicks,
                        boolean required, Consumer<class_4516> function) {
        this(batch, testName, structure, rotation, timeoutTicks, setupTicks, required, false, 1, 1, false, function);
    }

    /** run(GameTestHelper) */
    public void method_22297(class_4516 helper) {
        comp_2227.accept(helper);
    }

    @Override
    public String toString() {
        return comp_2219;
    }

    /** isFlaky() */
    public boolean method_32257() {
        return comp_2225 > 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(comp_2218, comp_2219, comp_2220, comp_2221, comp_2222, comp_2223, comp_2224, comp_2445, comp_2225, comp_2226,
            comp_2362, comp_2227);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof TestFunction t && comp_2222 == t.comp_2222 && comp_2223 == t.comp_2223 && comp_2224 == t.comp_2224
            && comp_2445 == t.comp_2445 && comp_2225 == t.comp_2225 && comp_2226 == t.comp_2226 && comp_2362 == t.comp_2362
            && Objects.equals(comp_2218, t.comp_2218) && Objects.equals(comp_2219, t.comp_2219) && Objects.equals(comp_2220, t.comp_2220)
            && comp_2221 == t.comp_2221 && Objects.equals(comp_2227, t.comp_2227);
    }

    public String comp_2218() {
        return comp_2218;
    }

    public String comp_2219() {
        return comp_2219;
    }

    public String comp_2220() {
        return comp_2220;
    }

    public class_2470 comp_2221() {
        return comp_2221;
    }

    public int comp_2222() {
        return comp_2222;
    }

    public long comp_2223() {
        return comp_2223;
    }

    public boolean comp_2224() {
        return comp_2224;
    }

    public boolean comp_2445() {
        return comp_2445;
    }

    public int comp_2225() {
        return comp_2225;
    }

    public int comp_2226() {
        return comp_2226;
    }

    public boolean comp_2362() {
        return comp_2362;
    }

    public Consumer<class_4516> comp_2227() {
        return comp_2227;
    }
}
