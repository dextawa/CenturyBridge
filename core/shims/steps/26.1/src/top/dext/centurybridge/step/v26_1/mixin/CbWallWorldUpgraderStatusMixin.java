package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.worldupdate.UpgradeProgress;
import net.minecraft.util.worldupdate.WorldUpgrader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallWorldUpgraderStatus;

/**
 * 1.21.11 WorldUpgrader.status (class_1257.field_5765) written by a mod. 1.21.11's getStatus() returned the field, which
 * its upgrade loop rewrote at each stage change (AbstractUpgrader.upgrade: "upgrading" / "finished" component per data
 * type; the thread's failure handler: "failed"), so a written component showed until the next stage. 26.1's getStatus()
 * is statusTranslator.translate(upgradeProgress) (javap 26.1), whose stage is UpgradeProgress.status (setStatus, from
 * RegionStorageUpgrader.upgrade and WorldUpgrader's failure handler) and dataFixType (reset(type) per data type). The
 * written component is answered while both are what they were at the write, i.e. until 26.1 changes stage.
 */
@Mixin(WorldUpgrader.class)
public abstract class CbWallWorldUpgraderStatusMixin implements CbWallWorldUpgraderStatus {

    @Shadow
    @Final
    private UpgradeProgress upgradeProgress;

    @Unique
    private volatile Component cb$status;

    @Unique
    private volatile UpgradeProgress.Status cb$statusStage;

    @Unique
    private volatile DataFixTypes cb$statusType;

    @Override
    public void cb$setLegacyStatus(Component status) {
        this.cb$statusStage = this.upgradeProgress.getStatus();
        this.cb$statusType = this.upgradeProgress.getDataFixType();
        this.cb$status = status;
    }

    @Inject(method = "getStatus", at = @At("RETURN"), cancellable = true)
    private void cb$legacyStatus(CallbackInfoReturnable<Component> cir) {
        Component written = this.cb$status;
        if (written != null && this.upgradeProgress.getStatus() == this.cb$statusStage
                && this.upgradeProgress.getDataFixType() == this.cb$statusType) {
            cir.setReturnValue(written);
        }
    }
}
