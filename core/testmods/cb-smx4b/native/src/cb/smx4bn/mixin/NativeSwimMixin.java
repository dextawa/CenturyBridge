package cb.smx4bn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.20.5 SwimNodeEvaluator.getNeighbors, counted */
@Mixin(net.minecraft.class_12.class)
public abstract class NativeSwimMixin {
    @Inject(method = "method_18([Lnet/minecraft/class_9;Lnet/minecraft/class_9;)I", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$neighbors(net.minecraft.class_9[] nodes, net.minecraft.class_9 node, CallbackInfoReturnable<Integer> cir) {
        cb.smx4bn.Driver.swimNeighbors++;
    }
}
