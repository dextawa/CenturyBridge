package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetGame;

/**
 * MobEffectInstance: whether its blend state has settled (CbNetEffectBlendMixin keeps it current; a new
 * instance has not, and copyBlendState(other) takes the other's along with its state).
 */
@Mixin(net.minecraft.class_1293.class)
public abstract class CbNetEffectBlendSlotMixin implements CbNetGame.BlendSlot {

    @Unique
    private boolean cbS4net$settled;

    @Override
    public boolean cbS4net$blendSettled() {
        return this.cbS4net$settled;
    }

    @Override
    public void cbS4net$blendSettled(boolean settled) {
        this.cbS4net$settled = settled;
    }

    /** copyBlendState(other) */
    @Inject(method = "method_55656", at = @At("RETURN"))
    private void cbS4net$copied(net.minecraft.class_1293 other, CallbackInfo ci) {
        this.cbS4net$settled = ((CbNetGame.BlendSlot) (Object) other).cbS4net$blendSettled();
    }
}
