package top.dext.centurybridge.step.v26_2.rt;

import com.ibm.icu.util.MeasureUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * ICU 77.1 (26.1.2) -> 78.3 (26.2), a client-only library: MeasureUnit.PORTION_PER_1E9 ("concentr", "portion-per-1e9") is
 * PART_PER_1E9 in 78.3 -- CLDR renamed the unit; the same concentration (one part per 10^9), the constant that replaces it.
 */
@Environment(EnvType.CLIENT)
public final class CbLibIcu {
    private CbLibIcu() {
    }

    /** MeasureUnit.PORTION_PER_1E9 (also read through its subclasses Currency and TimeUnit) */
    public static MeasureUnit PORTION_PER_1E9() {
        return MeasureUnit.PART_PER_1E9;
    }
}
