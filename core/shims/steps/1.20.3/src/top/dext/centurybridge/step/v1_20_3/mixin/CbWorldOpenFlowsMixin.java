package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * WorldOpenFlows' 1.20.2 entry points that 1.20.3 replaced:
 * <ul>
 * <li>loadLevel(Screen lastScreen, String levelId) (method_41894): 1.20.3's checkForBackupAndLoad(levelId,
 *     onCancel), going back to lastScreen when cancelled, as 1.20.2's did;</li>
 * <li>loadWorldStem(LevelStorageAccess, boolean safeMode) (method_41890): the level's data read from the
 *     access and its pack repository, then 1.20.3's loadWorldStem(levelData, safeMode, packs).</li>
 * </ul>
 */
@Mixin(net.minecraft.class_7196.class)
public abstract class CbWorldOpenFlowsMixin {

    @Shadow
    @Final
    private net.minecraft.class_310 field_37914;

    public void method_41894(net.minecraft.class_437 lastScreen, String levelId) {
        ((net.minecraft.class_7196) (Object) this).method_54618(levelId, () -> field_37914.method_1507(lastScreen));
    }

    public net.minecraft.class_6904 method_41890(net.minecraft.class_32.class_5143 access, boolean safeMode) throws Exception {
        net.minecraft.class_3283 packs = net.minecraft.class_3286.method_45285(access);
        return ((net.minecraft.class_7196) (Object) this).method_54610(access.method_54545(), safeMode, packs);
    }
}
