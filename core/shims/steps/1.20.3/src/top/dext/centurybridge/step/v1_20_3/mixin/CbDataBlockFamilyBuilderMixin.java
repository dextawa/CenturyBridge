package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.rt.CbDataRecipes;

/**
 * BlockFamily.Builder's 1.20.2 featureLockedBehind(flags) back on the class (a cover), the 1.20.2
 * body: the family's required features become exactly those flags (CbDataBlockFamilyMixin holds them).
 */
@Mixin(net.minecraft.class_5794.class_5795.class)
public abstract class CbDataBlockFamilyBuilderMixin {

    @Shadow
    @Final
    private net.minecraft.class_5794 field_28532;

    /** featureLockedBehind(flags) */
    public net.minecraft.class_5794.class_5795 method_45964(net.minecraft.class_7696... flags) {
        ((CbDataRecipes.FamilyFeatures) field_28532).cb$setRequiredFeatures(net.minecraft.class_7701.field_40180.method_45390(flags));
        return (net.minecraft.class_5794.class_5795) (Object) this;
    }
}
