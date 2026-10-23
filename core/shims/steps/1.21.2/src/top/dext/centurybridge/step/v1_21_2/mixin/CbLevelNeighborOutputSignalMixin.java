package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelNeighbors;

/** Level.updateNeighbourForOutputSignal: 1.21.1 told each comparator the changed position (CbLevelNeighbors) */
@Mixin(net.minecraft.class_1937.class)
public abstract class CbLevelNeighborOutputSignalMixin {

    @Redirect(method = "method_8455", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1937;method_41410(Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;Lnet/minecraft/class_9904;Z)V"))
    private void cb$source(net.minecraft.class_1937 level, net.minecraft.class_2680 state, net.minecraft.class_2338 pos, net.minecraft.class_2248 block, net.minecraft.class_9904 orientation, boolean moved, net.minecraft.class_2338 changedPos, net.minecraft.class_2248 b) {
        CbLevelNeighbors.neighborChanged(level, state, pos, block, orientation, moved, changedPos);
    }
}
