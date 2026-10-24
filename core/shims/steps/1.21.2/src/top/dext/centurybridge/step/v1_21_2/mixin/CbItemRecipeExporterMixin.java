package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1860;
import net.minecraft.class_5321;
import net.minecraft.class_8790;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemDatagen;

/**
 * Fabric API 0.116 saved a vanilla builder's recipe under the output's FabricRecipeExporter.getRecipeIdentifier(id)
 * (its AllCraftingRecipeJsonBuilders / ComplexRecipeJsonBuilder / Smithing*RecipeJsonBuilder mixins). The same on
 * 1.21.2's builders, whose save takes the recipe key: for an output implementing the 0.116 interface (a 1.21.1 mod's),
 * the key is that of the identifier it answers for the key's location.
 */
@Mixin({net.minecraft.class_2454.class, net.minecraft.class_2447.class, net.minecraft.class_2450.class, net.minecraft.class_3981.class})
public abstract class CbItemRecipeExporterMixin {

    @ModifyVariable(method = "method_17972(Lnet/minecraft/class_8790;Lnet/minecraft/class_5321;)V", at = @At("HEAD"), argsOnly = true)
    private class_5321<class_1860<?>> cb$recipeIdentifier(class_5321<class_1860<?>> key, class_8790 output) {
        return CbItemDatagen.exportedKey(output, key);
    }
}
