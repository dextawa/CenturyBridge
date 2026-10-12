package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ShriekParticleOption's own field(s): 1.20.4 wrote these, not what a subclass's getter answers (rt/CbParticles super bodies) */
@Mixin(net.minecraft.class_7290.class)
public interface CbParticleShriekAccessor {

    /** delay */
    @Accessor("field_38360")
    int cbS4$delay();
}
