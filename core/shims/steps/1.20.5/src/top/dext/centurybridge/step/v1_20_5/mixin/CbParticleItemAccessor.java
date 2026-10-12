package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ItemParticleOption's own field(s): 1.20.4 wrote these, not what a subclass's getter answers (rt/CbParticles super bodies) */
@Mixin(net.minecraft.class_2392.class)
public interface CbParticleItemAccessor {

    /** itemStack */
    @Accessor("field_11192")
    net.minecraft.class_1799 cbS4$itemStack();
}
