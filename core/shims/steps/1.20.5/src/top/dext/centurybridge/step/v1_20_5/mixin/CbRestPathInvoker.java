package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** WalkNodeEvaluator's protected raw path type of a block (CbRestPath.Context computes types uncached with it) */
@Mixin(net.minecraft.class_14.class)
public interface CbRestPathInvoker {

    @Invoker("method_58")
    static net.minecraft.class_7 cbRest$pathTypeFromState(net.minecraft.class_1922 level, net.minecraft.class_2338 pos) {
        throw new AssertionError("mixin invoker");
    }
}
