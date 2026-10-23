package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestKillScore;

/**
 * LivingEntity.deathScore of 1.21.3 (CbRestKillScore), 0 by default as 1.21.3's field was; LivingEntity.die
 * awards the kill only when it is >= 0, as 1.21.3's die did.
 */
@Mixin(class_1309.class)
public abstract class CbRestDeathScoreMixin implements CbRestKillScore.DeathScore {

    @Unique
    private int cb$deathScore;

    @Override
    public int cb$deathScore() {
        return this.cb$deathScore;
    }

    @Override
    public void cb$setDeathScore(int score) {
        this.cb$deathScore = score;
    }

    @Redirect(method = "method_6078", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1309;method_5716(Lnet/minecraft/class_1297;Lnet/minecraft/class_1282;)V"))
    private void cb$awardIfScored(class_1309 killer, class_1297 killed, class_1282 source) {
        if (CbRestKillScore.awards((class_1309) (Object) this)) {
            killer.method_5716(killed, source);
        }
    }
}
