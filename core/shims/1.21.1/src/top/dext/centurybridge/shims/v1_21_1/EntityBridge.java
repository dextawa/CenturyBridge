package top.dext.centurybridge.shims.v1_21_1;

import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: Entity.getHeightOffset (ride offset) was removed in the
 * vehicle-position rework. The vanilla default was 0.0; legacy overrides keep
 * their bodies (dormant for vanilla, still callable by mod code). Positional
 * approximation, noted in the report.
 */
@Mixin(class_1297.class)
public abstract class EntityBridge {

    public double method_5678() {
        return 0.0D;
    }

    /** 1.20.1 updateTrackedPositionAndAngles lost its trailing interpolate flag */
    public void method_5759(double x, double y, double z, float yaw, float pitch, int steps, boolean interpolate) {
        ((class_1297) (Object) this).method_5759(x, y, z, yaw, pitch, steps);
    }

    /** 1.20.3 widened getScoreboardTeam's return to AbstractTeam; runtime instances are still Teams */
    public net.minecraft.class_270 method_5781() {
        return (net.minecraft.class_270) ((class_1297) (Object) this).method_5781();
    }
}
