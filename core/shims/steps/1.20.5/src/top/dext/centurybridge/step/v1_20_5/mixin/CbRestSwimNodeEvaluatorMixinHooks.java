package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;

/**
 * The route into 1.20.5's own SwimNodeEvaluator.getNeighbors that goes with {@link CbRestSwimNodeEvaluatorMixin} (its
 * restored 1.20.4 isDiagonalNodeValid stays there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.4 injectors can
 * anchor inside it).
 */
@Mixin(net.minecraft.class_12.class)
public abstract class CbRestSwimNodeEvaluatorMixinHooks {

    @Shadow
    protected abstract boolean method_38487(net.minecraft.class_9 node);

    @Shadow
    protected abstract net.minecraft.class_9 method_47936(int x, int y, int z);

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
        CbPrioritySplit.SwimDiagonal restored = (CbPrioritySplit.SwimDiagonal) (Object) this;
        for (net.minecraft.class_2350 d : net.minecraft.class_2350.class_2353.field_11062) {
            net.minecraft.class_2350 cw = d.method_10170();
            net.minecraft.class_9 n = this.method_47936(node.field_40 + d.method_10148() + cw.method_10148(), node.field_39,
                node.field_38 + d.method_10165() + cw.method_10165());
            if (restored.cb$v1_20_5$isDiagonalNodeValid(n, around.get(d), around.get(cw))) {
                nodes[count++] = n;
            }
        }
        cir.setReturnValue(count);
    }
}
