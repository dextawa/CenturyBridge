package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelNeighbors;

/** InstantNeighborUpdater: the update runs at once, with the source the call that made it named */
@Mixin(net.minecraft.class_7164.class)
public abstract class CbLevelNeighborInstantMixin {

    @Redirect(method = "method_41703", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_7165;method_41708(Lnet/minecraft/class_1937;Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;Lnet/minecraft/class_9904;Z)V"))
    private void cb$deliver(net.minecraft.class_1937 level, net.minecraft.class_2680 state, net.minecraft.class_2338 pos, net.minecraft.class_2248 block, net.minecraft.class_9904 orientation, boolean moved) {
        CbLevelNeighbors.deliver(CbLevelNeighbors.pendingSource(), level, state, pos, block, orientation, moved);
    }
}
