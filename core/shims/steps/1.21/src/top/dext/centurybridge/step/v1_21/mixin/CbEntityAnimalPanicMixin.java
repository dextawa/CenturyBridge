package top.dext.centurybridge.step.v1_21.mixin;

import java.util.function.Predicate;

import net.minecraft.class_1314;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbEntityRest;

/**
 * AnimalPanic made by a 1.20.6 caller with its own should-panic predicate (new AnimalPanic(speed, predicate)):
 * shouldPanic answers 1.20.6's way, from the predicate (CbEntityRest.shouldPanic); one made 1.21's way keeps
 * 1.21's answer from the damage that hurt the mob.
 */
@Mixin(net.minecraft.class_6028.class)
public abstract class CbEntityAnimalPanicMixin implements CbEntityRest.LegacyPanic {

    @Unique
    private Predicate<class_1314> cb$predicate;

    @Override
    public void cb$setPanicPredicate(Predicate<class_1314> predicate) {
        this.cb$predicate = predicate;
    }

    @Inject(method = "method_49487", at = @At("HEAD"), cancellable = true)
    private void cb$legacyShouldPanic(class_3218 level, class_1314 mob, CallbackInfoReturnable<Boolean> cir) {
        if (this.cb$predicate != null) {
            cir.setReturnValue(CbEntityRest.shouldPanic(this.cb$predicate, mob));
        }
    }
}
