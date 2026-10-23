package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** 1.21.1's ModelUtils (net/minecraft/class_4496, classRenames), gone in 1.21.2. 1.21.1's body. */
@Environment(EnvType.CLIENT)
public class ModelUtils {

    public ModelUtils() {
    }

    /** rotlerpRad(min, max, delta): from min towards max the short way round, in radians */
    public static float method_22114(float min, float max, float delta) {
        float d = max - min;
        while (d < -3.1415927F) {
            d += 6.2831855F;
        }
        while (d >= 3.1415927F) {
            d -= 6.2831855F;
        }
        return min + delta * d;
    }
}
