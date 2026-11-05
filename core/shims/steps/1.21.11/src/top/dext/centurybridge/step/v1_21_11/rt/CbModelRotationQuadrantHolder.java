package top.dext.centurybridge.step.v1_21_11.rt;

import net.minecraft.class_10821;

/**
 * Implemented on 1.21.11's ModelRotation by mixin/CbModelRotationRestoredMixin (the restored 1.21.10 x / y Quadrant
 * fields); mixin/CbModelRotationMixinHooks fills them at the end of the constructor through it.
 */
public interface CbModelRotationQuadrantHolder {
    void cb$v1_21_11$setQuadrants(class_10821 x, class_10821 y);
}
