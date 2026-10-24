package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2535;
import net.minecraft.class_2596;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeNet;

/**
 * Connection.send(packet, listener, flush), the one path every send takes: a 1.21.1 packet 1.21.2 has no type for
 * (ClientboundRecipePacket) goes out as the 1.21.2 packet(s) saying the same (CbRestRecipeNet.Outgoing).
 */
@Mixin(class_2535.class)
public abstract class CbRestOutgoingPacketMixin {

    @ModifyVariable(method = "method_52906", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private class_2596<?> cb$outgoing(class_2596<?> packet) {
        return CbRestRecipeNet.outgoing(packet);
    }
}
