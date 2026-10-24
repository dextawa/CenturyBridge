package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbEntityRest;

/**
 * TamableAnimal.canFlyToOwner() answering true for an animal a 1.20.6 FollowOwnerGoal was made for with canFly =
 * true: 1.20.6's goal then let it teleport onto leaves, which 1.21 decides by canFlyToOwner (CbEntityRest).
 */
@Mixin(net.minecraft.class_1321.class)
public abstract class CbEntityTamableFlyMixin implements CbEntityRest.LegacyFlyToOwner {

    @Unique
    private boolean cb$canFlyToOwner;

    @Override
    public void cb$setCanFlyToOwner(boolean canFly) {
        this.cb$canFlyToOwner = canFly;
    }

    @Inject(method = "method_60716", at = @At("HEAD"), cancellable = true)
    private void cb$legacyCanFly(CallbackInfoReturnable<Boolean> cir) {
        if (this.cb$canFlyToOwner) {
            cir.setReturnValue(true);
        }
    }
}
