package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ScalableParticleOptionsBase's own field(s): 1.20.4 wrote these, not what a subclass's getter answers (rt/CbParticles super bodies) */
@Mixin(net.minecraft.class_9679.class)
public interface CbParticleScaleAccessor {

    /** scale */
    @Accessor("field_51493")
    float cbS4$scale();
}
