package top.dext.centurybridge.step.v1_21.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** The client-only half of {@link CbNet}: what a 1.20.6 packet built on the client reads off the client. */
@Environment(EnvType.CLIENT)
public final class CbNetClient {

    private CbNetClient() {
    }

    /** the local player's yaw, what 1.21's own client puts in a ServerboundUseItemPacket (0 without a player) */
    public static float cbLocalYRot() {
        net.minecraft.class_746 p = net.minecraft.class_310.method_1551().field_1724;
        return p == null ? 0.0F : p.method_36454();
    }

    /** the local player's pitch, see {@link #cbLocalYRot} */
    public static float cbLocalXRot() {
        net.minecraft.class_746 p = net.minecraft.class_310.method_1551().field_1724;
        return p == null ? 0.0F : p.method_36455();
    }

    /** the client level's entity of that id, or null */
    public static net.minecraft.class_1297 cbClientEntity(int id) {
        net.minecraft.class_638 level = net.minecraft.class_310.method_1551().field_1687;
        return level == null ? null : level.method_8469(id);
    }
}
