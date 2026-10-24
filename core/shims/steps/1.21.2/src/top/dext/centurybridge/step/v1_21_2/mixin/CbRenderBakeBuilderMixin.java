package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBakeOverrides;

/**
 * SimpleBakedModel.Builder: the overrides 1.21.1's constructor took go to the model build() makes, as 1.21.1's
 * Builder passed them to its SimpleBakedModel (CbRenderBakeOverrides).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1093.class_1094.class)
public abstract class CbRenderBakeBuilderMixin implements CbRenderBakeOverrides.Holder {

    @Unique
    private net.minecraft.class_806 cb$overrides;

    @Override
    public void cb$overrides(net.minecraft.class_806 overrides) {
        this.cb$overrides = overrides;
    }

    @Inject(method = "method_4746", at = @At("RETURN"))
    private void cb$keepOverrides(CallbackInfoReturnable<net.minecraft.class_1087> cir) {
        if (this.cb$overrides != null && cir.getReturnValue() instanceof CbRenderBakeOverrides.Holder model) {
            model.cb$overrides(this.cb$overrides);
        }
    }
}
