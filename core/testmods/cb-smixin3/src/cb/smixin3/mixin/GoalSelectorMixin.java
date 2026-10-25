package cb.smixin3.mixin;

import cb.smixin3.Driver;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import java.util.Set;

/** 26.1.2: removeGoal's first read of availableGoals (the loop stopping the running goal) */
@Mixin(GoalSelector.class)
public abstract class GoalSelectorMixin {
    @Inject(method = "removeGoal", remap = false, at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, ordinal = 0,
        target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;availableGoals:Ljava/util/Set;"))
    private void cbsmixin3$hit(CallbackInfo ci) {
        Driver.hits++;
    }

    /** removeGoal's first read of availableGoals again, wrapped (the WrapOperation fallback: unguarded, the read happens as it is) */
    @WrapOperation(method = "removeGoal", remap = false, at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, ordinal = 0,
        target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;availableGoals:Ljava/util/Set;"))
    private Set<WrappedGoal> cbsmixin3$wrap(GoalSelector self, Operation<Set<WrappedGoal>> op) {
        Driver.wraps++;
        return op.call(self);
    }
}
