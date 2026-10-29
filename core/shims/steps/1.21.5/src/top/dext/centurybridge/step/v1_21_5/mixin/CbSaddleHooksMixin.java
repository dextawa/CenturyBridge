package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_1308;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_5.facade.Saddleable;
import top.dext.centurybridge.step.v1_21_5.rt.CbSaddles;

/**
 * 1.21.5 reads a mob's saddle through Mob.isSaddled() (class_1308.method_66672) where 1.21.4 vanilla called the
 * Saddleable's isSaddled() -- virtually, so a 1.21.4 subclass's override answered. For an entity whose class declares
 * 1.21.4's isSaddled below the vanilla class restoring it (a converted Pig / Strider / horse subclass, or a mod's own
 * Saddleable mob), 1.21.5's answer is replaced by that override's when the method returns; any other mob keeps 1.21.5's
 * answer untouched. 1.21.5's body and every native injector before RETURN still run.
 */
@Mixin(class_1308.class)
public abstract class CbSaddleHooksMixin {

    @Inject(method = "method_66672()Z", at = @At("RETURN"), cancellable = true)
    private void cb$v1_21_5$isSaddled(CallbackInfoReturnable<Boolean> cir) {
        Object self = this;
        if (self instanceof Saddleable s && CbSaddles.overrides(self, "method_6725")) {
            cir.setReturnValue(s.method_6725());
        }
    }

}
