package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Super calls of a mod subclass of ClientLevel.ClientLevelData into its 1.20.4 per-coordinate spawn
 * methods (see CbLevelData): 1.20.4's ClientLevelData read and wrote its own fields, which 1.20.5 folded
 * into one spawn position (mixin/CbLevelDataClientMixin carries them as CbLevelData.Vanilla).
 */
@Environment(EnvType.CLIENT)
public final class CbLevelDataClient {

    /** super.getXSpawn(): 1.20.4 read its xSpawn field */
    public static int superXSpawn(net.minecraft.class_638.class_5271 d) {
        return ((CbLevelData.Vanilla) d).cbS4$spawnPos().method_10263();
    }

    /** super.getYSpawn() */
    public static int superYSpawn(net.minecraft.class_638.class_5271 d) {
        return ((CbLevelData.Vanilla) d).cbS4$spawnPos().method_10264();
    }

    /** super.getZSpawn() */
    public static int superZSpawn(net.minecraft.class_638.class_5271 d) {
        return ((CbLevelData.Vanilla) d).cbS4$spawnPos().method_10260();
    }

    /** super.setXSpawn(x): 1.20.4 wrote its xSpawn field */
    public static void superSetXSpawn(net.minecraft.class_638.class_5271 d, int x) {
        CbLevelData.stateX((CbLevelData.Vanilla) d, x);
    }

    /** super.setYSpawn(y) */
    public static void superSetYSpawn(net.minecraft.class_638.class_5271 d, int y) {
        CbLevelData.stateY((CbLevelData.Vanilla) d, y);
    }

    /** super.setZSpawn(z) */
    public static void superSetZSpawn(net.minecraft.class_638.class_5271 d, int z) {
        CbLevelData.stateZ((CbLevelData.Vanilla) d, z);
    }

    /** super.setSpawnAngle(angle): 1.20.4 wrote its spawnAngle field */
    public static void superSetSpawnAngle(net.minecraft.class_638.class_5271 d, float angle) {
        ((CbLevelData.Vanilla) d).cbS4$spawnAngle(angle);
    }

    private CbLevelDataClient() {
    }
}
