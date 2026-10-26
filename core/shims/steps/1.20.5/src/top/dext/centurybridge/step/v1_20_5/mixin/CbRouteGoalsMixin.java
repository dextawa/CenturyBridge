package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;

/**
 * Route for 1.20.4's GoalSelector.getRunningGoals() (restored by CbEntityGoalSelectorMixin): 1.20.4's
 * PathfinderMob.isPanicking, when the brain has no panic memory, asked it whether any running goal is a PanicGoal;
 * 1.20.5's walks getAvailableGoals for a running PanicGoal (the same answer). With a converted mod mixing the
 * restored member, the goal part asks it; unmixed, and the brain part always, run as 1.20.5 has them. Extends Mob
 * for the inherited protected goalSelector.
 */
@Mixin(value = net.minecraft.class_1314.class, priority = CbRoutes.ROUTE_PRIORITY)
public abstract class CbRouteGoalsMixin extends net.minecraft.class_1308 {

    protected CbRouteGoalsMixin(net.minecraft.class_1299<? extends net.minecraft.class_1308> type, net.minecraft.class_1937 level) {
        super(type, level);
    }

    @Inject(method = "method_52546", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1355;method_35115()Ljava/util/Set;"),
        cancellable = true)
    private void cbRoute$isPanicking(CallbackInfoReturnable<Boolean> cir) {
        if (CbRoutes.route(CbRoutes.GOALS_RUNNING, this)) {
            cir.setReturnValue(((CbRoutes.RunningGoals) this.field_6201).method_19048()
                .anyMatch(goal -> goal.method_19058() instanceof net.minecraft.class_1374));
        }
    }
}
