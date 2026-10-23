package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetGame;

/**
 * MobEffectInstance.BlendState: each time it is ticked or set outright, whether its factor has reached its
 * target, kept on the instance (CbNetEffectBlendSlotMixin) -- what a 1.20.4 ClientboundUpdateMobEffectPacket
 * built from the instance turns into 1.20.5's blend flag (rt/CbNetGame.mobEffectBlend).
 */
@Mixin(targets = "net.minecraft.class_1293$class_9063")
public abstract class CbNetEffectBlendMixin {

    @Shadow
    private float field_47740;

    @Shadow
    private static float method_55662(net.minecraft.class_1293 instance) {
        throw new AssertionError();
    }

    /** tick(instance) / setImmediate(instance) */
    @Inject(method = {"method_55661", "method_55659"}, at = @At("RETURN"))
    private void cbS4net$settled(net.minecraft.class_1293 instance, CallbackInfo ci) {
        CbNetGame.blendSettled(instance, this.field_47740 == method_55662(instance));
    }
}
