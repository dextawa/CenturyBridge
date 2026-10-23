package top.dext.centurybridge.step.v26_1.facade;

import java.util.Objects;
import org.joml.Vector3fc;

/**
 * 1.21.11's PanoramicScreenshotParameters (net/minecraft/class_12335), a record of the forward vector a
 * panoramic screenshot keeps the camera on. 26.1 keeps that state in the Camera itself
 * (enablePanoramicMode, panoramicForwards); GameRenderer's 1.21.11 getter and setter
 * (mixin/CbWallGameRendererMixin) read and write it there. A final class with the record's members (its
 * component field public: access wideners never reach a CenturyBridge class), equal by its vector as the
 * record was. Member names are 1.21.11's intermediary names.
 */
public final class PanoramicScreenshotParameters {

    public final Vector3fc comp_5225;

    public PanoramicScreenshotParameters(Vector3fc forwardVector) {
        this.comp_5225 = forwardVector;
    }

    /** forwardVector() */
    public Vector3fc comp_5225() {
        return comp_5225;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof PanoramicScreenshotParameters p && Objects.equals(comp_5225, p.comp_5225);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(comp_5225);
    }

    @Override
    public String toString() {
        return "PanoramicScreenshotParameters[forwardVector=" + comp_5225 + "]";
    }
}
