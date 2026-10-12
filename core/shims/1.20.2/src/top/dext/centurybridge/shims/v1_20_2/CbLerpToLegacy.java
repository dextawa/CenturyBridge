package top.dext.centurybridge.shims.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyLerpTo;

/**
 * 1.20.1's client moved entities through lerpTo(x, y, z, yRot, xRot, steps, teleport): with
 * teleport set for a teleport packet (handleTeleportEntity), clear for a move packet
 * (handleMoveEntity). 1.20.2 calls lerpTo without the flag at the same places. An entity of a
 * converted class overriding the 1.20.1 method (CbLegacyLerpTo) is moved through the override
 * again, with the flag 1.20.1 passed there; every other entity is left to 1.20.2.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_634.class)
public abstract class CbLerpToLegacy {

    /** handleTeleportEntity(packet) */
    @Redirect(method = "method_11086", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1297;method_5759(DDDFFI)V"))
    private void cb$teleportLerp(class_1297 entity, double x, double y, double z, float yRot, float xRot, int steps) {
        if (entity instanceof CbLegacyLerpTo legacy) {
            legacy.method_5759(x, y, z, yRot, xRot, steps, true);
        } else {
            entity.method_5759(x, y, z, yRot, xRot, steps);
        }
    }

    /** handleMoveEntity(packet) */
    @Redirect(method = "method_11155", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1297;method_5759(DDDFFI)V"))
    private void cb$moveLerp(class_1297 entity, double x, double y, double z, float yRot, float xRot, int steps) {
        if (entity instanceof CbLegacyLerpTo legacy) {
            legacy.method_5759(x, y, z, yRot, xRot, steps, false);
        } else {
            entity.method_5759(x, y, z, yRot, xRot, steps);
        }
    }
}
