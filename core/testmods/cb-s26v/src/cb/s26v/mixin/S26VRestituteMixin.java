package cb.s26v.mixin;

import cb.s26v.S26VNative;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** a native 26.2 mod's mixin on Entity.restituteMovementAfterCollisions: counts the calls that reach its end */
@Mixin(Entity.class)
public abstract class S26VRestituteMixin {

    @Inject(method = "restituteMovementAfterCollisions", at = @At("RETURN"))
    private void cbS26v$count(BlockState state, boolean xCollided, boolean zCollided, Vec3 movement, CallbackInfo ci) {
        S26VNative.restituteReturns++;
    }
}
