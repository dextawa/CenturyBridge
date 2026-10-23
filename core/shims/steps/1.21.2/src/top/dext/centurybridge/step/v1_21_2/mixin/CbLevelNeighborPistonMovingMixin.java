package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelNeighbors;

/** PistonMovingBlockEntity's two neighborChanged calls: 1.21.1 told the moved block its own position (CbLevelNeighbors) */
@Mixin(net.minecraft.class_2669.class)
public abstract class CbLevelNeighborPistonMovingMixin {

    @Redirect(method = "method_11513", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1937;method_8492(Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;Lnet/minecraft/class_9904;)V"))
    private void cb$finalTickSource(net.minecraft.class_1937 level, net.minecraft.class_2338 pos, net.minecraft.class_2248 block, net.minecraft.class_9904 orientation) {
        CbLevelNeighbors.neighborChanged(level, pos, block, orientation, pos);
    }

    @Redirect(method = "method_31707", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1937;method_8492(Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;Lnet/minecraft/class_9904;)V"))
    private static void cb$tickSource(net.minecraft.class_1937 level, net.minecraft.class_2338 pos, net.minecraft.class_2248 block, net.minecraft.class_9904 orientation) {
        CbLevelNeighbors.neighborChanged(level, pos, block, orientation, pos);
    }
}
