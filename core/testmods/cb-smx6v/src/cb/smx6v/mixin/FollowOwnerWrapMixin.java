package cb.smx6v.mixin;

import cb.smx6v.Driver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * A native 1.21 mod's @WrapOperation on the two TamableAnimal calls of FollowOwnerGoal that the 1.21 step layer routes
 * (CbEntityFollowOwnerRestoredMixinHooks wraps the same calls): tick's tryToTeleportToOwner (method_6268 ->
 * class_1321.method_60713) and canUse's unableToMoveToOwner (method_6264 -> class_1321.method_60715). Counts and passes on.
 */
@Mixin(net.minecraft.class_1350.class)
public abstract class FollowOwnerWrapMixin {

    @WrapOperation(method = "method_6268()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1321;method_60713()V"))
    private void cbsmx6v$teleport(net.minecraft.class_1321 animal, Operation<Void> original) {
        Driver.nativeTeleportWraps++;
        original.call(animal);
    }

    @WrapOperation(method = "method_6264()Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1321;method_60715()Z"))
    private boolean cbsmx6v$unable(net.minecraft.class_1321 animal, Operation<Boolean> original) {
        Driver.nativeUnableWraps++;
        return original.call(animal);
    }
}
