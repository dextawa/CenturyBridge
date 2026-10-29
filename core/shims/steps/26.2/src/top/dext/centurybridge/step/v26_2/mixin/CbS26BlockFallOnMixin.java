package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_2.rt.CbS26Bounce;

/**
 * 26.1.2's Block.updateEntityMovementAfterFallOn, restored with 26.1.2's body (rt/CbS26Bounce). Priority CbVanillaReads.PRIORITY, below every
 * mod's, so a mod's injector may anchor anywhere in the restored body.
 */
@Mixin(value = Block.class, priority = top.dext.centurybridge.step.v26_2.rt.CbVanillaReads.PRIORITY)
public abstract class CbS26BlockFallOnMixin implements CbS26Bounce.FallOn {

    @Override
    public void updateEntityMovementAfterFallOn(BlockGetter level, Entity entity) {
        CbS26Bounce.stop(entity);
    }
}
