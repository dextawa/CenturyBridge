package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_3222;
import net.minecraft.class_3231;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestAttachmentSync;

/**
 * 1.21.1's Fabric API (0.116) sent a player an entity's synced attachments once the entity's pairing (spawn)
 * packets were sent (EntityTrackerEntryMixin): ServerEntity.addPairing, at its end.
 */
@Mixin(class_3231.class)
public abstract class CbRestAttachmentTrackingMixin {

    @Shadow
    @Final
    private class_1297 field_14049;

    @Inject(method = "method_18760", at = @At("TAIL"))
    private void cb$startedTracking(class_3222 player, CallbackInfo ci) {
        CbRestAttachmentSync.startedTracking(player, this.field_14049);
    }
}
