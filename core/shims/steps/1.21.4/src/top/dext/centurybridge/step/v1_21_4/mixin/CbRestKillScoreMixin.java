package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestKillScore;

/**
 * ServerPlayer's half of 1.21.3's kill score (CbRestKillScore): awardKillScore adds the score to the
 * player's (1.21.3: increaseScore(score) for a kill other than itself; at TAIL, past 1.21.4's own early
 * return for that case), and ServerPlayer.die awards its killer only for a deathScore >= 0.
 */
@Mixin(class_3222.class)
public abstract class CbRestKillScoreMixin {

    @Inject(method = "method_5716", at = @At("TAIL"))
    private void cb$increaseScore(class_1297 killed, class_1282 source, CallbackInfo ci) {
        ((class_3222) (Object) this).method_7285(CbRestKillScore.scoreFor(killed));
    }

    @Redirect(method = "method_6078", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1309;method_5716(Lnet/minecraft/class_1297;Lnet/minecraft/class_1282;)V"))
    private void cb$awardIfScored(class_1309 killer, class_1297 killed, class_1282 source) {
        if (CbRestKillScore.awards((class_1309) (Object) this)) {
            killer.method_5716(killed, source);
        }
    }
}
