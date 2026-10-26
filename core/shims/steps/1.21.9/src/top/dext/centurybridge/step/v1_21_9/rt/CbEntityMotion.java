package top.dext.centurybridge.step.v1_21_9.rt;

import net.minecraft.class_243;

/**
 * Entity.lerpMotion: 1.21.8 took the motion as (x, y, z), 1.21.9 takes it as a Vec3 (javap: Entity's body is
 * setDeltaMovement of it in both, RemotePlayer's keeps it as its lerp target in both). A 1.21.8 call hands its three
 * values as one Vec3; a 1.21.8 override is reached by 1.21.9's call with the Vec3's components.
 */
public final class CbEntityMotion {

    private CbEntityMotion() {
    }

    /** the 1.21.8 call's (x, y, z) as the Vec3 1.21.9 takes */
    public static class_243 vec(double x, double y, double z) {
        return new class_243(x, y, z);
    }

    /** a 1.21.8 Entity.lerpMotion(x, y, z) override */
    public interface LegacyLerpMotion {
        void method_5750(double x, double y, double z);
    }

    /** 1.21.9's lerpMotion(vec) on an entity whose class overrides the 1.21.8 shape */
    public static void cbLerpMotion(LegacyLerpMotion self, class_243 motion) {
        self.method_5750(motion.field_1352, motion.field_1351, motion.field_1350);
    }
}
