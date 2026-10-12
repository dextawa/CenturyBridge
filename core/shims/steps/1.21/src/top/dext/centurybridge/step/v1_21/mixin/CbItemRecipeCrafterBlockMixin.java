package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbItemRecipes;

/** CrafterBlock.dispenseFrom crafts from the crafter's grid; the input goes through CbItemRecipes (see there) */
@Mixin(net.minecraft.class_8886.class)
public abstract class CbItemRecipeCrafterBlockMixin {

    @Redirect(method = "method_54478", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_8887;method_59961()Lnet/minecraft/class_9694;"))
    private net.minecraft.class_9694 cbItem$dispenseInput(net.minecraft.class_8887 crafter) {
        return CbItemRecipes.cbCraftingInput(crafter);
    }
}
