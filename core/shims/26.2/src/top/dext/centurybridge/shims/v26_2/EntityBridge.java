package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: Entity.getHeightOffset (ride offset) was removed in the
 * vehicle-position rework. The vanilla default was 0.0; legacy overrides keep
 * their bodies (dormant for vanilla, still callable by mod code). Positional
 * approximation, noted in the report.
 */
@Mixin(net.minecraft.world.entity.Entity.class)
public abstract class EntityBridge {

    public double method_5678() {
        return 0.0D;
    }

    /** 1.20.1 updateTrackedPositionAndAngles lost its trailing interpolate flag;
     *  the 6-arg lerp itself died at 1.21.5 (interpolation-handler rework), so
     *  this snaps to the target state instead of lerping over {@code steps}. */
    public void method_5759(double x, double y, double z, float yaw, float pitch, int steps, boolean interpolate) {
        ((net.minecraft.world.entity.Entity) (Object) this).snapTo(x, y, z, yaw, pitch);
    }

    /** 1.20.3 widened getScoreboardTeam's return to AbstractTeam; runtime instances are still Teams */
    public net.minecraft.world.scores.Team getTeam() {
        return (net.minecraft.world.scores.Team) ((net.minecraft.world.entity.Entity) (Object) this).getTeam();
    }
}
