package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbNet;

/**
 * 1.21's handleUseItem turns the player to the rotation the packet carries; a packet built the 1.20.6 way
 * (ServerboundUseItemPacket(hand, sequence), which carried none) leaves the player's rotation as the server
 * has it, as 1.20.6's handleUseItem did (CbNet.cbKeepsRotation).
 */
@Mixin(net.minecraft.class_3244.class)
public abstract class CbNetServerUseItemMixin {

    @Redirect(method = "method_12065", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3222;method_60608(FF)V"))
    private void cb$rotateForUse(net.minecraft.class_3222 player, float yRot, float xRot, net.minecraft.class_2886 packet) {
        if (!CbNet.cbKeepsRotation(packet)) {
            player.method_60608(yRot, xRot);
        }
    }
}
