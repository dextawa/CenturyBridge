package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * LivingEntity's protected makeBrain(Brain.Packed), for rt/CbWallBrain: a 1.21.11 mod's call of an
 * entity's makeBrain(Dynamic) is that, dispatched as 26.1 dispatches it.
 */
@Mixin(LivingEntity.class)
public interface CbWallLivingEntityAccess {

    @Invoker("makeBrain")
    Brain<?> cb$makeBrain(Brain.Packed packed);
}
