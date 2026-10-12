package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_3218;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEntityPortals;

/**
 * ServerPlayer.teleportTo(level, x, y, z, yRot, xRot): 1.20.6 added a player arriving this way with
 * addDuringCommandTeleport; 1.21 moves it through changeDimension like a portal traveller. The teleport is marked
 * while it runs, so a 1.20.6 ServerLevel subclass's addDuringCommandTeleport answers it (CbEntityPortals).
 */
@Mixin(class_3222.class)
public abstract class CbEntityPortalCommandMixin {

    @Unique
    private class_3222 cb$outerTeleport;

    @Inject(method = "method_14251(Lnet/minecraft/class_3218;DDDFF)V", at = @At("HEAD"))
    private void cb$beginCommandTeleport(class_3218 level, double x, double y, double z, float yRot, float xRot, CallbackInfo ci) {
        this.cb$outerTeleport = CbEntityPortals.beginCommandTeleport((class_3222) (Object) this);
    }

    @Inject(method = "method_14251(Lnet/minecraft/class_3218;DDDFF)V", at = @At("RETURN"))
    private void cb$endCommandTeleport(class_3218 level, double x, double y, double z, float yRot, float xRot, CallbackInfo ci) {
        CbEntityPortals.endCommandTeleport(this.cb$outerTeleport);
        this.cb$outerTeleport = null;
    }
}
