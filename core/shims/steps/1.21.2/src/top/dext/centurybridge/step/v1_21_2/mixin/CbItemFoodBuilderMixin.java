package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.List;
import java.util.Optional;
import net.minecraft.class_1799;
import net.minecraft.class_4174;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.facade.FoodPossibleEffect;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemFood;

/**
 * FoodProperties.Builder keeps 1.21.1's eat time, remainder and possible effects (fast / usingConvertsTo / effect,
 * CbItemFood), and build() attaches them to the food it makes.
 */
@Mixin(class_4174.class_4175.class)
public abstract class CbItemFoodBuilderMixin implements CbItemFood.BuilderExtras {

    // no field initializers: unset reads as 1.21.1's defaults (1.6 s, nothing, no effects)
    @Unique
    private Float cb$eatSeconds;

    @Unique
    private Optional<class_1799> cb$convertsTo;

    @Unique
    private List<FoodPossibleEffect> cb$effects;

    @Inject(method = "method_19242", at = @At("RETURN"), cancellable = true)
    private void cb$build(CallbackInfoReturnable<class_4174> cir) {
        cir.setReturnValue(CbItemFood.built((class_4174.class_4175) (Object) this, cir.getReturnValue()));
    }

    @Override
    public float cb$eatSeconds() {
        return cb$eatSeconds != null ? cb$eatSeconds : CbItemFood.DEFAULT_EAT_SECONDS;
    }

    @Override
    public void cb$setEatSeconds(float seconds) {
        this.cb$eatSeconds = seconds;
    }

    @Override
    public Optional<class_1799> cb$convertsTo() {
        return cb$convertsTo != null ? cb$convertsTo : Optional.empty();
    }

    @Override
    public void cb$setConvertsTo(Optional<class_1799> convertsTo) {
        this.cb$convertsTo = convertsTo;
    }

    @Override
    public List<FoodPossibleEffect> cb$effects() {
        if (cb$effects == null) {
            cb$effects = CbItemFood.newEffects();
        }
        return cb$effects;
    }
}
