package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 ItemOverride.getModel() / getPredicates() on 1.21.2's ItemOverride record (model / predicates components),
 * with 1.21.1's bodies over the same fields. A 1.21.1 subclass (the record is opened by the layer's access widener, as
 * 1.21.1's class was open) overriding either is asked where 1.21.2 reads the component (overrideHooks), and its super
 * call reaches these.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_799.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderItemOverrideMixin {

    @Shadow
    @Final
    private net.minecraft.class_2960 comp_3193;

    @Shadow
    @Final
    private java.util.List<net.minecraft.class_799.class_5826> comp_3194;

    /** 1.21.1 ItemOverride.getModel() */
    public net.minecraft.class_2960 method_3472() {
        return this.comp_3193;
    }

    /** 1.21.1 ItemOverride.getPredicates() */
    public java.util.stream.Stream<net.minecraft.class_799.class_5826> method_33690() {
        return this.comp_3194.stream();
    }
}
