package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbLevelData;

/**
 * ClientLevel.ClientLevelData's own spawn state, as 1.20.4's per-coordinate methods read and wrote it
 * (its xSpawn / ySpawn / zSpawn / spawnAngle fields; 1.20.5 keeps spawnPos and spawnAngle): see
 * CbLevelData.Vanilla.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_638.class_5271.class)
public abstract class CbLevelDataClientMixin implements CbLevelData.Vanilla {

    /** spawnPos */
    @Shadow
    private net.minecraft.class_2338 field_48402;

    /** spawnAngle */
    @Shadow
    private float field_26372;

    @Override
    public net.minecraft.class_2338 cbS4$spawnPos() {
        return field_48402;
    }

    @Override
    public void cbS4$spawnPos(net.minecraft.class_2338 pos) {
        field_48402 = pos.method_10062();
    }

    @Override
    public void cbS4$spawnAngle(float angle) {
        field_26372 = angle;
    }
}
