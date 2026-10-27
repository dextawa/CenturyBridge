package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_5454;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * ServerPlayer members of 1.21.1 that 1.21.2 dropped, back with 1.21.1's bodies on 1.21.2's API, so a
 * 1.21.1 mod's calls link, its subclass overrides override and its super calls reach them.
 */
@Mixin(value = class_3222.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestServerPlayerMixin {

    /**
     * setPlayerInput(xxa, zza, jumping, shiftKeyDown): while riding, the strafe and forward impulses when
     * within [-1, 1], the jump flag (setJumping: the same field) and the sneak state, as 1.21.1 (1.21.2 keeps the client's Input instead).
     */
    public void method_14218(float xxa, float zza, boolean jumping, boolean shiftKeyDown) {
        class_3222 self = (class_3222) (Object) this;
        if (self.method_5765()) {
            if (xxa >= -1.0F && xxa <= 1.0F) {
                self.field_6212 = xxa;
            }
            if (zza >= -1.0F && zza <= 1.0F) {
                self.field_6250 = zza;
            }
            self.method_6100(jumping);
            self.method_5660(shiftKeyDown);
        }
    }

    /**
     * teleportTo(level, x, y, z, yRot, xRot): the camera back on the player, off any vehicle, then within the
     * level the connection's teleport and across levels a dimension change to that position with no
     * movement and no post-transition, as 1.21.1 (its DimensionTransition is 1.21.2's TeleportTransition,
     * the same six values).
     */
    public void method_14251(class_3218 level, double x, double y, double z, float yRot, float xRot) {
        class_3222 self = (class_3222) (Object) this;
        self.method_14224(self);
        self.method_5848();
        if (level == self.method_37908()) {
            self.field_13987.method_14363(x, y, z, yRot, xRot);
        } else {
            self.method_5731(new class_5454(level, new class_243(x, y, z), class_243.field_1353, yRot, xRot,
                class_5454.field_52245));
        }
    }
}
