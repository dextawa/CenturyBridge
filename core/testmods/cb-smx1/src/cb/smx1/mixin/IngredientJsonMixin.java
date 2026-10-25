package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's Ingredient.fromJson(json, canBeEmpty) (method_8102): every call counted at HEAD.
 */
@Mixin(net.minecraft.class_1856.class)
public abstract class IngredientJsonMixin {
    @Inject(method = "method_8102(Lcom/google/gson/JsonElement;Z)Lnet/minecraft/class_1856;", at = @At("HEAD"), remap = false)
    private static void cbsmx1$countIngredient(com.google.gson.JsonElement json, boolean canBeEmpty,
                                               CallbackInfoReturnable<net.minecraft.class_1856> cir) {
        Driver.count("ingredient-from-json-all");
    }
}
