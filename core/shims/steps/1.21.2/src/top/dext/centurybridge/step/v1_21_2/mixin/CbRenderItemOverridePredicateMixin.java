package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.21.1 ItemOverride.Predicate.getProperty() / getValue() on 1.21.2's Predicate record (property / value components),
 * with 1.21.1's bodies over the same fields; see CbRenderItemOverrideMixin.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_799.class_5826.class)
public abstract class CbRenderItemOverridePredicateMixin {

    @Shadow
    @Final
    private net.minecraft.class_2960 comp_3195;

    @Shadow
    @Final
    private float comp_3196;

    /** 1.21.1 ItemOverride.Predicate.getProperty() */
    public net.minecraft.class_2960 method_33692() {
        return this.comp_3195;
    }

    /** 1.21.1 ItemOverride.Predicate.getValue() */
    public float method_33693() {
        return this.comp_3196;
    }
}
