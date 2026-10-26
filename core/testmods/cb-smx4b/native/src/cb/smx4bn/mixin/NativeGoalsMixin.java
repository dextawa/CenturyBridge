package cb.smx4bn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.20.5 GoalSelector.getAvailableGoals, counted */
@Mixin(net.minecraft.class_1355.class)
public abstract class NativeGoalsMixin {
    @Inject(method = "method_35115()Ljava/util/Set;", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$available(CallbackInfoReturnable<java.util.Set<net.minecraft.class_4135>> cir) {
        cb.smx4bn.Driver.availableGoals++;
    }
}
