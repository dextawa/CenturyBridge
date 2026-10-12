package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** DustColorTransitionOptions's own field(s): 1.20.4 wrote these, not what a subclass's getter answers (rt/CbParticles super bodies) */
@Mixin(net.minecraft.class_5743.class)
public interface CbParticleTransitionAccessor {

    /** fromColor */
    @Accessor("field_51491")
    org.joml.Vector3f cbS4$fromColor();

    /** toColor */
    @Accessor("field_28271")
    org.joml.Vector3f cbS4$toColor();
}
