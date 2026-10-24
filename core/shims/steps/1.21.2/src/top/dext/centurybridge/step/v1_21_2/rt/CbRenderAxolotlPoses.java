package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * AxolotlModel's private pose helpers, 1.21.1 shape on 1.21.2 (callReshapes in s7render.json).
 *
 * 1.21.1's setupWaterHoveringAnimation(ageInTicks), setupSwimmingAnimation(limbSwing, ageInTicks) and
 * setupInitialAnimationValues()/mirror-legs helper posed the model for the one state setupAnim had picked, all the way
 * (smoothed over frames by the model's lerp). 1.21.2 blends the states: each helper takes the weight of its state
 * (AxolotlRenderState's inWaterFactor / movingFactor / ...) and adds that share of its pose, returning early below
 * 1e-5. A 1.21.1 caller of one of these helpers asks for that state's pose in full, so the weight it passes is 1.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderAxolotlPoses {
    private CbRenderAxolotlPoses() {
    }

    /** the weight of the state a 1.21.1 call asks for: the whole pose */
    public static float fullWeight() {
        return 1.0F;
    }
}
