package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.Set;
import java.util.stream.Stream;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * GoalSelector: 1.20.4's getRunningGoals() and setNewGoalRate(rate). 1.20.5 dropped both: the first is the
 * available goals that are running, as 1.20.4 answered it; 1.20.4 stored the second in a field nothing read
 * (its tick did not consult it), and so does this.
 */
@Mixin(net.minecraft.class_1355.class)
public abstract class CbEntityGoalSelectorMixin implements CbRoutes.RunningGoals {

    @Shadow
    @Final
    private Set<net.minecraft.class_4135> field_6461;

    @Unique
    private int cbS4$newGoalRate = 3;

    /** was getRunningGoals() */
    @Override
    @CbVanillaReads("CbRouteGoalsMixin: PathfinderMob.isPanicking asks it for a running panic goal, as 1.20.4's did, when a converted mod mixes it")
    public Stream<net.minecraft.class_4135> method_19048() {
        return this.field_6461.stream().filter(net.minecraft.class_4135::method_19056);
    }

    /** was setNewGoalRate(rate) */
    public void method_35114(int rate) {
        this.cbS4$newGoalRate = rate;
    }
}
