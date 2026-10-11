package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2788;
import net.minecraft.class_2960;
import net.minecraft.class_8786;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbRecipes;

/**
 * The recipe-sync packet reads each recipe as (serializer id, recipe id,
 * serializer.read(buf)). A 1.20.1 serializer's read(id, buf) needs the recipe id:
 * publish the second identifier the reader stores to CbRecipes.CURRENT_ID.
 */
@Mixin(class_2788.class)
public abstract class RecipeSyncIdBridge {

    @ModifyVariable(method = "method_17817", at = @At("STORE"), ordinal = 1)
    private static class_2960 cb$recipeId(class_2960 id) {
        CbRecipes.CURRENT_ID.set(id);
        return id;
    }

    @Inject(method = "method_17817", at = @At("RETURN"))
    private static void cb$exit(net.minecraft.class_2540 buf, CallbackInfoReturnable<class_8786<?>> cir) {
        CbRecipes.CURRENT_ID.remove();
    }
}
