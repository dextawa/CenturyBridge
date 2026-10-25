package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlimeBlock;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_2.rt.CbS26Bounce;

/**
 * 26.1.2's SlimeBlock.updateEntityMovementAfterFallOn, restored with 26.1.2's body (rt/CbS26Bounce): Block's (the
 * vertical movement stopped) when the entity suppresses bouncing, else bounceUp. Priority 1, below every mod's.
 */
@Mixin(value = SlimeBlock.class, priority = 1)
public abstract class CbS26SlimeFallOnMixin {

    public void updateEntityMovementAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce()) {
            CbS26Bounce.stop(entity);
        } else {
            CbS26Bounce.bounceUp(entity, 1.0);
        }
    }
}
