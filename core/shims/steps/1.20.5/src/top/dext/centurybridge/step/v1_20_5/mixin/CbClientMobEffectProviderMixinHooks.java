package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplitClient;

/**
 * The injector into 1.20.5's own MobEffectProvider.createParticle that goes with {@link CbClientMobEffectProviderMixin}
 * (its restored 1.20.4 createParticle stays there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.4 injectors can
 * anchor inside it): simple particle options go to the 1.20.4 shape.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_711.class_714.class)
public abstract class CbClientMobEffectProviderMixinHooks {

    @Inject(method = "method_3090", at = @At("HEAD"), cancellable = true)
    private void cb$simpleType(net.minecraft.class_2394 options, net.minecraft.class_638 level, double x, double y, double z,
                               double xd, double yd, double zd, CallbackInfoReturnable<net.minecraft.class_703> cir) {
        if (options instanceof net.minecraft.class_2400 simple) {
            cir.setReturnValue(((CbPrioritySplitClient.MobEffectParticle) (Object) this)
                .cb$v1_20_5$createParticle(simple, level, x, y, z, xd, yd, zd));
        }
    }
}
