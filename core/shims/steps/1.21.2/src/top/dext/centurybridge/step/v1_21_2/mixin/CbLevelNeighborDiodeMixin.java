package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelNeighbors;

/**
 * DiodeBlock.updateNeighborsInFront: 1.21.1 passed the diode's position as the neighbor's fromPos (CbLevelNeighbors)
 */
@Mixin(net.minecraft.class_2312.class)
public abstract class CbLevelNeighborDiodeMixin {

    @Redirect(method = "method_9997", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1937;method_8492(Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;Lnet/minecraft/class_9904;)V"))
    private void cb$source(net.minecraft.class_1937 level, net.minecraft.class_2338 pos, net.minecraft.class_2248 block, net.minecraft.class_9904 orientation, net.minecraft.class_1937 l, net.minecraft.class_2338 diodePos, net.minecraft.class_2680 state) {
        CbLevelNeighbors.neighborChanged(level, pos, block, orientation, diodePos);
    }
}
