package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BedBlock;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_2.rt.CbS26Bounce;

/**
 * 26.1.2's BedBlock.updateEntityMovementAfterFallOn, restored with 26.1.2's body (rt/CbS26Bounce): Block's (the
 * vertical movement stopped) when the entity suppresses bouncing, else bounceUp at 0.66. Priority 1, below every
 * mod's.
 */
@Mixin(value = BedBlock.class, priority = 1)
public abstract class CbS26BedFallOnMixin {

    public void updateEntityMovementAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce()) {
            CbS26Bounce.stop(entity);
        } else {
            CbS26Bounce.bounceUp(entity, 0.66F);
        }
    }
}
