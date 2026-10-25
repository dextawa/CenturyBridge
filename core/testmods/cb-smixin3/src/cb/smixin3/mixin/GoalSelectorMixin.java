package cb.smixin3.mixin;

import cb.smixin3.Driver;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 26.1.2: removeGoal's first read of availableGoals (the loop stopping the running goal) */
@Mixin(GoalSelector.class)
public abstract class GoalSelectorMixin {
    @Inject(method = "removeGoal", remap = false, at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, ordinal = 0,
        target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;availableGoals:Ljava/util/Set;"))
    private void cbsmixin3$hit(CallbackInfo ci) {
        Driver.hits++;
    }
}
