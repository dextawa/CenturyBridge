package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_243;
import net.minecraft.class_2692;
import net.minecraft.class_2735;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestPackets;

/**
 * ClientPacketListener reads ClientboundMoveVehiclePacket and ClientboundSetHeldSlotPacket the way
 * 1.21.3's did: through the getters, so a 1.21.3 mod's subclass overriding them is what the handler
 * sees (CbRestPackets).
 */
@Mixin(class_634.class)
public abstract class CbRestClientPacketsMixin {

    @Redirect(method = "method_11134", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2692;comp_3347()Lnet/minecraft/class_243;"))
    private class_243 cb$vehiclePosition(class_2692 packet) {
        return CbRestPackets.position(packet);
    }

    @Redirect(method = "method_11134", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2692;comp_3348()F"))
    private float cb$vehicleYRot(class_2692 packet) {
        return CbRestPackets.method_11675(packet);
    }

    @Redirect(method = "method_11134", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2692;comp_3349()F"))
    private float cb$vehicleXRot(class_2692 packet) {
        return CbRestPackets.method_11671(packet);
    }

    @Redirect(method = "method_11135", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2735;comp_3325()I"))
    private int cb$heldSlot(class_2735 packet) {
        return CbRestPackets.method_11803(packet);
    }
}
