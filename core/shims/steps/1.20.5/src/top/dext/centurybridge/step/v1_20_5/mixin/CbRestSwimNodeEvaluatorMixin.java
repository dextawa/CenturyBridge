package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * 1.20.4 SwimNodeEvaluator.isDiagonalNodeValid(diagonal, a, b), which 1.20.5 inlined into getNeighbors,
 * with 1.20.4's body: a valid diagonal node and two neighbours without malus (the getNeighbors route:
 * CbRestSwimNodeEvaluatorMixinHooks, default priority).
 */
@Mixin(value = net.minecraft.class_12.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestSwimNodeEvaluatorMixin implements CbPrioritySplit.SwimDiagonal {

    @Shadow
    protected abstract boolean method_38487(net.minecraft.class_9 node);

    /** 1.20.4 isDiagonalNodeValid(diagonal, a, b) */
    @CbVanillaReads("CbRestSwimNodeEvaluatorMixinHooks (cbRoute$getNeighbors): getNeighbors judges each diagonal through it, as 1.20.4's did, when a converted mod mixes it")
    protected boolean method_38488(net.minecraft.class_9 diagonal, net.minecraft.class_9 a, net.minecraft.class_9 b) {
        return this.method_38487(diagonal) && a != null && a.field_43 >= 0.0F && b != null && b.field_43 >= 0.0F;
    }

    /** 1.20.4's isDiagonalNodeValid called virtually, for the getNeighbors route (CbRestSwimNodeEvaluatorMixinHooks) */
    @Override
    public boolean cb$v1_20_5$isDiagonalNodeValid(net.minecraft.class_9 diagonal, net.minecraft.class_9 a, net.minecraft.class_9 b) {
        return this.method_38488(diagonal, a, b);
    }
}
