package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelNeighbors;

/**
 * CactusBlock.randomTick: 1.21.1 told the new cactus above it came from the cactus's position (CbLevelNeighbors)
 */
@Mixin(net.minecraft.class_2266.class)
public abstract class CbLevelNeighborCactusMixin {

    @Redirect(method = "method_9514", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3218;method_41410(Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;Lnet/minecraft/class_9904;Z)V"))
    private void cb$source(net.minecraft.class_3218 level, net.minecraft.class_2680 state, net.minecraft.class_2338 pos, net.minecraft.class_2248 block, net.minecraft.class_9904 orientation, boolean moved,
                           net.minecraft.class_2680 s, net.minecraft.class_3218 l, net.minecraft.class_2338 cactusPos, net.minecraft.class_5819 random) {
        CbLevelNeighbors.neighborChanged(level, state, pos, block, orientation, moved, cactusPos);
    }
}
