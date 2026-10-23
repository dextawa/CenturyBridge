package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelNeighbors;

/**
 * PistonHeadBlock.neighborChanged: 1.21.1 passed on the fromPos the head itself was told (CbLevelNeighbors)
 */
@Mixin(net.minecraft.class_2671.class)
public abstract class CbLevelNeighborPistonHeadMixin {

    @Redirect(method = "method_9612", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1937;method_8492(Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;Lnet/minecraft/class_9904;)V"))
    private void cb$source(net.minecraft.class_1937 level, net.minecraft.class_2338 pos, net.minecraft.class_2248 block, net.minecraft.class_9904 orientation, net.minecraft.class_2680 s, net.minecraft.class_1937 l, net.minecraft.class_2338 headPos, net.minecraft.class_2248 b, net.minecraft.class_9904 headOrientation,
                           boolean moved) {
        CbLevelNeighbors.neighborChanged(level, pos, block, orientation, CbLevelNeighbors.fromPos(headPos, headOrientation));
    }
}
