package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_10584;
import net.minecraft.class_1297;
import net.minecraft.class_243;

/**
 * 1.21.4's Entity lerp members (lerpTo, lerpTargetX/Y/Z/XRot/YRot, cancelLerp) on 1.21.5, which moved client-side
 * position interpolation into InterpolationHandler (class_10584, Entity.getInterpolation(): null for an entity that
 * does not interpolate). javap -c -p 1.21.4 class_1297 / class_1309 / class_1688 / class_10255 and 1.21.5 class_1297 /
 * class_10584:
 * - lerpTo(x, y, z, yRot, xRot, steps): where 1.21.4 vanilla called lerpTo, 1.21.5 calls moveOrInterpolateTo(pos,
 *   yRot, xRot) (final): the entity's handler interpolates over the length 1.21.5 gives that entity type, else the
 *   position and rotation are set (1.21.4 Entity's own lerpTo). The step count is not used: 1.21.5's handler keeps
 *   its own length per entity (1.21.4's LivingEntity took it from the call, its boats used 10, its minecarts their
 *   behaviour's).
 * - lerpTargetX/Y/Z/XRot/YRot: the handler's position() / xRot() / yRot() (the target while interpolating, else the
 *   entity's own, as 1.21.4's overrides answered), else the entity's own (1.21.4 Entity).
 * - cancelLerp: the handler's cancel() (1.21.4's overrides set lerpSteps to 0); nothing for an entity without one.
 */
public final class CbLerp {
    private CbLerp() {
    }

    /** lerpTo(x, y, z, yRot, xRot, steps) */
    public static void lerpTo(class_1297 e, double x, double y, double z, float yRot, float xRot, int steps) {
        e.method_66246(new class_243(x, y, z), yRot, xRot);
    }

    /** lerpTargetX() */
    public static double lerpTargetX(class_1297 e) {
        class_10584 h = e.method_66233();
        return h != null ? h.method_66265().field_1352 : e.method_23317();
    }

    /** lerpTargetY() */
    public static double lerpTargetY(class_1297 e) {
        class_10584 h = e.method_66233();
        return h != null ? h.method_66265().field_1351 : e.method_23318();
    }

    /** lerpTargetZ() */
    public static double lerpTargetZ(class_1297 e) {
        class_10584 h = e.method_66233();
        return h != null ? h.method_66265().field_1350 : e.method_23321();
    }

    /** lerpTargetXRot() */
    public static float lerpTargetXRot(class_1297 e) {
        class_10584 h = e.method_66233();
        return h != null ? h.method_66269() : e.method_36455();
    }

    /** lerpTargetYRot() */
    public static float lerpTargetYRot(class_1297 e) {
        class_10584 h = e.method_66233();
        return h != null ? h.method_66268() : e.method_36454();
    }

    /** cancelLerp() */
    public static void cancelLerp(class_1297 e) {
        class_10584 h = e.method_66233();
        if (h != null) {
            h.method_66272();
        }
    }
}
