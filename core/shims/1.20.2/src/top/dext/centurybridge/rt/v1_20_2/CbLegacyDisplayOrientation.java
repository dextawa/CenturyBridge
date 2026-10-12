package top.dext.centurybridge.rt.v1_20_2;

/**
 * Marks a converted Display class that overrides 1.20.1's orientation() (method_48863): 1.20.1's
 * DisplayRenderer drew a FIXED-billboard display with display.orientation(); 1.20.2's renderer
 * computes the rotation itself, so for such a display it takes the override's answer again
 * (client mixin CbDisplayRendererOrientation).
 */
public interface CbLegacyDisplayOrientation {

    /** 1.20.1 Display.orientation() */
    org.joml.Quaternionf method_48863();
}
