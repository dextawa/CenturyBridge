package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.20.4 SwimNodeEvaluator.isDiagonalNodeValid(diagonal, a, b), which 1.20.5 inlined into getNeighbors,
 * with 1.20.4's body: a valid diagonal node and two neighbours without malus.
 */
@Mixin(net.minecraft.class_12.class)
public abstract class CbRestSwimNodeEvaluatorMixin {

    @Shadow
    protected abstract boolean method_38487(net.minecraft.class_9 node);

    /** 1.20.4 isDiagonalNodeValid(diagonal, a, b) */
    protected boolean method_38488(net.minecraft.class_9 diagonal, net.minecraft.class_9 a, net.minecraft.class_9 b) {
        return this.method_38487(diagonal) && a != null && a.field_43 >= 0.0F && b != null && b.field_43 >= 0.0F;
    }
}
