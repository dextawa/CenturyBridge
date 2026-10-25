package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26Bounce;

/**
 * Entity.restituteMovementAfterCollisions(state under the entity, x collided, z collided, movement) always runs as 26.2
 * wrote it, so a 26.2 mod's mixins on it still fire. For a block whose class overrides 26.1.2's
 * updateEntityMovementAfterFallOn (rt/CbS26Bounce.legacy) the block then answers the vertical movement as on 26.1.2:
 * after 26.2's restitution, on a vertical collision, the vertical movement is put back to what it was before
 * restitution and the block is asked (rt/CbS26Bounce.legacyFallOn). The horizontal movement stays 26.2's (on an axis
 * that collided 26.2 gives -x times the entity's bounciness, 0 for an entity without bounciness: 26.1.2's zeroing).
 * Every other block is untouched.
 */
@Mixin(Entity.class)
public abstract class CbS26EntityFallOnMixin {

    /** the delta movement restituteMovementAfterCollisions started from, held for the block answer at its end */
    @Unique
    private Vec3 cbS26$beforeRestitution;

    @Inject(method = "restituteMovementAfterCollisions(Lnet/minecraft/world/level/block/state/BlockState;ZZLnet/minecraft/world/phys/Vec3;)V",
            at = @At("HEAD"))
    private void cbS26$keepMovement(BlockState state, boolean xCollided, boolean zCollided, Vec3 movement, CallbackInfo ci) {
        this.cbS26$beforeRestitution = CbS26Bounce.legacy(state.getBlock()) ? ((Entity) (Object) this).getDeltaMovement() : null;
    }

    @Inject(method = "restituteMovementAfterCollisions(Lnet/minecraft/world/level/block/state/BlockState;ZZLnet/minecraft/world/phys/Vec3;)V",
            at = @At("TAIL"))
    private void cbS26$legacyFallOn(BlockState state, boolean xCollided, boolean zCollided, Vec3 movement, CallbackInfo ci) {
        Vec3 before = this.cbS26$beforeRestitution;
        if (before == null) {
            return;
        }
        this.cbS26$beforeRestitution = null;
        Block block = state.getBlock();
        if (CbS26Bounce.legacy(block)) {
            CbS26Bounce.legacyFallOn((Entity) (Object) this, block, before);
        }
    }
}
