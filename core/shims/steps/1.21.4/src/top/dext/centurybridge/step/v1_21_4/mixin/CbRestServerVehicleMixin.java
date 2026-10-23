package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_243;
import net.minecraft.class_2833;
import net.minecraft.class_3244;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestPackets;

/**
 * ServerGamePacketListenerImpl.handleMoveVehicle reads the packet the way 1.21.3's did: through
 * getX/getY/getZ/getYRot/getXRot, so a 1.21.3 mod's subclass overriding them is what the server sees
 * (CbRestPackets). onGround is 1.21.4's own component and is read as is.
 */
@Mixin(class_3244.class)
public abstract class CbRestServerVehicleMixin {

    @Redirect(method = "method_12078", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2833;comp_3350()Lnet/minecraft/class_243;"))
    private class_243 cb$vehiclePosition(class_2833 packet) {
        return CbRestPackets.position(packet);
    }

    @Redirect(method = "method_12078", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2833;comp_3351()F"))
    private float cb$vehicleYRot(class_2833 packet) {
        return CbRestPackets.method_12281(packet);
    }

    @Redirect(method = "method_12078", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2833;comp_3352()F"))
    private float cb$vehicleXRot(class_2833 packet) {
        return CbRestPackets.method_12277(packet);
    }
}
