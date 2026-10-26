package top.dext.centurybridge.step.v1_21_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Facade for 1.21.4's com/mojang/blaze3d/platform/GlStateManager$class_1040 (GlStateManager.Viewport), which 1.21.5
 * dropped: the one constant INSTANCE holding the last viewport GlStateManager._viewport set (x, y, width, height; 1.21.4's
 * _viewport wrote the four fields before glViewport, javap). 1.21.5's _viewport only calls glViewport, and every
 * viewport 1.21.5 sets goes through it (its command encoder class_10860 calls GlStateManager._viewport; no other 1.21.5
 * class calls glViewport -- constant pools scanned), so CbGlViewportMixin records each call here at its head and the
 * getters answer what 1.21.4's did. The four fields were protected in 1.21.4; they are public here because access
 * wideners never reach a CenturyBridge class (wiring-check), so a mod's access widened onto them still links.
 */
@Environment(EnvType.CLIENT)
public enum GlStateManager$class_1040 {
    INSTANCE;

    /** 1.21.4's intermediary name of INSTANCE */
    public static final GlStateManager$class_1040 field_5169 = INSTANCE;

    /** x */
    public int field_5172;
    /** y */
    public int field_5171;
    /** width */
    public int field_5170;
    /** height */
    public int field_5168;

    /** was Viewport.x() */
    public static int method_35330() {
        return INSTANCE.field_5172;
    }

    /** was Viewport.y() */
    public static int method_35331() {
        return INSTANCE.field_5171;
    }

    /** was Viewport.width() */
    public static int method_35332() {
        return INSTANCE.field_5170;
    }

    /** was Viewport.height() */
    public static int method_35333() {
        return INSTANCE.field_5168;
    }

    /** CbGlViewportMixin: 1.21.5's _viewport(x, y, width, height), as 1.21.4's recorded it */
    public static void cb$record(int x, int y, int width, int height) {
        INSTANCE.field_5172 = x;
        INSTANCE.field_5171 = y;
        INSTANCE.field_5170 = width;
        INSTANCE.field_5168 = height;
    }
}
