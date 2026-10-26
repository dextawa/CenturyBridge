package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_3.rt.CbDataRecipes;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * BlockFamily's 1.20.2 feature lock back on the class: the family's required features (1.20.3
 * dropped the field -- blocks carry their own), VANILLA_SET unless BlockFamily.Builder
 * .featureLockedBehind set them, and the 1.20.2 shouldGenerateRecipe(enabled) over them (a cover:
 * generate recipes for the family only when it asks for them and its features are all enabled).
 */
@Mixin(value = net.minecraft.class_5794.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbDataBlockFamilyMixin implements CbDataRecipes.FamilyFeatures {

    /** generateRecipe */
    @Shadow
    boolean field_28529;

    @Unique
    private net.minecraft.class_7699 cb$requiredFeatures = net.minecraft.class_7701.field_40182;

    @Override
    public void cb$setRequiredFeatures(net.minecraft.class_7699 features) {
        this.cb$requiredFeatures = features;
    }

    /** shouldGenerateRecipe(enabledFeatures) */
    public boolean method_33478(net.minecraft.class_7699 enabledFeatures) {
        return field_28529 && cb$requiredFeatures.method_45400(enabledFeatures);
    }
}
