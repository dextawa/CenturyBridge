package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1860;
import net.minecraft.class_5321;
import net.minecraft.class_8790;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemDatagen;

/** As CbItemRecipeExporterMixin, for net.minecraft.class_8074's save (Fabric API 0.116 modified its id the same way). */
@Mixin(net.minecraft.class_8074.class)
public abstract class CbItemRecipeExporterTransformMixin {

    @ModifyVariable(method = "method_48537(Lnet/minecraft/class_8790;Lnet/minecraft/class_5321;)V", at = @At("HEAD"), argsOnly = true)
    private class_5321<class_1860<?>> cb$recipeIdentifier(class_5321<class_1860<?>> key, class_8790 output) {
        return CbItemDatagen.exportedKey(output, key);
    }
}
