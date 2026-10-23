package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelNeighbors;

/**
 * IceBlock.melt: 1.21.1 told the melted position itself (fromPos = pos) (CbLevelNeighbors)
 */
@Mixin(net.minecraft.class_2386.class)
public abstract class CbLevelNeighborIceMixin {

    @Redirect(method = "method_10275", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1937;method_8492(Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;Lnet/minecraft/class_9904;)V"))
    private void cb$source(net.minecraft.class_1937 level, net.minecraft.class_2338 pos, net.minecraft.class_2248 block, net.minecraft.class_9904 orientation, net.minecraft.class_2680 state, net.minecraft.class_1937 l, net.minecraft.class_2338 icePos) {
        CbLevelNeighbors.neighborChanged(level, pos, block, orientation, icePos);
    }
}
