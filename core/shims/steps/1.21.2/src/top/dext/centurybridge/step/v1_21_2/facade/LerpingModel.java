package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.Map;
import org.joml.Vector3f;

/**
 * 1.21.1 LerpingModel (net/minecraft/class_6375), an entity keeping per-model-part rotation vectors for its model to
 * interpolate (Axolotl; AxolotlModel stored and read them). 1.21.2 removed it (its axolotl animates from binary
 * animators on the render state). Axolotl implements it again with 1.21.1's storage (CbEntityLerpingAxolotlMixin), so
 * instanceof, casts and calls behave as in 1.21.1.
 */
public interface LerpingModel {
    /** getModelRotationValues() */
    Map<String, Vector3f> method_36976();
}
