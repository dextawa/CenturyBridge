package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;

/**
 * The injectors into 1.20.5's own Entity methods that go with {@link CbRestEntityMixin} (its restored 1.20.4 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.4 injectors can anchor inside them): where 1.20.5
 * reads {@code dimensions.eyeHeight()} where 1.20.4 called getEyeHeight(pose, dimensions) (the constructor with
 * STANDING, fixupDimensions, refreshDimensions, getEyeHeight(pose)) it asks 1.20.4's getEyeHeight again, and
 * maxUpStep() answers setMaxUpStep's kept height of an entity that is not a LivingEntity.
 */
@Mixin(net.minecraft.class_1297.class)
public abstract class CbRestEntityMixinHooks {

    @Shadow
    public abstract net.minecraft.class_4050 method_18376();

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4048;comp_2187()F"))
    private float cbRest$initialEyeHeight(net.minecraft.class_4048 dimensions) {
        return ((CbPrioritySplit.EntityEyes) (Object) this).cb$v1_20_5$eyeHeight(net.minecraft.class_4050.field_18076, dimensions);
    }

    @Redirect(method = {"method_46396", "method_18382"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4048;comp_2187()F"))
    private float cbRest$eyeHeight(net.minecraft.class_4048 dimensions) {
        return ((CbPrioritySplit.EntityEyes) (Object) this).cb$v1_20_5$eyeHeight(this.method_18376(), dimensions);
    }

    @Redirect(method = "method_18381", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4048;comp_2187()F"))
    private float cbRest$eyeHeightFor(net.minecraft.class_4048 dimensions, net.minecraft.class_4050 pose) {
        return ((CbPrioritySplit.EntityEyes) (Object) this).cb$v1_20_5$eyeHeight(pose, dimensions);
    }

    @Inject(method = "method_49476", at = @At("HEAD"), cancellable = true)
    private void cbRest$maxUpStep(CallbackInfoReturnable<Float> cir) {
        float height = ((CbPrioritySplit.EntityEyes) (Object) this).cb$v1_20_5$maxUpStep();
        if (height != 0F) {
            cir.setReturnValue(height);
        }
    }
}
