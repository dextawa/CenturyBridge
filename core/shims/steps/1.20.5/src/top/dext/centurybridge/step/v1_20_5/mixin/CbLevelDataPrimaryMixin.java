package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbLevelData;

/**
 * PrimaryLevelData's own spawn state, as 1.20.4's per-coordinate methods read and wrote it (its xSpawn /
 * ySpawn / zSpawn / spawnAngle fields; 1.20.5 keeps spawnPos and spawnAngle): see CbLevelData.Vanilla.
 */
@Mixin(net.minecraft.class_31.class)
public abstract class CbLevelDataPrimaryMixin implements CbLevelData.Vanilla {

    /** spawnPos */
    @Shadow
    private net.minecraft.class_2338 field_48380;

    /** spawnAngle */
    @Shadow
    private float field_26367;

    @Override
    public net.minecraft.class_2338 cbS4$spawnPos() {
        return field_48380;
    }

    @Override
    public void cbS4$spawnPos(net.minecraft.class_2338 pos) {
        field_48380 = pos.method_10062();
    }

    @Override
    public void cbS4$spawnAngle(float angle) {
        field_26367 = angle;
    }
}
