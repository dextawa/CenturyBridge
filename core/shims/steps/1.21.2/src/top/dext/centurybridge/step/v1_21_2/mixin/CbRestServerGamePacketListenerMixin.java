package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Set;
import net.minecraft.class_10182;
import net.minecraft.class_243;
import net.minecraft.class_3244;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestPackets;

/**
 * ServerGamePacketListenerImpl.teleport(x, y, z, yRot, xRot, relatives) of 1.21.1, back on 1.21.2's
 * teleport(PositionMoveRotation, relatives): 1.21.1 moved the player to the position and rotations (each
 * relative to the player's own where its constant is in the set) and sent the position packet, whose
 * client kept its movement on the relative axes and zeroed it on the absolute ones; 1.21.2's teleport does
 * both from the PositionMoveRotation (no movement of its own) and the set with the relative axes' DELTA_
 * constants added (CbRestPackets.withKeptDelta, as the 1.21.1 position packet constructor). The server's
 * copy of the player's movement, which 1.21.1 left alone, follows the same set.
 */
@Mixin(class_3244.class)
public abstract class CbRestServerGamePacketListenerMixin {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void method_14360(double x, double y, double z, float yRot, float xRot, Set relatives) {
        ((class_3244) (Object) this).method_14360(new class_10182(new class_243(x, y, z), class_243.field_1353, yRot, xRot),
            CbRestPackets.withKeptDelta(CbRestPackets.relatives(relatives)));
    }
}
