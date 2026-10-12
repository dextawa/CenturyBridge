package top.dext.centurybridge.shims.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.compat.fapi.impl.attachment.AttachmentTargetImpl;

/**
 * On a respawn the client's new player takes the old player's attachments -- on a death (the respawn
 * does not keep attributes) only those that copy on death -- just before the new player is initialised
 * (1.20.1's client ClientPlayNetworkHandlerMixin). 1.20.1's Fabric API read the old player out of the
 * method's locals; here it is taken when the respawn starts running on the game thread, before the
 * method replaces the client's player.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_634.class)
abstract class CbFapiClientRespawnAttachments {

    @Unique
    private net.minecraft.class_746 cb$respawningPlayer;

    @Inject(method = "method_11117", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
        target = "Lnet/minecraft/class_2600;method_11074(Lnet/minecraft/class_2596;Lnet/minecraft/class_2547;Lnet/minecraft/class_1255;)V"))
    private void cb$rememberOldPlayer(net.minecraft.class_2724 packet, CallbackInfo ci) {
        cb$respawningPlayer = net.minecraft.class_310.method_1551().field_1724;
    }

    @Inject(method = "method_11117", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_746;method_33689()V"))
    private void cb$copyAttachmentsOnClientRespawn(net.minecraft.class_2724 packet, CallbackInfo ci) {
        net.minecraft.class_746 oldPlayer = cb$respawningPlayer;
        cb$respawningPlayer = null;
        net.minecraft.class_746 newPlayer = net.minecraft.class_310.method_1551().field_1724;
        AttachmentTargetImpl.transfer((AttachmentTargetImpl) oldPlayer, (AttachmentTargetImpl) newPlayer, !packet.method_48016((byte) 1));
    }
}
