package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** DustParticleOptions's own field(s): 1.20.4 wrote these, not what a subclass's getter answers (rt/CbParticles super bodies) */
@Mixin(net.minecraft.class_2390.class)
public interface CbParticleDustAccessor {

    /** color */
    @Accessor("field_51492")
    org.joml.Vector3f cbS4$color();
}
