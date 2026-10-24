package top.dext.centurybridge.step.v1_20_5.facade;

import java.util.List;
import net.minecraft.class_4520;
import net.minecraft.class_4521;

/**
 * 1.20.4's GameTestRunner as a type: a holder of static methods whose public no-argument constructor made an
 * instance with no state and no instance methods. 1.20.5's GameTestRunner is a stateful runner whose
 * constructor makes itself the given ticker's runner; this one is built with no batches on a ticker of its
 * own (no ticker anything ticks), so creating one -- or a 1.20.4 subclass, whose hierarchy points here --
 * touches nothing, as in 1.20.4. The static methods are wired to rt/CbFinGameTests.
 */
public class LegacyGameTestRunner extends class_4520 {

    public LegacyGameTestRunner() {
        super(null, List.of(), null, new class_4521(), null, null);
    }
}
