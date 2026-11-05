package top.dext.centurybridge.step.v1_21_11.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;

/**
 * 1.21.10 ChatComponent (class_338) private statics reached through a mod's access widener (javap -c -p -constants of
 * both code jars). 1.21.11 dropped them when chat drawing moved behind ChatComponent.AlphaCalculator (class_338$class_12232).
 * - field_39771 (= -1) and field_39773 (= 4) were compile-time constants; the getters answer 1.21.10's values.
 * - getTimeFactor(age) (method_19348): 1.21.10's formula, clamp((1 - age / 200) * 10, 0, 1) squared. 1.21.11 still
 *   computes the same expression inside AlphaCalculator.timeBased (method_75805, as a float from a line's age); it has
 *   no entry point taking an age, so the double result is computed here.
 */
@Environment(EnvType.CLIENT)
public final class CbLegacyChat {

    public static int field_39771() {
        return -1;
    }

    public static int field_39773() {
        return 4;
    }

    /** getTimeFactor(age) */
    public static double method_19348(int age) {
        double d = age / 200.0;
        d = 1.0 - d;
        d *= 10.0;
        d = class_3532.method_15350(d, 0.0, 1.0);
        return d * d;
    }

    private CbLegacyChat() {
    }
}
