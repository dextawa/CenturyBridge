package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1657;
import net.minecraft.class_4050;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyPose;

/**
 * 1.20.2's player pose test through 1.20.1's Entity.canEnterPose (restored by CbEntityPoseLegacy). 1.20.1's
 * Player.updatePlayerPose asked canEnterPose(pose), which tested getBoundingBoxForPose(pose) deflated by 1.0E-7;
 * 1.20.2 asks canPlayerFitWithinBlocksAndEntitiesWhen(pose) (method_52558), which builds the same box inline
 * (EntityDimensions.makeBoundingBox(position): the width centred on x / z, from y up the height). Answering it
 * from the restored canEnterPose gives the same value while nothing changes those members, and a 1.20.1 mod's
 * mixin on canEnterPose or getBoundingBoxForPose decides the player's pose again, as it did on 1.20.1. Called
 * virtually, so a converted class's own canEnterPose still decides.
 */
@Mixin(class_1657.class)
public abstract class CbEntityPoseVanillaMixin {
    @Inject(method = "method_52558(Lnet/minecraft/class_4050;)Z", at = @At("HEAD"), cancellable = true)
    private void cb$fitThroughLegacyPose(class_4050 pose, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(((CbLegacyPose) (Object) this).cb$legacyCanEnterPose(pose));
    }
}
