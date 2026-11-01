package top.dext.centurybridge.step.v1_21_11.rt;

import net.minecraft.class_3532;

/**
 * 1.21.10 Mth.sin(float) / cos(float) (method_15374 / method_15362 (F)F), which 1.21.11 replaced by (D)F forms (javap -c -p
 * both). Both versions read the same 65536-entry SIN table; they differ in the index: 1.21.10 multiplies in float
 * (10430.378f) and truncates to int, 1.21.11 multiplies in double (10430.378350470453) and truncates to long. Widening the
 * float argument therefore picks a neighbouring entry for some inputs. 1.21.10's index is computed here exactly as
 * 1.21.10 did, and 1.21.11's sin is handed the centre of that entry ((i + 0.5) / K, which its truncation maps back to
 * i), so the answer is 1.21.10's table entry without opening the private table.
 */
public final class CbMth {
    private static final double K = 10430.378350470453;

    private CbMth() {
    }

    private static float entry(int i) {
        return class_3532.method_15374((i + 0.5) / K);
    }

    /** Mth.sin(float) */
    public static float method_15374(float f) {
        return entry((int) (f * 10430.378F) & 65535);
    }

    /** Mth.cos(float) */
    public static float method_15362(float f) {
        return entry((int) (f * 10430.378F + 16384.0F) & 65535);
    }
}
