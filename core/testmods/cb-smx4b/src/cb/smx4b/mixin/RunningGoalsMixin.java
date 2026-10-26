package cb.smx4b.mixin;

import cb.smx4b.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.20.4 GoalSelector.getRunningGoals(): counted; while Driver.fakePanic is set, a running panic goal of Driver.panicMob */
@Mixin(net.minecraft.class_1355.class)
public abstract class RunningGoalsMixin {
    @Inject(method = "method_19048()Ljava/util/stream/Stream;", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbsmx4b$running(CallbackInfoReturnable<java.util.stream.Stream<net.minecraft.class_4135>> cir) {
        Driver.goalsAsked++;
        if (Driver.fakePanic) {
            cir.setReturnValue(java.util.stream.Stream.of(new net.minecraft.class_4135(1, new net.minecraft.class_1374(Driver.panicMob, 1.25))));
        }
    }
}
