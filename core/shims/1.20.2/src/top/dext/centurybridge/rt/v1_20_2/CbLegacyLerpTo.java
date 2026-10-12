package top.dext.centurybridge.rt.v1_20_2;

/**
 * Marks a converted entity class overriding 1.20.1's lerpTo(x, y, z, yRot, xRot, steps, teleport)
 * (method_5759 with the trailing flag): 1.20.1's client packet listener interpolated entities
 * through it -- teleport packets with the flag set, move packets without -- and 1.20.2's calls the
 * flagless lerpTo. For such an entity the listener calls the override again, with the flag 1.20.1
 * passed at that place (CbLerpToLegacy).
 */
public interface CbLegacyLerpTo {

    /** 1.20.1 lerpTo(x, y, z, yRot, xRot, steps, teleport) */
    void method_5759(double x, double y, double z, float yRot, float xRot, int steps, boolean teleport);
}
