package top.dext.centurybridge.step.v1_21.facade;

import net.minecraft.class_243;

/**
 * 1.20.6's PortalInfo (class_5454): where an entity changing dimension arrives -- position, motion and the
 * two rotations -- with no level. 1.21 gave the intermediary name to DimensionTransition, a final record
 * that also carries the destination level and what happens after the move; a 1.20.6 mod's PortalInfo is
 * this class (s6world.json renames it), and the layer adds the level where it hands one to 1.21
 * (CbEntityPortals). Same members as 1.20.6's: four public final fields and the one constructor.
 */
public class PortalInfo {

    /** pos */
    public final class_243 field_25879;
    /** speed */
    public final class_243 field_25880;
    /** yRot */
    public final float field_25881;
    /** xRot */
    public final float field_25882;

    public PortalInfo(class_243 pos, class_243 speed, float yRot, float xRot) {
        this.field_25879 = pos;
        this.field_25880 = speed;
        this.field_25881 = yRot;
        this.field_25882 = xRot;
    }
}
