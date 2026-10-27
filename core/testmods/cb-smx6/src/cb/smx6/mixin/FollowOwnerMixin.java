package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.6 mod's mixins on FollowOwnerGoal's private teleport helpers: a cancellable HEAD on canTeleportTo
 * (method_23344(BlockPos)Z), which refuses every spot while Driver.blockFollowTeleport is set, and an INVOKE-anchored
 * injector on maybeTeleportTo's move (method_23343 at Mob.moveTo, class_1321.method_5808), counting real teleports.
 */
@Mixin(net.minecraft.class_1350.class)
public abstract class FollowOwnerMixin {

    @Inject(method = "method_23344", at = @At("HEAD"), cancellable = true)
    private void cbsmx6$canTeleport(net.minecraft.class_2338 pos, CallbackInfoReturnable<Boolean> cir) {
        Driver.followCanAsked++;
        if (Driver.blockFollowTeleport) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "method_23343", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1321;method_5808(DDDFF)V"))
    private void cbsmx6$moved(int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        Driver.followMoved++;
    }
}
