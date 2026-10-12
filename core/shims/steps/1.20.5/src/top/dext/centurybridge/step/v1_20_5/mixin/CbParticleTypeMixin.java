package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.facade.ParticleOptionsDeserializer;
import top.dext.centurybridge.step.v1_20_5.rt.CbParticles;

/** ParticleType: the 1.20.4 deserializer a type made the 1.20.4 way keeps (rt/CbParticles). */
@Mixin(net.minecraft.class_2396.class)
public abstract class CbParticleTypeMixin implements CbParticles.DeserializerSlot {

    @Unique
    @SuppressWarnings("rawtypes")
    private ParticleOptionsDeserializer cbS4$deserializer;

    @Override
    @SuppressWarnings("rawtypes")
    public ParticleOptionsDeserializer cbS4$deserializer() {
        return cbS4$deserializer;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void cbS4$deserializer(ParticleOptionsDeserializer deserializer) {
        this.cbS4$deserializer = deserializer;
    }

    /** a converted super(overrideLimiter, deserializer) handed its deserializer on (CbParticles.pend) */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void cbS4$claim(boolean overrideLimiter, CallbackInfo ci) {
        CbParticles.claim((net.minecraft.class_2396<?>) (Object) this);
    }
}
