package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26Bounce;

/**
 * Entity.restituteMovementAfterCollisions(state under the entity, x collided, z collided, movement) takes 26.1.2's path
 * for a block whose class overrides 26.1.2's updateEntityMovementAfterFallOn (rt/CbS26Bounce.legacy): the horizontal
 * movement zeroed on an axis that collided, the block asked on a vertical collision. Every other block keeps 26.2's
 * restitution.
 */
@Mixin(Entity.class)
public abstract class CbS26EntityFallOnMixin {

    @Inject(method = "restituteMovementAfterCollisions(Lnet/minecraft/world/level/block/state/BlockState;ZZLnet/minecraft/world/phys/Vec3;)V",
            at = @At("HEAD"), cancellable = true)
    private void cbS26$legacyFallOn(BlockState state, boolean xCollided, boolean zCollided, Vec3 movement, CallbackInfo ci) {
        Block block = state.getBlock();
        if (CbS26Bounce.legacy(block)) {
            CbS26Bounce.legacyRestitute((Entity) (Object) this, block, xCollided, zCollided);
            ci.cancel();
        }
    }
}
