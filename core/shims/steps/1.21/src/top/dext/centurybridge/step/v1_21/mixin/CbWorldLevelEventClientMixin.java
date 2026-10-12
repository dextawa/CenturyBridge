package top.dext.centurybridge.step.v1_21.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_638;
import net.minecraft.class_761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbWorldConstants;

/**
 * Level event 1003 (1.20.6 LevelEvent.SOUND_ENDER_EYE_LAUNCH). 1.21 dropped it -- the ender eye plays its
 * sound itself -- and LevelRenderer.levelEvent has no case for it any more (the only id of 1.20.6's 79 that
 * is gone). A 1.20.6 mod's levelEvent(1003, pos, 0) is played as 1.20.6's renderer played it: ENDER_EYE_LAUNCH,
 * NEUTRAL, volume 1, pitch 1.2, at the position.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_761.class)
public abstract class CbWorldLevelEventClientMixin {

    @Shadow
    private class_638 field_4085;

    @Inject(method = "method_8567(ILnet/minecraft/class_2338;I)V", at = @At("HEAD"), cancellable = true)
    private void cb$enderEyeLaunch(int type, class_2338 pos, int data, CallbackInfo ci) {
        if (type == CbWorldConstants.SOUND_ENDER_EYE_LAUNCH) {
            this.field_4085.method_45446(pos, class_3417.field_15155, class_3419.field_15254, 1.0F, 1.2F, false);
            ci.cancel();
        }
    }
}
