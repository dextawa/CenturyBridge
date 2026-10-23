package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelNeighbors;

/**
 * CollectingNeighborUpdater's single updates (SimpleNeighborUpdate, FullNeighborUpdate): each remembers the source
 * the update was made for (the call that made it named it, CbLevelNeighbors) and hands it on when it runs.
 */
@Mixin(targets = {"net.minecraft.class_7159$class_7163", "net.minecraft.class_7159$class_7160"})
public abstract class CbLevelNeighborRecordMixin implements CbLevelNeighbors.Sourced {

    @Unique
    private net.minecraft.class_2338 cb$source;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$recordSource(CallbackInfo ci) {
        this.cb$source = CbLevelNeighbors.pendingSource();
    }

    @Override
    public net.minecraft.class_2338 cb$source() {
        return this.cb$source;
    }

    @Redirect(method = "method_41707", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_7165;method_41708(Lnet/minecraft/class_1937;Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;Lnet/minecraft/class_9904;Z)V"))
    private void cb$deliver(net.minecraft.class_1937 level, net.minecraft.class_2680 state, net.minecraft.class_2338 pos, net.minecraft.class_2248 block, net.minecraft.class_9904 orientation, boolean moved) {
        CbLevelNeighbors.deliver(this.cb$source, level, state, pos, block, orientation, moved);
    }
}
