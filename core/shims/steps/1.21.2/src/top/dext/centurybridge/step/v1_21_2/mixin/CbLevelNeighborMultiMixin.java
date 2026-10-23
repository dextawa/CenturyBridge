package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelNeighbors;

/** CollectingNeighborUpdater.MultiNeighborUpdate: each neighbor learns the update's source position, as 1.21.1 passed it */
@Mixin(targets = "net.minecraft.class_7159$class_7161")
public abstract class CbLevelNeighborMultiMixin {

    @Shadow
    @Final
    private net.minecraft.class_2338 field_37834;

    @Redirect(method = "method_41707", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_7165;method_41708(Lnet/minecraft/class_1937;Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;Lnet/minecraft/class_9904;Z)V"))
    private void cb$deliver(net.minecraft.class_1937 level, net.minecraft.class_2680 state, net.minecraft.class_2338 pos, net.minecraft.class_2248 block, net.minecraft.class_9904 orientation, boolean moved) {
        CbLevelNeighbors.deliver(this.field_37834, level, state, pos, block, orientation, moved);
    }
}
