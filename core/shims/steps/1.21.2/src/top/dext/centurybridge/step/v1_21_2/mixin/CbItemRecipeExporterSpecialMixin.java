package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1860;
import net.minecraft.class_5321;
import net.minecraft.class_8790;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemDatagen;

/** As CbItemRecipeExporterMixin, for net.minecraft.class_2456's save (Fabric API 0.116 modified its id the same way). */
@Mixin(net.minecraft.class_2456.class)
public abstract class CbItemRecipeExporterSpecialMixin {

    @ModifyVariable(method = "method_10475(Lnet/minecraft/class_8790;Lnet/minecraft/class_5321;)V", at = @At("HEAD"), argsOnly = true)
    private class_5321<class_1860<?>> cb$recipeIdentifier(class_5321<class_1860<?>> key, class_8790 output) {
        return CbItemDatagen.exportedKey(output, key);
    }
}
