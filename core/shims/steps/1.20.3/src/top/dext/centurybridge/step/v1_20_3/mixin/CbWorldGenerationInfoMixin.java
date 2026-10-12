package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;

/**
 * WorldGenerationInfo's 1.20.2 getters -- getSeed(), getLevelType(), shouldGenerateStructures() -- on
 * 1.20.3's record (seed, level type, generate structures, experiments), whose accessors for the first
 * three are comp_1976, comp_1977 and comp_1978. Each getter forwards to its accessor virtually: a 1.20.2
 * subclass's override of the getter is renamed to the accessor (overrideRenames), so it answers either
 * call, as it answered the getter in 1.20.2.
 */
@Mixin(net.minecraft.class_4413.class)
public abstract class CbWorldGenerationInfoMixin {

    private net.minecraft.class_4413 cb$self() {
        return (net.minecraft.class_4413) (Object) this;
    }

    public String method_32508() {
        return cb$self().comp_1976();
    }

    public net.minecraft.class_5672 method_32509() {
        return cb$self().comp_1977();
    }

    public boolean method_32510() {
        return cb$self().comp_1978();
    }
}
