package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * 1.20.4 SwimNodeEvaluator.isDiagonalNodeValid(diagonal, a, b), which 1.20.5 inlined into getNeighbors,
 * with 1.20.4's body: a valid diagonal node and two neighbours without malus.
 */
@Mixin(net.minecraft.class_12.class)
public abstract class CbRestSwimNodeEvaluatorMixin {

    @Shadow
    protected abstract boolean method_38487(net.minecraft.class_9 node);

    @Shadow
    protected abstract net.minecraft.class_9 method_47936(int x, int y, int z);

    /** 1.20.4 isDiagonalNodeValid(diagonal, a, b) */
    @CbVanillaReads("CbRestSwimNodeEvaluatorMixin (cbRoute$getNeighbors): getNeighbors judges each diagonal through it, as 1.20.4's did, when a converted mod mixes it")
    protected boolean method_38488(net.minecraft.class_9 diagonal, net.minecraft.class_9 a, net.minecraft.class_9 b) {
        return this.method_38487(diagonal) && a != null && a.field_43 >= 0.0F && b != null && b.field_43 >= 0.0F;
    }

    /**
     * Route: 1.20.4's getNeighbors(nodes, node) -- the six neighbours as 1.20.5's, then each horizontal diagonal
     * found and judged by isDiagonalNodeValid(diagonal, a, b); 1.20.5 inlined the judgement (and finds the diagonal
     * only when both neighbours pass). With a converted mod mixing the restored member, getNeighbors runs 1.20.4's
     * body; unmixed it runs as 1.20.5 has it.
     */
    @Inject(method = "method_18", at = @At("HEAD"), cancellable = true)
    private void cbRoute$getNeighbors(net.minecraft.class_9[] nodes, net.minecraft.class_9 node, CallbackInfoReturnable<Integer> cir) {
        if (!CbRoutes.route(CbRoutes.SWIM_DIAGONAL, this)) {
            return;
        }
        int count = 0;
        java.util.Map<net.minecraft.class_2350, net.minecraft.class_9> around = new java.util.EnumMap<>(net.minecraft.class_2350.class);
        for (net.minecraft.class_2350 d : net.minecraft.class_2350.values()) {
            net.minecraft.class_9 n = this.method_47936(node.field_40 + d.method_10148(), node.field_39 + d.method_10164(),
                node.field_38 + d.method_10165());
            around.put(d, n);
            if (this.method_38487(n)) {
                nodes[count++] = n;
            }
        }
        for (net.minecraft.class_2350 d : net.minecraft.class_2350.class_2353.field_11062) {
            net.minecraft.class_2350 cw = d.method_10170();
            net.minecraft.class_9 n = this.method_47936(node.field_40 + d.method_10148() + cw.method_10148(), node.field_39,
                node.field_38 + d.method_10165() + cw.method_10165());
            if (this.method_38488(n, around.get(d), around.get(cw))) {
                nodes[count++] = n;
            }
        }
        cir.setReturnValue(count);
    }
}
