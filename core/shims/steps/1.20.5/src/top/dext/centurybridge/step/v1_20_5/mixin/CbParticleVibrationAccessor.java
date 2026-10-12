package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** VibrationParticleOption's own field(s): 1.20.4 wrote these, not what a subclass's getter answers (rt/CbParticles super bodies) */
@Mixin(net.minecraft.class_5745.class)
public interface CbParticleVibrationAccessor {

    /** destination */
    @Accessor("field_38361")
    net.minecraft.class_5716 cbS4$destination();

    /** arrivalInTicks */
    @Accessor("field_38362")
    int cbS4$arrivalInTicks();
}
