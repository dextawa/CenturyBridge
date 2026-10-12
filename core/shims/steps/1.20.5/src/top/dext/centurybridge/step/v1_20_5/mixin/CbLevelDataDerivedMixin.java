package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbLevelData;

/**
 * DerivedLevelData's spawn, as 1.20.4's per-coordinate methods had it: read from the wrapped data, every
 * write ignored (1.20.4's setXSpawn / setYSpawn / setZSpawn / setSpawnAngle bodies were empty, as is
 * 1.20.5's setSpawn): see CbLevelData.Vanilla.
 */
@Mixin(net.minecraft.class_27.class)
public abstract class CbLevelDataDerivedMixin implements CbLevelData.Vanilla {

    /** wrapped */
    @Shadow
    @Final
    private net.minecraft.class_5268 field_139;

    @Override
    public net.minecraft.class_2338 cbS4$spawnPos() {
        return field_139.method_56126();
    }

    @Override
    public void cbS4$spawnPos(net.minecraft.class_2338 pos) {
        // 1.20.4's DerivedLevelData ignored spawn writes
    }

    @Override
    public void cbS4$spawnAngle(float angle) {
        // 1.20.4's DerivedLevelData ignored spawn writes
    }
}
