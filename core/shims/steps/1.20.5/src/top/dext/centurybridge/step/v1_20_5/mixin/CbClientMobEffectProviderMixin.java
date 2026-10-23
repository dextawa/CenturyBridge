package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * SpellParticle.MobEffectProvider over a SimpleParticleType: 1.20.4's provider (1.20.5's takes coloured
 * particle options). 1.20.4's createParticle(type, ...) comes back -- a spell particle with the velocity it is
 * given, that velocity as its colour -- and a provider a 1.20.4 mod registered for its own simple particle
 * type is handed such options by the particle engine, so 1.20.5's createParticle(options, ...) gives them to
 * the 1.20.4 shape instead of failing its cast.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_711.class_714.class)
public abstract class CbClientMobEffectProviderMixin {

    @Shadow
    @Final
    private net.minecraft.class_4002 field_17873;

    /** 1.20.4 MobEffectProvider.createParticle(SimpleParticleType, ...) */
    public net.minecraft.class_703 method_3098(net.minecraft.class_2400 type, net.minecraft.class_638 level, double x, double y, double z,
                                               double xd, double yd, double zd) {
        net.minecraft.class_711 particle = CbClientSpellParticleInvoker.cb$newSpell(level, x, y, z, xd, yd, zd, this.field_17873);
        particle.method_3084((float) xd, (float) yd, (float) zd);
        return particle;
    }

    @Inject(method = "method_3090", at = @At("HEAD"), cancellable = true)
    private void cb$simpleType(net.minecraft.class_2394 options, net.minecraft.class_638 level, double x, double y, double z,
                               double xd, double yd, double zd, CallbackInfoReturnable<net.minecraft.class_703> cir) {
        if (options instanceof net.minecraft.class_2400 simple) {
            cir.setReturnValue(this.method_3098(simple, level, x, y, z, xd, yd, zd));
        }
    }
}
