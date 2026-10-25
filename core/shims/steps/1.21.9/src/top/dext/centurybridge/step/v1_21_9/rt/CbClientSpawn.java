package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import net.minecraft.class_5217;
import net.minecraft.class_5269;
import net.minecraft.class_638;

/** CbSpawn's calls named through ClientLevel.ClientLevelData (client-only). */
@Environment(EnvType.CLIENT)
public final class CbClientSpawn {

    private CbClientSpawn() {
    }

    public static class_2338 method_56126(class_638.class_5271 data) {
        return CbSpawn.method_56126((class_5217) data);
    }

    public static float method_30656(class_638.class_5271 data) {
        return CbSpawn.method_30656((class_5217) data);
    }

    public static void method_187(class_638.class_5271 data, class_2338 pos, float angle) {
        CbSpawn.method_187((class_5269) data, pos, angle);
    }
}
