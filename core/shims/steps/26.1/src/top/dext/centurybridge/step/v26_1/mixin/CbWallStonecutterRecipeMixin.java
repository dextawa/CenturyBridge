package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipes;

/** a stonecutting recipe built by 1.21.11's constructor answers the group it was given (CbWallSingleItemRecipeMixin) */
@Mixin(StonecutterRecipe.class)
public abstract class CbWallStonecutterRecipeMixin {

    @Inject(method = "group", at = @At("HEAD"), cancellable = true)
    private void cb$group(CallbackInfoReturnable<String> cir) {
        String group = ((CbWallRecipes.SingleItemGroup) this).cb$group();
        if (group != null) {
            cir.setReturnValue(group);
        }
    }
}
