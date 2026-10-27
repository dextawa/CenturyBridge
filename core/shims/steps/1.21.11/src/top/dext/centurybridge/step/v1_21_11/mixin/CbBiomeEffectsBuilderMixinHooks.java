package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_4763;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_11.rt.CbBiomeEffects;

/**
 * 1.21.11's BiomeSpecialEffects.Builder.build(): hands what the restored 1.21.10 setters set
 * (CbBiomeEffectsBuilderMixin) to the effects it built (CbBiomeEffects.attach). It only records beside the result and
 * changes nothing 1.21.11 returns, so it runs at the default priority among the mods' injectors.
 */
@Mixin(class_4763.class_4764.class)
public abstract class CbBiomeEffectsBuilderMixinHooks {
    @Inject(method = "method_24391", at = @At("RETURN"))
    private void cb$build(CallbackInfoReturnable<class_4763> cir) {
        CbBiomeEffects.attach(cir.getReturnValue(), ((CbBiomeEffects.Builder) this).cb$v1_21_11$settings());
    }
}
