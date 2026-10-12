package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** BlockParticleOption's own field(s): 1.20.4 wrote these, not what a subclass's getter answers (rt/CbParticles super bodies) */
@Mixin(net.minecraft.class_2388.class)
public interface CbParticleBlockAccessor {

    /** state */
    @Accessor("field_11182")
    net.minecraft.class_2680 cbS4$state();
}
