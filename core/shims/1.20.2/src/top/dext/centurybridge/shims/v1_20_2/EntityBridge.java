package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: updateTrackedPositionAndAngles lost its trailing interpolate flag.
 * (The riding offsets that used to live here moved to CbEntityRiding, which answers
 * 1.20.1's per-class formulas and reconnects converted overrides to 1.20.2's riding.)
 */
@Mixin(class_1297.class)
public abstract class EntityBridge {

    /** 1.20.1 updateTrackedPositionAndAngles lost its trailing interpolate flag */
    public void method_5759(double x, double y, double z, float yaw, float pitch, int steps, boolean interpolate) {
        ((class_1297) (Object) this).method_5759(x, y, z, yaw, pitch, steps);
    }
}
